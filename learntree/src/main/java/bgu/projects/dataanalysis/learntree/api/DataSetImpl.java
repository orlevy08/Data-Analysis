package bgu.projects.dataanalysis.learntree.api;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Immutable class that implements {@link DataSet}
 * @param <T> - The class type
 */
public class DataSetImpl<T> implements DataSet<T> {

    private List<DataSetEntry<T>> dataSet;
    private Set<String> labels;

    public DataSetImpl(Collection<DataSetEntry<T>> dataSet) {
        this.dataSet = new ArrayList<>(dataSet);
        this.labels = dataSet.parallelStream()
                .map(DataSetEntry::getLabel)
                .collect(Collectors.toSet());
    }

    @Override
    public Map<String, Long> getLabelCounts() {
        return this.dataSet.parallelStream()
                .map(DataSetEntry::getLabel)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    @Override
    public String getMaxOccurrencesLabel() {
        return getLabelCounts()
                .entrySet()
                .stream()
                .max(Comparator.comparing(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    @Override
    public Set<String> getAllLabels() {
        return Collections.unmodifiableSet(this.labels);
    }

    @Override
    public Tuple2<DataSet<T>, DataSet<T>> splitByPercentage(int percentage){
        if(percentage < 0 || percentage > 100)
            throw new IllegalArgumentException("Percentage: not a legal value");
        int numOfElements = (percentage * size()) / 100;
        List<DataSetEntry<T>> firstSplit = new ArrayList<>();
        List<DataSetEntry<T>> secondSplit = new ArrayList<>(this.dataSet);
        for (int i = 0; i < numOfElements; i++) {
            int randIdx = (int)(Math.random() * secondSplit.size());
            firstSplit.add(secondSplit.remove(randIdx));
        }
        return new Tuple2<>(new DataSetImpl<>(firstSplit), new DataSetImpl<>(secondSplit));
    }

    @Override
    public int size() {
        return this.dataSet.size();
    }

    @Override
    public Iterator<DataSetEntry<T>> iterator() {
        return this.dataSet.iterator();
    }
}
