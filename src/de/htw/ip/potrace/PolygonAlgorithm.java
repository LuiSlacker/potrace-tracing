package de.htw.ip.potrace;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.htw.ip.basics.AbsoluteDirection;
import de.htw.ip.basics.Path;

public class PolygonAlgorithm {
	
	public static List<Path<Point>> optimizedPolygons(ArrayList<Path<Point>> contours, int imgWidth) {
		List<Path<Point>> polygons = new ArrayList<Path<Point>>();
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
	private static Path<Point> optimizedPolygon(List<Point> contour) {
		int[] pivots = pivots(contour);
		int[] possibles = possibles(pivots);
		List<Path<Point>> possiblePolygons = buildPolygons(contour, possibles);
		return possiblePolygons.stream().min(Comparator.comparing(Path<Point>::size)).get();
	}
	
	/**
	 * builds all enclosed polygons based on possible segments
	 * @return
	 */
	private static List<Path<Point>> buildPolygons(List<Point> contour, int[] possibles){
		List<Path<Point>> polygons = new ArrayList<Path<Point>>();
		for (int i = 0; i < possibles.length; i++) {
			polygons.add(buildPolygon(contour, possibles, i));
		}
		return polygons;
	}
	
	private static Path<Point> buildPolygon(List<Point> contour, int[] possibles, int startIndex){
		Path<Point> polygon = new Path<Point>();
		int currentPointer = startIndex;
		int nextPointer = getNextPossibleIndex(possibles, startIndex);
		polygon.add(contour.get(currentPointer));
		while(normalizedVertex(nextPointer, startIndex, possibles.length)-normalizedVertex(currentPointer, startIndex, possibles.length) > 0){
			polygon.add(contour.get(nextPointer));
			currentPointer = nextPointer;
			nextPointer = getNextPossibleIndex(possibles, nextPointer);
		}
		polygon.setType(((Path<Point>)contour).getType());
		return polygon;
	}
	
	private static int normalizedVertex(int vertex, int startVertex, int n){
		return (vertex - startVertex + n) % n;
	}
	
	private static int getNextPossibleIndex(int[] possibles, int index){
		int assumedPossible = index;
		while (cyclicDiffViolated(index, assumedPossible, possibles.length)) {
			assumedPossible = (assumedPossible - 1 + possibles.length) % possibles.length;
		}
		return possibles[assumedPossible];
	}

	private static boolean cyclicDiffViolated(int i, int j, int n){
		return cyclicDiff(i, j, n-1) > (n-1-3);
		
	}
	
	private static int cyclicDiff(int i, int j, int n){
		return (j < i)? j-i+n : j-i;
	}
	
	private static int[] possibles(int[] pivots) {
		int[] possibles = new int[pivots.length];
		for (int i = 0; i < pivots.length; i++) {
			possibles[i] = ((pivots[((i-1 + pivots.length) % pivots.length)] - 1) + pivots.length) % pivots.length;
		}
		return possibles;
		
	}

	private static int[] pivots(List<Point> contour) {
		int[] pivots = new int[contour.size()];
		for (int i = 0; i < contour.size();i++) {
			pivots[i] = maxStraightPath(contour, i);
		}
		return pivots;
	}

	public static int maxStraightPath(List<Point> contour, int vertexIndex) {
		Point c0 = new Point(0,0);
		Point c1 = new Point(0,0);
		Point vertex = contour.get(vertexIndex);
		Set<AbsoluteDirection> directions = new HashSet<AbsoluteDirection>();
		while (true) {
			Point currentVertex = contour.get((vertexIndex) % contour.size());
			Point vertex2Check = contour.get((vertexIndex+1) % contour.size());
			directions.add(getDirections(currentVertex, vertex2Check));
			if (directions.size() > 3) break;
			Point vector = new Point(vertex2Check.x-vertex.x, vertex2Check.y-vertex.y);
			if (constraintsViolated(vector, c0, c1)) break;
			updateConstraints(vector, c0,c1);
			vertexIndex++;
		}
		return (vertexIndex) % contour.size();
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
