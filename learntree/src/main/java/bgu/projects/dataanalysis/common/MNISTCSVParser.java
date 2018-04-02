package bgu.projects.dataanalysis.common;

import bgu.projects.dataanalysis.learntree.api.DataSetEntry;
import com.google.common.primitives.UnsignedBytes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

/**
 * class to parse a CSV file of MNIST data set entries
 */
public class MNISTCSVParser {

    /**
     * this method parses the CSV file
     * @return a list of MNIST data set entries
     */
    public static List<DataSetEntry<MNISTImage>> parseCSV(String filename) throws IOException, ParseException{
        List<DataSetEntry<MNISTImage>> dataSet = new ArrayList<>();
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] data = line.split(",");
                if (data.length != MNISTImage.SIZE+1)
                    throw new ParseException("The file could not be parsed", -1);
                String label = data[0];
                Byte[] dataByte = Arrays.stream(data)
                        .skip(1)
                        .map(UnsignedBytes::parseUnsignedByte)
                        .toArray(Byte[]::new);
                dataSet.add(new DataSetEntry<>(label, new MNISTImage(dataByte)));
            }
        } catch (NumberFormatException e){
            throw new ParseException(e.getMessage(), -1);
        }
        return dataSet;
    }
}
