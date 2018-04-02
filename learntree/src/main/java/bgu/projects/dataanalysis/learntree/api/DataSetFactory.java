package bgu.projects.dataanalysis.learntree.api;

import java.util.Collection;

/**
 * Interface to implement the "Factory Method" design pattern for {@link DataSet}
 */
@FunctionalInterface
public interface DataSetFactory<T> {

    /**
     * Method to return a new {@link DataSet} instance
     * @param dataSet - a collection of data set entries
     * @return New instance of {@link DataSet} wrapping the collection
     */
    DataSet<T> newInstance(Collection<DataSetEntry<T>> dataSet);
}
