package de.htw.ip.potrace;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PolygonAlgorithm {
	
	public static List<List<Point>> optimizedPolygons(List<List<Point>> contours, int imgWidth){
		List<List<Point>> polygons = new ArrayList<List<Point>>();
		for (List<Point> contour : contours) {
			polygons.add(optimizedPolygon(contour));
		}
		return polygons;
	}
	
	private static List<Point> optimizedPolygon(List<Point> contour) {
		List<List<Point>> possibles = possiblePolygons(contour);
		// check for shortest possible polygon
		return null;
	}

	private static List<List<Point>> possiblePolygons(List<Point> contour) {
		List<List<Point>> possibles = new ArrayList<List<Point>>();
		List<List<Point>> pivots = pivotPolygons(contour);
		pivots.forEach(s -> System.out.println(s));
		// generate possibles
		return possibles;
		
	}

	private static List<List<Point>> pivotPolygons(List<Point> contour) {
		List<List<Point>> pivots = new ArrayList<List<Point>>();
		for (Point vertex : contour) {
			pivots.add(straightPathPolygon(contour, vertex));
		}
		return pivots;
	}

	public static List<Point> straightPathPolygon(List<Point> contour, Point startVertex) {
		List<Point> pivot = new ArrayList<Point>();
		Point newVertex = startVertex;
		do{
			newVertex = maxStraightPath(contour, newVertex, startVertex);
			pivot.add(newVertex);
		} while(!(newVertex.x == startVertex.x && newVertex.y == startVertex.y));
		return pivot;
	}

	public static Point maxStraightPath(List<Point> contour, Point vertex, Point startVertex){
		Point c0 = new Point(0,0);
		Point c1 = new Point(0,0);
		
		int index = contour.indexOf(vertex);
		while (true) {
			Point vertex2Check = contour.get((index+1) % contour.size());
			//checkDirections break
			// calculate new vector
			Point vector = new Point(vertex2Check.x-vertex.x, vertex2Check.y-vertex.y);
			if (vertex2Check.x == startVertex.x && vertex2Check.y == startVertex.y) break;
			if (constraintsViolated(vector, c0,c1)) {
				break;
			}
			updateConstraints(vector, c0,c1);
			index++;
		}
		return contour.get((index+1) % contour.size()); //TODO REALLLYYYY index +1 ???????????
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
//		c0 = vectorProduct(c0, d) >= 0 ? d: c0;
		if (vectorProduct(c0, d) >= 0) {
			c0.x = d.x;
			c0.y = d.y;
		}
	}
	
	private static void updateC1(Point a, Point c1){
		Point d = new Point();
		d.x = (a.y <= 0 && (a.y < 0 || a.x < 0)) ? a.x+1: a.x-1;  
		d.y = (a.x >= 0 && (a.x > 0 || a.y < 0)) ? a.y+1: a.y-1;
//		c1 = vectorProduct(c1, d) <= 0 ? d: c1;
		if (vectorProduct(c1, d) <= 0) {
			c1.x = d.x;
			c1.y = d.y;
		}
	}
	
	private static int vectorProduct(Point a, Point b){
		return a.x * b.y - a.y * b.x;
	}
}
