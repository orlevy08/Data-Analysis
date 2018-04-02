package bgu.projects.dataanalysis.learntree.api;

import lombok.NonNull;

import java.io.Serializable;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;

/**
 * Class that implements 'ID3' algorithm to build a decision tree classifier
 * @param <T> - The class type
 */
public class DecisionTree<T> implements Serializable{

    private DecisionTreeNode<T> root;

    /**
     * Interface to represent a node of {@link DecisionTree}
     * @param <T> - The class type
     */
    private interface DecisionTreeNode<T> extends Serializable {

        /**
         * Method to return Whether this node is a leaf or not
         * @return true if this node is a leaf and false otherwise
         */
        boolean isLeaf();

        /**
         * Method to return a copy of this node
         * @return a copy of this node
         */
        DecisionTreeNode<T> copy();
    }

    /**
     * Class that implements {@link DecisionTreeNode}
     * This node object is an inner node of the tree
     * @param <T> - The class type
     */
    private class FeatureNode<T> implements DecisionTreeNode<T> {
        Predicate<T> feature;
        DecisionTreeNode<T> parent;
        DecisionTreeNode<T> right;
        DecisionTreeNode<T> left;

        @Override
        public boolean isLeaf(){
            return false;
        }

        @Override
        public DecisionTreeNode<T> copy() {
            FeatureNode<T> copy = new FeatureNode<>();
            copy.feature = this.feature;
            copy.left = this.left.copy();
            copy.right = this.right.copy();
            return copy;
        }
    }

    /**
     * Class that implements {@link DecisionTreeNode}
     * This node object is a leaf of the tree
     * @param <T> - The class type
     */
    private class LabelNode<T> implements DecisionTreeNode<T> {
        String label;
        DecisionTreeNode<T> parent;
        List<Predicate<T>> features;
        DataSet<T> dataSet;

        @Override
        public boolean isLeaf(){
            return true;
        }

        @Override
        public DecisionTreeNode<T> copy() {
            LabelNode<T> copy = new LabelNode<>();
            copy.label = this.label;
            return copy;
        }
    }

    private DecisionTree() {root = null;}

    /**
     * This method predicts the label of the given object
     * @param toPredict - the object to predict it's matching label
     * @return the label matching the given object
     */
    public String predict(T toPredict){
        if (root == null)
            return null;
        DecisionTreeNode current = root;
        while (!current.isLeaf()){
            if(((FeatureNode<T>)current).feature.test(toPredict))
                current = ((FeatureNode<T>)current).right;
            else
                current = ((FeatureNode<T>)current).left;
        }
        return ((LabelNode<T>)current).label;
    }

    /**
     * Function to build decision tree object according to 'ID3' algorithm
     * using a training data set of <label,object> pairs,
     * a set of features and maximum tree size
     * @param features - A collection of predicate objects which have a boolean testing method
     * @param trainingDataSet - The training data set to learn
     * @param extractTreeIterations - Array of iteration numbers in which snapshots
     *                              of the currently built tree will be returned
     * @return List of decision tree snapshots
     */
    public static <T> List<DecisionTree<T>> buildTree(@NonNull Collection<Predicate<T>> features,
                @NonNull DataSet<T> trainingDataSet, @NonNull DataSetFactory<T> factory, int[] extractTreeIterations) {
        Arrays.sort(extractTreeIterations);
        int numOfIterations = extractTreeIterations[extractTreeIterations.length-1];
        DecisionTree<T> learnTree = new DecisionTree<>();
        DecisionTree<T>.LabelNode<T> node = learnTree.new LabelNode<T>();
        node.label = trainingDataSet.getMaxOccurrencesLabel();
        node.dataSet = trainingDataSet;
        node.features = new ArrayList<>(features);
        node.parent = null;
        learnTree.root = node;
        Set<DecisionTree<T>.LabelNode<T>> leafSet = new HashSet<>();
        leafSet.add(node);
        Set<Tuple3<DecisionTree<T>.LabelNode<T>,DecisionTree<T>.FeatureNode<T>,Double>> calculatedLeaves =
                new HashSet<>();
        List<DecisionTree<T>> returnedTrees = new ArrayList<>();
        int index = 0;
        for(int i = 1; i <= numOfIterations; i++){
            for (DecisionTree<T>.LabelNode<T> leaf : leafSet)
                calculatedLeaves.add(learnTree.maxInformationGain(leaf, factory));
            leafSet.clear();
            Tuple3<DecisionTree<T>.LabelNode<T>,DecisionTree<T>.FeatureNode<T>,Double> maxLeaf =
                    calculatedLeaves.parallelStream()
                    .max(Comparator.comparing(Tuple3::getT3))
                    .get();
            DecisionTree<T>.LabelNode<T> leafToReplace = maxLeaf.getT1();
            DecisionTree<T>.FeatureNode<T> replacementFeature = maxLeaf.getT2();
            Double maxInformationGain = maxLeaf.getT3();
            //TODO: What are we doing when max information gain is 0?!
            if (maxInformationGain == 0.0)
                break;
            DecisionTreeNode<T> parent = leafToReplace.parent;
            replacementFeature.parent = parent;
            if (parent != null){
                DecisionTree<T>.FeatureNode<T> featureParent = (DecisionTree<T>.FeatureNode<T>) parent;
                if (leafToReplace == featureParent.left)
                    featureParent.left = replacementFeature;
                else
                    featureParent.right = replacementFeature;
            }
            else //First swap
                learnTree.root = replacementFeature;

            leafSet.add((DecisionTree<T>.LabelNode<T>)replacementFeature.left);
            leafSet.add((DecisionTree<T>.LabelNode<T>)replacementFeature.right);
            calculatedLeaves.remove(maxLeaf);
            if (extractTreeIterations[index] == i) {
                returnedTrees.add(learnTree.copy());
                index++;
            }
        }

        return returnedTrees;
    }

    /**
     * Method that calculates entropy value for a given leaf
     * @param t - leaf
     * @return Entropy of t (H(L))
     */
    private Double calcEntropy(LabelNode<T> t){
        Integer dataSetSize = t.dataSet.size(); //N(L)
        Map<String, Long> labelCounts = t.dataSet.getLabelCounts();
        Double entropy = 0.0;
        for(Map.Entry<String, Long> entry : labelCounts.entrySet()) {
            entropy += ((((double)entry.getValue())/dataSetSize) *
                    Math.log(((double)dataSetSize)/entry.getValue()));
        }
        return entropy;
    }

    /**
     * Method that calculates the maximal information gain (over all possible features) for a given leaf
     * @param t - leaf
     * @return A tuple of the leaf, a replacement sub-tree and information gain of the replacement
     */
    private Tuple3<LabelNode<T>,FeatureNode<T>,Double> maxInformationGain(LabelNode<T> t, DataSetFactory<T> factory) {
        Double entropy = calcEntropy(t);
        Double minWeightedEntropy = Double.MAX_VALUE;
        Predicate<T> bestFeature = null;
        LabelNode resultRight = null;
        LabelNode resultLeft = null;
        Integer dataSetSize = t.dataSet.size(); //N(L)
        for(Predicate<T> feature : t.features){
            List<DataSetEntry<T>> listA = new ArrayList<>();
            List<DataSetEntry<T>> listB = new ArrayList<>();
            for (DataSetEntry<T> entry : t.dataSet) {
                if(feature.test(entry.getObject()))
                    listA.add(entry); //Yes
                else
                    listB.add(entry); //No
            }
            LabelNode<T> la = new LabelNode<>();
            la.dataSet = factory.newInstance(listA);
            LabelNode<T> lb = new LabelNode<>();
            lb.dataSet = factory.newInstance(listB);
            Double laEntropy =  calcEntropy(la);
            Double lbEntropy =  calcEntropy(lb);
            Integer laDataSize = la.dataSet.size();
            Integer lbDataSize = lb.dataSet.size();
            Double result = (((double)laDataSize)/dataSetSize)*laEntropy +
                    (((double)lbDataSize)/dataSetSize)*lbEntropy;
            if(result < minWeightedEntropy) {
                minWeightedEntropy = result;
                bestFeature = feature;
                resultRight = la;
                resultLeft = lb;
            }
        }
        FeatureNode<T> featureNode = new FeatureNode<>();
        featureNode.feature = bestFeature;
        featureNode.right = resultRight;
        featureNode.left = resultLeft;
        resultRight.features = new ArrayList<>(t.features);
        resultRight.features.remove(bestFeature);
        resultRight.label = resultRight.dataSet.getMaxOccurrencesLabel();
        resultRight.parent = featureNode;
        resultLeft.features = new ArrayList<>(resultRight.features);
        resultLeft.label = resultLeft.dataSet.getMaxOccurrencesLabel();
        resultLeft.parent = featureNode;

        return new Tuple3<>(t, featureNode, dataSetSize*(entropy - minWeightedEntropy));
    }

    /**
     * Method to return a snapshot of this tree
     * @return a snapshot of this tree
     */
    private DecisionTree<T> copy() {
        DecisionTree<T> copy = new DecisionTree<>();
        copy.root = this.root.copy();
        return copy;
    }
}
