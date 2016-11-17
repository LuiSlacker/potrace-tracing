package de.htw.ip.potrace;

public class PolygonAlgorithm {

	public static boolean constraintsViolated(int[] a, int[] c0, int[] c1){
		return vectorProduct(c0, a) < 0 || vectorProduct(c1, a) > 0;
	}
	
	public static void updateConstraints(int[] a, int[] c0, int[] c1){
		if (Math.abs(a[0]) > 1 || Math.abs(a[1]) > 1){
			updateC0(a, c0);
		} 
	}
	
	public static void updateC0(int[] a, int[] c0){
		int[] d = new int[2];
		d[0] = (a[1] >= 0 && (a[1] > 0 || a[0] < 0)) ? a[0]+1: a[0]-1;  
		d[1] = (a[0] <= 0 && (a[0] < 0 || a[1] < 0)) ? a[1]+1: a[1]-1;
		c0 = vectorProduct(c0, d) >= 0 ? d: c0;
	}
	
	public static void updateC1(int[] a, int[] c1){
		int[] d = new int[2];
		d[0] = (a[1] <= 0 && (a[1] < 0 || a[0] < 0)) ? a[0]+1: a[0]-1;  
		d[1] = (a[0] >= 0 && (a[0] > 0 || a[1] < 0)) ? a[1]+1: a[1]-1;
		c1 = vectorProduct(c1, d) <= 0 ? d: c1;
	}
	
	public static int vectorProduct(int[] x, int[] y){
		return x[0] * y[1] - x[1] * y[0];
	}
}
