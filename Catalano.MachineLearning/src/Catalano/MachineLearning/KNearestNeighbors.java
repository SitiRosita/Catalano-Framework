/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Catalano.MachineLearning;

import Catalano.Math.Matrix;
import Catalano.Statistics.Kernels.IKernel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * K Nearest Neighbour classifier.
 * @author Diego Catalano
 * @param <T> Object.
 */
public class KNearestNeighbors<T> {
    
    public static enum Distance {Euclidean};
    
    private int k;
    private List<double[]> input;
    private T output[];
    private Distance distance = Distance.Euclidean;
    private IKernel kernel;
    private boolean useKernel = false;

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = Math.max(1, k);
    }

    public List<double[]> getInput() {
        return input;
    }

    public void setInput(List<double[]> input) {
        this.input = input;
    }

    public T[] getOutput() {
        return output;
    }

    public void setOutput(T[] output) {
        this.output = output;
    }

    public IKernel getKernel() {
        return kernel;
    }

    public void setKernel(IKernel kernel) {
        this.kernel = kernel;
        this.useKernel = true;
    }
    
    public KNearestNeighbors(List<double[]> input, T[] output){
        this.k = 3;
        this.input = input;
        this.output = output;
    }
    
    public KNearestNeighbors(List<double[]> input, T[] output, Distance distance){
        this.k = 3;
        this.input = input;
        this.output = output;
        this.distance = distance;
    }

    public KNearestNeighbors(int k, List<double[]> input, T[] output) {
        setK(k);
        this.input = input;
        this.output = output;
    }
    
    public KNearestNeighbors(int k, List<double[]> input, T[] output, Distance distance) {
        setK(k);
        this.input = input;
        this.output = output;
        this.distance = distance;
    }
    
    public KNearestNeighbors(int k, List<double[]> input, T[] output, IKernel kernel) {
        setK(k);
        this.input = input;
        this.output = output;
        setKernel(kernel);
    }
    
    /**
     * Compute.
     * @param feature Feature to compute.
     * @return Object.
     */
    public T Compute(double[] feature){
        
        int sizeF = input.size();
        int lengthF = feature.length;
        double[] dist = new double[sizeF];
        
        //Compute distance.
        if(useKernel){
            
            for (int i = 0; i < sizeF; i++) {
                dist[i] = this.kernel.Function(feature, input.get(i));
            }
            
        }else{
            
            switch(distance){
                case Euclidean:
                    double sum;
                    for (int i = 0; i < sizeF; i++) {
                        sum = 0;
                        double[] featureModel = input.get(i);
                        for (int j = 0; j < lengthF; j++) {
                            sum += Math.pow(feature[j] - featureModel[j], 2);
                        }
                        dist[i] = Math.sqrt(sum);
                    }
                break;
            }
            
        }
        
        //If k is 1, we can retrive the object quickly.
        if(k == 1) return output[Matrix.MinIndex(dist)];
        
        //Sort indexes based on score
        int[] indexes = Matrix.Indices(0, dist.length);
        List<Score> lst = new ArrayList<Score>(dist.length);
        for (int i = 0; i < dist.length; i++) {
            lst.add(new Score(dist[i], indexes[i]));
        }
        
        Collections.sort(lst);
        
        //Compute vote majority
        HashMap<T, Integer> map = new HashMap<T, Integer>();
        int max = 0;
        for (int i = 0; i < k; i++) {
            int index = lst.get(i).index;
            if(!map.containsKey(output[index])){
                map.put(output[index], 1);
            }
            else{
                int x = map.get(output[index]) + 1;
                map.put(output[index], x);
                if(x > max) max = x;
            }
        }
        
        for(Map.Entry<T,Integer> entry : map.entrySet()) {
          if(entry.getValue() == max)
              return entry.getKey();

        }
        
        return null;
        
    }
    
    private class Score implements Comparable<Score> {
        double score;
        int index;

        public Score(double score, int index) {
            this.score = score;
            this.index = index;
        }

        @Override
        public int compareTo(Score o) {
            return score < o.score ? -1 : score > o.score ? 1 : 0;
        }
    }
    
}