package basic;

import java.util.*;

public class MathTool {
	public static double mean(ArrayList<Double> list) {
	    double sum = 0;
	    for (double val : list) {
	        sum += val;
	    }
	    return sum / list.size();
	}
	
	
	public static double median(ArrayList<Double> list) {
		if(list.size()==0){
			System.out.println("list is empty for:"+list);
			return Double.NEGATIVE_INFINITY;
		}
		if(list.size()==1){
			return list.get(0);
		}
		int middle=list.size()/2;
	    if (list.size()%2 == 1) {
	        return list.get(middle);
	    } else {
	        return (list.get(middle-1)+list.get(middle))/2.0;
	    }
	}
	
	/**
     * Returns the sample variance in the ArrayList<Double> a, NaN if no such value.
     */
    public static double var(ArrayList<Double> a) {
        if (a.size() == 0) return Double.NaN;
        double avg = mean(a);
        double sum = 0.0;
        for (int i = 0; i < a.size(); i++) {
            sum += (a.get(i) - avg) * (a.get(i) - avg);
        }
        return sum / (a.size() - 1);
    }
    
    /**
     * Returns the sample standard deviation in the ArrayList<Double> a, NaN if no such value.
     */
    public static double stddev(ArrayList<Double> a) {
        return Math.sqrt(var(a));
    }
    
    /**
     * remove outlier
     */
    
    public static ArrayList<Double> removeOutlier(ArrayList<Double> a, double m){
    	double u=mean(a);
    	double s=stddev(a);
    	ArrayList<Double> filtered=new ArrayList<Double>();
    	for(Double e:a){
    		if(e > u-m*s &&  e < u+m*s){
    			filtered.add(e);
    		}
    	}
    	return filtered;
    }

	private static final long MEGABYTE = 1024L * 1024L;

	public static double bytesToKB(long bytes) {
		return bytes*1.0 / 1024L;
	}

	public static double bytesToMB(long bytes) {
		return bytes*1.0 / MEGABYTE;
	}
}
