package de.htw.ip.potrace;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import de.htw.ip.basics.BezierElement;
import de.htw.ip.basics.CurveElement;
import de.htw.ip.basics.LineElement;

public class BezierAlgorithm {
	public static List<List<CurveElement>> generateBezierCurves(List<List<Point>> polygons, double alphaMin, double alphaMax, double distanceFactor){
		List<List<CurveElement>> bezierCurves = new ArrayList<List<CurveElement>>();
		polygons.forEach(polygon -> {
			bezierCurves.add(generateBezierCurve(polygon, alphaMin, alphaMax, distanceFactor));
		});
//		bezierCurves.add(generateBezierCurve(polygons.get(1), alphaMin, alphaMax, distanceFactor));
		bezierCurves.forEach(curve -> {
			curve.forEach(elem -> System.out.println(elem.toString()));
		});
		return bezierCurves;
	}
	
	private static List<CurveElement> generateBezierCurve(List<Point> polygon, double alphaMin, double alphaMax, double distanceFactor){
		List<CurveElement> curve = new ArrayList<CurveElement>();
		for (int i = 0; i < polygon.size(); i++) {//
			Point2D.Double a = new Point2D.Double(polygon.get(i).x, polygon.get(i).y);
			Point2D.Double midPoint1 = new Point2D.Double(
					a.x + (polygon.get((i-1 + polygon.size()) % polygon.size()).x - a.x)/2,
					a.y + (polygon.get((i-1 + polygon.size()) % polygon.size()).y - a.y)/2); 
			Point2D.Double midPoint2 = new Point2D.Double(
					a.x + (polygon.get((i+1) % polygon.size()).x - a.x)/2,
					a.y + (polygon.get((i+1) % polygon.size()).y - a.y)/2);
			Point2D.Double s_ = new Point2D.Double(midPoint2.y - midPoint1.y, -midPoint2.x + midPoint1.x);
			double s_length = Math.sqrt(Math.pow(s_.x, 2) + Math.pow(s_.y, 2));
			Point2D.Double s = new Point2D.Double(s_.x/s_length, s_.y/s_length );
			double d = scalarProduct(s, new Point2D.Double(a.x - midPoint1.x, a.y - midPoint1.y));
			double alpha = (4/3) * (d - 0.5)/d; //distanceFactor 
			alpha = alpha < alphaMin? alphaMin: alpha;
			
			if (alpha < alphaMax){
				curve.add(new LineElement(a, midPoint2));
			} else {
				alpha = 0.7;
				Point2D.Double z1 = new Point2D.Double(
						a.x + alpha * (midPoint1.x - a.x),
						a.y + alpha * (midPoint1.y - a.y));
				
				Point2D.Double z2 = new Point2D.Double(
						a.x + alpha * (midPoint2.x - a.x),
						a.y + alpha * (midPoint2.y - a.y));
				curve.add(new BezierElement(z1, z2, midPoint2));
			}
		}
		return curve;
	}
		
	private static double scalarProduct(Point2D.Double a, Point2D.Double b){
		return a.x * b.x + a.y * b.y;
	}
}
