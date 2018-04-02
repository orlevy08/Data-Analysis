package bgu.projects.dataanalysis.learntree.api;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * Interface to represent a data set used by {@link DecisionTree}
 * @param <T> - the class type
 */
public interface DataSet<T> extends Iterable<DataSetEntry<T>>, Serializable {

    /**
     * Method to return a map of all different labels
     * and their number of occurrences in this data set
     * @return map of <label,occurrences> pairs
     */
    Map<String, Long> getLabelCounts();

    /**
     * Method to return the most common label in this data set
     * @return most common label
     */
    String getMaxOccurrencesLabel();

    /**
     * Method to return a set of all different labels in this data set
     * @return set of all labels
     */
    Set<String> getAllLabels();

    /**
     * Method to immutably split this data set into 2 data sets according to a given percentage
     * @param percentage - An integer with values between 0 and 100
     * @return A tuple of 2 splits of this data set such that:
     * The first contains {@param percentage} percent of this data set entries
     * The second contains the difference of this data set's entries and the first split's entries
     */
    Tuple2<DataSet<T>, DataSet<T>> splitByPercentage(int percentage);

    /**
     * Method to return the size of this data set
     * @return size of this data set
     */
    int size();
}
