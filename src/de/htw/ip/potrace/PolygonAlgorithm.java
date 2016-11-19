package de.htw.ip.potrace;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class PolygonAlgorithm {
	
	public static void optimizedPolygons(List<List<Point>> contours, int imgWidth){
		for (List<Point> contour : contours) {
			optimizedPolygon(contour);
		}
	}
	
	private static void optimizedPolygon(List<Point> contour) {
		List<List<Point>> pivots = new ArrayList<List<Point>>();
		for (Point vertex : contour) {
			pivots.add(maxStraightPath(contour, vertex));
		}
	}

	public static void allStraightPathPolygons(List<Point> contour){
		for (Point vertex : contour) {
			straightPathPolygon(contour, vertex);
		}
	}

	public static void straightPathPolygon(List<Point> contour, Point vertex) {
		
		
	}

	public static List<Point> maxStraightPath(List<Point> contour, Point vertex){
		Point c0 = new Point(0,0);
		Point c1 = new Point(0,0);
		
		List<Point> pivot = new ArrayList<Point>(); 
		int index = contour.indexOf(vertex);
		while (true) {
			Point vertex2Check = contour.get((index+1) % contour.size());
			//checkDirections break
			// calculate new vector
			if (constraintsViolated(vertex2Check, c0,c1)) {
				break;
			}
			updateConstraints(vertex2Check, c0,c1);
			index++;
		}
		pivot.add(contour.get(index+1));
		return pivot;
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
		c0 = vectorProduct(c0, d) >= 0 ? d: c0;
	}
	
	private static void updateC1(Point a, Point c1){
		Point d = new Point();
		d.x = (a.y <= 0 && (a.y < 0 || a.x < 0)) ? a.x+1: a.x-1;  
		d.y = (a.x >= 0 && (a.x > 0 || a.y < 0)) ? a.y+1: a.y-1;
		c1 = vectorProduct(c1, d) <= 0 ? d: c1;
	}
	
	private static int vectorProduct(Point a, Point b){
		return a.x * b.y - a.y * b.x;
	}
}
