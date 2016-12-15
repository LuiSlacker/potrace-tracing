package de.htw.ip.basics;

import java.awt.geom.Point2D;

public class LineElement extends CurveElement{
	public Point2D.Double a;
	
	public LineElement(Point2D.Double a, Point2D.Double midPoint2){
		super(midPoint2);
		this.a = a;
	}
	
	@Override
	public String toString() {
		return "LineElement | a:" + a + "midpoint2: " + midPoint2;
	}
	
}
