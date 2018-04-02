package bgu.projects.dataanalysis.learntree;

import bgu.projects.dataanalysis.common.MNISTCSVParser;
import bgu.projects.dataanalysis.common.DecisionTreeFileReaderWriter;
import bgu.projects.dataanalysis.common.MNISTImage;
import bgu.projects.dataanalysis.learntree.api.*;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import static java.lang.System.exit;

/**
 * main class for creating a decision tree to predict the value of an image
 */
public class Main {

    /**
     * main method to create the decision tree
     * @param args - configuration for the tree
     */
    public static void main(String[] args){

        try {
            int version = Integer.parseInt(args[0]);
            int percentage = Integer.parseInt(args[1]);
            int maxPow = Integer.parseInt(args[2]);
            validationCheck(version, percentage, maxPow);
            String trainingSetFilename = args[3];
            String outputTreeFilename = args[4];
            List<DataSetEntry<MNISTImage>> trainingDataSet = MNISTCSVParser.parseCSV(trainingSetFilename);
            DataSet<MNISTImage> dataSet = new DataSetImpl<>(trainingDataSet);
            Tuple2<DataSet<MNISTImage>, DataSet<MNISTImage>> splits = dataSet.splitByPercentage(percentage);
            DataSet<MNISTImage> validationSample = splits.getT1();
            DataSet<MNISTImage> trainingSample = splits.getT2();
            int[] extractTreeIterations = new int[maxPow+1];
            for(int i = 0; i < extractTreeIterations.length; i++)
                extractTreeIterations[i] = (int)(Math.pow(2, i));
            List<DecisionTree<MNISTImage>> learnTrees = DecisionTree.buildTree(
                    getFeaturesByVersion(version),
                    trainingSample,
                    col->new DataSetImpl<>(col),
                    extractTreeIterations);
            int minError = 100;
            int bestTreeSize = -1;
            for(int i=0; i < learnTrees.size() ; i++) {
                int error = calcError(learnTrees.get(i), validationSample);
                if(error < minError) {
                    minError = error;
                    bestTreeSize = extractTreeIterations[i];
                }
            }
            System.out.println("num: " + trainingSample.size());
            System.out.println("error: " + minError);
            System.out.println("size: " + bestTreeSize);
            List<DecisionTree<MNISTImage>> finalLearnTree = DecisionTree.buildTree(
                    getFeaturesByVersion(version),
                    dataSet,
                    col->new DataSetImpl<>(col),
                    new int[] {bestTreeSize});
            DecisionTreeFileReaderWriter.write(outputTreeFilename, finalLearnTree.get(0));

        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            printErrorMessage();
        } catch (ParseException pe){
            System.err.println("Illegal training-set format");
            exit(-1);
        } catch (IOException ioe) {
            System.err.println("Could not open file '" + args[3] + "'");
            exit(-1);
        }
    }

    /**
     *
     * @param version
     * @return
     */
    private static Collection<Predicate<MNISTImage>> getFeaturesByVersion(int version) {
        Collection<Predicate<MNISTImage>> features = new ArrayList<>();
        if (version == 1){
            for (int i = 0; i < MNISTImage.ROWS; i++) {
                int x = i;
                for (int j = 0; j < MNISTImage.COLUMNS; j++) {
                    int y = j;
                    features.add((Predicate<MNISTImage>&Serializable) img -> img.getPixelValue(x,y) > 128);
                }
            }
        }
        else {
            //TODO: complete the second version
        }
        return features;
    }

    /**
     *
     * @param tree
     * @param validationSet
     * @return
     */
    private static int calcError(DecisionTree<MNISTImage> tree, DataSet<MNISTImage> validationSet){
        int size = validationSet.size();
        int errorCount = 0;
        for(DataSetEntry<MNISTImage> entry : validationSet){
            if(!(tree.predict(entry.getObject()).equals(entry.getLabel())))
                errorCount++;
        }
        return (int)((((double)errorCount)/size)*100+0.5);
    }

    /**
     *
     * @param version
     * @param precentage
     */
    private static void validationCheck(int version, double precentage, int maxPow){
        if ((version != 1 && version != 2) || (precentage < 0 || precentage > 100) || maxPow < 0)
            printErrorMessage();
    }

    /**
     *
     */
    private static void printErrorMessage(){
        System.err.println("Invalid input arguments\n" +
                "usage: learntree <1/2> <P> <L> <trainingset_filename> <outputtree_filename>");
        exit(-1);
    }
}
