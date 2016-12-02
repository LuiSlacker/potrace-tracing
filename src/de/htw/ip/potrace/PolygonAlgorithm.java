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
		contours.forEach(contour -> polygons.add(optimizedPolygon(contour)));
		return polygons;
	}
	
	/**
	 * calculates an optimal polygon for one contour within following steps
	 * 
	 *   1. calculate pivot segments of length n
	 *   2. calculate possible segments from pivots
	 *   3. build n enclosed polygons out of possible segments starting from each vertex
	 *   4. return the polygon with fewest segments
	 */
	private static List<Point> optimizedPolygon(List<Point> contour) {
		int[] pivots = pivots(contour);
		int[] possibles = possibles(pivots);
		List<List<Point>> possiblePolygons = buildPolygons(contour, possibles);
		return possiblePolygons.stream().min(Comparator.comparing(List<Point>::size)).get();
	}
	
	/**
	 * builds all enclosed polygons based on possible segments
	 * @return
	 */
	private static List<List<Point>> buildPolygons(List<Point> contour, int[] possibles){
		List<List<Point>> polygons = new ArrayList<List<Point>>();
		for (int i = 0; i < possibles.length; i++) {
			polygons.add(buildPolygon(contour, possibles, i));
		}
		return polygons;
	}
	
	/**
	 * builds one enclosed polygon based on possible segments and a startIndex
	 */
	private static List<Point> buildPolygon(List<Point> contour, int[] possibles, int startIndex){
		List<Point> polygon = new ArrayList<Point>(); 	
		polygon.add(contour.get(startIndex));
		int pointerStep1 = getNextPossibleIndex(possibles, startIndex);
		int pointerStep2 = getSecondNextPossibleIndex(possibles, startIndex); 
		while(pointerStep1 != pointerStep2){
			polygon.add(contour.get(pointerStep1));
			pointerStep1 = getNextPossibleIndex(possibles, pointerStep1);
			pointerStep2 = getSecondNextPossibleIndex(possibles, pointerStep2); 
		}
		polygon.add(contour.get(startIndex));
		return polygon;
	}
	
	private static int getNextPossibleIndex(int[] possibles, int index){
		return possibles[index];
	}
	
	private static int getSecondNextPossibleIndex(int[] possibles, int index){
		int next = possibles[index];
		return possibles[next];
	}

	private static int[] possibles(int[] pivots) {
		//TODO calculate possibles from pivots
//		int[] possibles = new int[pivots.length];
//		pivot[i+1] = pivot[i]-1
		return pivots.clone();
		
	}

	private static int[] pivots(List<Point> contour) {
		int[] pivots = new int[contour.size()-1];
		for (int i = 0; i < contour.size()-1;i++) {
			pivots[i] = maxStraightPath(contour, contour.get(i));
		}
		return pivots;
	}

	public static int maxStraightPath(List<Point> contour, Point vertex) {
		Point c0 = new Point(0,0);
		Point c1 = new Point(0,0);
		Set<AbsoluteDirection> directions = new HashSet<AbsoluteDirection>();
		int index = contour.indexOf(vertex);
		while (true) {
			Point vertex2Check = contour.get((index+1) % (contour.size()-1));
			Point previousVertex = contour.get((index) % (contour.size()-1));
			directions.add(getDirections(previousVertex, vertex2Check));
			if (directions.size() > 3) break;
			
			Point vector = new Point(vertex2Check.x-vertex.x, vertex2Check.y-vertex.y);
			if (constraintsViolated(vector, c0, c1)) break;
			updateConstraints(vector, c0,c1);
			index++;
		}
		return index % (contour.size()-1);
	}
	
	private static AbsoluteDirection getDirections(Point p1, Point p2){
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
	
}
