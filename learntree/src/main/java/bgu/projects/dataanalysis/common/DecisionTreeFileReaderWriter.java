package bgu.projects.dataanalysis.common;

import bgu.projects.dataanalysis.learntree.api.DecisionTree;

import java.io.*;

/**
 * Utility class for reading/writing {@link DecisionTree} from/to file
 */
public class DecisionTreeFileReaderWriter {

    /**
     * Function to write a given {@link DecisionTree} to a file
     * @param outputFilename - the output file path
     * @param tree - the tree to export
     * @param <T> - the class type
     * @throws IOException
     */
    public static <T> void write(String outputFilename, DecisionTree<T> tree) throws IOException{
        try(FileOutputStream outStream = new FileOutputStream(outputFilename);
                ObjectOutputStream objectStream = new ObjectOutputStream(outStream)) {
            objectStream.writeObject(tree);
        }
    }

    /**
     * Function to read a {@link DecisionTree} from a given file
     * @param inputFilename - the input file path
     * @param <T> - the class type
     * @return {@link DecisionTree} written in the file
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static <T> DecisionTree<T> read(String inputFilename) throws IOException, ClassNotFoundException{
        try(FileInputStream inStream = new FileInputStream(inputFilename);
            ObjectInputStream objectStream = new ObjectInputStream(inStream)) {
            return (DecisionTree<T>)objectStream.readObject();
        }
    }
}
