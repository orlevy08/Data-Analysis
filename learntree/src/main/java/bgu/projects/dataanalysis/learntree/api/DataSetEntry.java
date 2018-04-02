package bgu.projects.dataanalysis.learntree.api;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Object of {@link DataSet} entry
 * This object is immutable
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class DataSetEntry<T> implements Serializable {

    private final String label;
    private final T object;

}
