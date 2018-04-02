package bgu.projects.dataanalysis.learntree.api;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Class to hold a tuple of 3 elements
 * @param <T1> - The first element class type
 * @param <T2> - The second element class type
 * @param <T3> - The third element class type
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Tuple3<T1,T2,T3> extends Tuple2<T1,T2> {

    private final T3 t3;

    public Tuple3(T1 t1, T2 t2, T3 t3) {
        super(t1,t2);
        this.t3 = t3;
    }
}
