package bgu.projects.dataanalysis.learntree.api;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Class to hold a tuple of 2 elements
 * @param <T1> - The first element class type
 * @param <T2> - The second element class type
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public class Tuple2<T1,T2> implements Serializable{

    private final T1 t1;
    private final T2 t2;
}
