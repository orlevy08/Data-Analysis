package bgu.projects.dataanalysis.common;

import java.io.Serializable;

/**
 * Class to represent an image from the MNIST data set
 */
public class MNISTImage implements Serializable{
    public static final int ROWS = 28;
    public static final int COLUMNS = 28;
    public static final int SIZE = ROWS*COLUMNS;
    private Byte[] arr;

    public MNISTImage(Byte [] arr) {
        if (arr.length == SIZE)
            this.arr = arr;
        else
            throw new IllegalArgumentException("MNISTImage: Illegal image size");
    }

    /**
     * Method to return the pixel value at a given coordinates
     * @param x - row coordinate
     * @param y - column coordinate
     * @return pixel value
     */
    public Integer getPixelValue(int x, int y){
        if(x >= 0 && x < ROWS && y >= 0 && y < COLUMNS)
            return (arr[x*COLUMNS+y] & 0xff);
        throw new IllegalArgumentException("Coordinates exceeded image boundaries");
    }
}
