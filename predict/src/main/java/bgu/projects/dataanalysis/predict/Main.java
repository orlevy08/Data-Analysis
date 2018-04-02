package bgu.projects.dataanalysis.predict;

import bgu.projects.dataanalysis.common.DecisionTreeFileReaderWriter;
import bgu.projects.dataanalysis.common.MNISTCSVParser;
import bgu.projects.dataanalysis.common.MNISTImage;
import bgu.projects.dataanalysis.learntree.api.DataSet;
import bgu.projects.dataanalysis.learntree.api.DataSetEntry;
import bgu.projects.dataanalysis.learntree.api.DataSetImpl;
import bgu.projects.dataanalysis.learntree.api.DecisionTree;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        String treeFilename = args[0];
        String testSetFilename = args[1];

        //Read Tree from File
        try {
            DecisionTree<MNISTImage> returnedTree = DecisionTreeFileReaderWriter.read(treeFilename);
            List<DataSetEntry<MNISTImage>> dataSetAsList = MNISTCSVParser.parseCSV(testSetFilename);
            DataSet<MNISTImage> testDataSet = new DataSetImpl<>(dataSetAsList);
            for (DataSetEntry<MNISTImage> entry : testDataSet) {
                System.out.println(returnedTree.predict(entry.getObject()));
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (ParseException pe) {
            pe.printStackTrace();
        }

    }
}
