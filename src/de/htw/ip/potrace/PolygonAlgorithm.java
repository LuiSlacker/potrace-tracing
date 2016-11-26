package de.htw.ip.potrace;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.htw.ip.basics.AbsoluteDirection;

public class PolygonAlgorithm {
	
	public static List<List<Point>> optimizedPolygons(List<List<Point>> contours, int imgWidth){
		List<List<Point>> polygons = new ArrayList<List<Point>>();
		for (List<Point> contour : contours) {
			polygons.add(optimizedPolygon(contour));
		}
		return polygons;
	}
	
	private static List<Point> optimizedPolygon(List<Point> contour) {
		return possiblePolygons(contour).stream().min(Comparator.comparing(List<Point>::size)).get();
	}

	private static List<List<Point>> possiblePolygons(List<Point> contour) {
		List<List<Point>> possibles = new ArrayList<List<Point>>();
		List<List<Point>> pivots = pivotPolygons(contour);
		pivots.forEach(s -> System.out.println(s));
		// generate possibles
		return pivots;
		
	}

	private static List<List<Point>> pivotPolygons(List<Point> contour) {
		List<List<Point>> pivots = new ArrayList<List<Point>>();
		for (Point vertex : contour) {
			pivots.add(straightPathPolygon(contour, vertex));
		}
//		pivots.add(straightPathPolygon(contour, contour.get(0)));
		return pivots;
	}

	public static List<Point> straightPathPolygon(List<Point> contour, Point startVertex) {
		List<Point> pivot = new ArrayList<Point>();
		Point newVertex = startVertex;
		pivot.add(startVertex);
		do{
			newVertex = maxStraightPath(contour, newVertex, startVertex);
			pivot.add(newVertex);
		} while(!(newVertex.equals(startVertex)));
		return pivot;
	}

	public static Point maxStraightPath(List<Point> contour, Point vertex, Point startVertex){
		Point c0 = new Point(0,0);
		Point c1 = new Point(0,0);
		Set<AbsoluteDirection> directions = new HashSet<AbsoluteDirection>();
		
		int index = contour.indexOf(vertex);
		while (true) {
			Point vertex2Check = contour.get((index+1) % contour.size());
			if (vertex2Check.equals(startVertex)) break;
			
			Point previousVertex = contour.get((index) % contour.size());
			directions.add(getDirections(previousVertex, vertex));
			if (directions.size() > 3) break;
			
			Point vector = new Point(vertex2Check.x-vertex.x, vertex2Check.y-vertex.y);
			if (constraintsViolated(vector, c0, c1)) break;
			updateConstraints(vector, c0,c1);
			index++;
		}
		return contour.get((index+1) % contour.size()); //TODO REALLLYYYY index +1 ???????????
	}
	
	public static AbsoluteDirection getDirections(Point p1, Point p2){
		int gapY = p1.y - p2.y;
		int gapX = p1.x - p2.x;
		
		if(gapY == 1){
			return AbsoluteDirection.TOP;
		}else if(gapY == -1){
			return AbsoluteDirection.BOTTOM;
		}else if(gapX == 1){
			return AbsoluteDirection.LEFT;
		}else{
			return AbsoluteDirection.RIGHT;
		}
	}
	
	private static boolean constraintsViolated(Point a, Point c0, Point c1){
		return vectorProduct(c0, a) < 0 || vectorProduct(c1, a) > 0;
	}
	
	private static void updateConstraints(Point a, Point c0, Point c1){
		if (!(Math.abs(a.x) <= 1 && Math.abs(a.y) <= 1)){
			updateC0(a, c0);
			updateC1(a, c1);
		} 
	}
	
	private static void updateC0(Point a, Point c0){
		Point d = new Point();
		d.x = (a.y >= 0 && (a.y > 0 || a.x < 0)) ? a.x+1: a.x-1;  
		d.y = (a.x <= 0 && (a.x < 0 || a.y < 0)) ? a.y+1: a.y-1;
		if (vectorProduct(c0, d) >= 0) {
			c0.x = d.x;
			c0.y = d.y;
		}
	}
	
	private static void updateC1(Point a, Point c1){
		Point d = new Point();
		d.x = (a.y <= 0 && (a.y < 0 || a.x < 0)) ? a.x+1: a.x-1;  
		d.y = (a.x >= 0 && (a.x > 0 || a.y < 0)) ? a.y+1: a.y-1;
		if (vectorProduct(c1, d) <= 0) {
			c1.x = d.x;
			c1.y = d.y;
		}
	}
	
	private static int vectorProduct(Point a, Point b){
		return a.x * b.y - a.y * b.x;
	}
}
