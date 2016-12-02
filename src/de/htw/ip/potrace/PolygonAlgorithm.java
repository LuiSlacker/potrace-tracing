package de.htw.ip.potrace;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.htw.ip.basics.AbsoluteDirection;
import de.htw.ip.basics.KeyPair;
import de.htw.ip.basics.Penalty;

public class PolygonAlgorithm {
	
	public static List<List<Point>> optimizedPolygons(List<List<Point>> contours, int imgWidth) {
		List<List<Point>> polygons = new ArrayList<List<Point>>();
//		contours.forEach(contour -> polygons.add(optimizedPolygon(contour)));
		polygons.add(optimizedPolygon(contours.get(0)));
		return polygons;
	}
	
	private static List<Point> optimizedPolygon(List<Point> contour) {
		List<Point> polygon = new ArrayList<Point>(); 
		int[] possibles = possibles(contour);
//		for (int index : possibles) {
//			polygon.add(contour.get(index));
//		}
//		
		int index = 0;
		polygon.add(contour.get(index));
		outer:
		while(true){
			if (possibles[index] > contour.size()-1) {
				int newIndex = possibles[index];
				while(newIndex > contour.size()-1){
					newIndex--;
				}
				polygon.add(contour.get(newIndex));
				break outer;
			} else {
				index = possibles[index];
				polygon.add(contour.get(index));
			}
		}
		
		// generate Polygons
//		return possiblePolygons(contour).stream().min(Comparator.comparing(List<Point>::size)).get();
		return polygon;
	}

	private static int[] possibles(List<Point> contour) {
		int[] possibles = new int[contour.size()-1];
		int[] pivots = pivots(contour);
		//System.out.println(Arrays.toString(pivots));
		// generate and return possibles
		// pivot[i+1] = pivot[i]-1
		return pivots;
		
	}

	private static int[] pivots(List<Point> contour) {
		int[] pivots = new int[contour.size()-1];
//		contour.forEach(vertex -> pivots[contour.indexOf(vertex)] = maxStraightPath(contour, vertex));
		for (int i = 0; i < contour.size()-1;i++) {
			pivots[i] = maxStraightPath(contour, contour.get(i));
		}
		System.out.println(Arrays.toString(pivots));
		return pivots;
	}

	public static int maxStraightPath(List<Point> contour, Point vertex) {
		Point c0 = new Point(0,0);
		Point c1 = new Point(0,0);
		Set<AbsoluteDirection> directions = new HashSet<AbsoluteDirection>();
		int index = contour.indexOf(vertex);
		while (true) {
			Point vertex2Check = contour.get((index+1) % contour.size());
			
			Point previousVertex = contour.get((index) % contour.size());
			directions.add(getDirections(previousVertex, vertex2Check));
			if (directions.size() > 3) break;
			
			Point vector = new Point(vertex2Check.x-vertex.x, vertex2Check.y-vertex.y);
			if (constraintsViolated(vector, c0, c1)) break;
			updateConstraints(vector, c0,c1);
			index++;
		}
		return index % contour.size();
	}
	
	public static AbsoluteDirection getDirections(Point p1, Point p2){
		Point gap = new Point(p1.x - p2.x, p1.y - p2.y);
		if(gap.y == 1) {
			return AbsoluteDirection.TOP;
		} else if(gap.y == -1) {
			return AbsoluteDirection.BOTTOM;
		} else if(gap.x == 1) {
			return AbsoluteDirection.LEFT;
		} else {
			return AbsoluteDirection.RIGHT;
		}
	}
	
	private static boolean constraintsViolated(Point a, Point c0, Point c1) {
		return vectorProduct(c0, a) < 0 || vectorProduct(c1, a) > 0;
	}
	
	private static void updateConstraints(Point a, Point c0, Point c1) {
		if (!(Math.abs(a.x) <= 1 && Math.abs(a.y) <= 1)){
			updateC0(a, c0);
			updateC1(a, c1);
		} 
	}
	
	private static void updateC0(Point a, Point c0) {
		Point d = new Point((a.y >= 0 && (a.y > 0 || a.x < 0)) ? a.x+1: a.x-1, (a.x <= 0 && (a.x < 0 || a.y < 0)) ? a.y+1: a.y-1);
		if (vectorProduct(c0, d) >= 0) {
			c0.x = d.x;
			c0.y = d.y;
		}
	}
	
	private static void updateC1(Point a, Point c1){
		Point d = new Point((a.y <= 0 && (a.y < 0 || a.x < 0)) ? a.x+1: a.x-1, (a.x >= 0 && (a.x > 0 || a.y < 0)) ? a.y+1: a.y-1);
		if (vectorProduct(c1, d) <= 0) {
			c1.x = d.x;
			c1.y = d.y;
		}
	}
	
	private static int vectorProduct(Point a, Point b){
		return a.x * b.y - a.y * b.x;
	}
	
	private static double grossPenalty(List<Point> possible){
//		possible.forEach(vertex -> {
//			penalty(possible, vertex, vj)
//		});
		return 0.0;
	}
	
	private static double penalty(List<Point> contour, Point vi, Point vj){
		int a = 0;
		int b = 0;
		int c = 0;
		int x = vj.x-vi.x;
		int y = vj.y-vi.y;
		int x_ = (vi.x+vj.x)/(2-vi.x);
		int y_ = (vi.y+vj.y)/(2-vi.y);
		calcSumTable(contour, vi, vj);
		return Math.sqrt(c*Math.pow(x, 2) + 2*b*x*y + a*Math.pow(y, 2));
	}
	
	private static void calcSumTable(List<Point> contour, Point vi, Point vj){
		Map<KeyPair, Double> sumTable = new HashMap<KeyPair, Double>();
		double xk = 0;
		double yk = 0;
		double xk2 = 0;
		double yk2 = 0;
		double xkyk = 0;
		for (int k = contour.indexOf(vi); k <= contour.indexOf(vj); k++) {
			xk += contour.get(k).x-vi.x;
			yk += contour.get(k).y-vi.y;
			xk2 += Math.pow(xk, 2);
			yk2 += Math.pow(yk, 2);
			xkyk += xk*yk;
			sumTable.put(new KeyPair(Penalty.XK, k), xk);
			sumTable.put(new KeyPair(Penalty.YK, k), yk);
			sumTable.put(new KeyPair(Penalty.XK2, k), xk2);
			sumTable.put(new KeyPair(Penalty.YK2, k), yk2);
			sumTable.put(new KeyPair(Penalty.XKYK, k), xkyk);
		}
	}
}
