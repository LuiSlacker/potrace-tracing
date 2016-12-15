package de.htw.ip.basics;

import java.awt.geom.Point2D;

public class BezierElement extends CurveElement{
	
	Point2D.Double z1, z2, midPoint2;
	
	public BezierElement(Point2D.Double z1, Point2D.Double z2, Point2D.Double midPoint2){
		super();
		this.z1 = z1;
		this.z2 = z2;
		this.midPoint2 = midPoint2;
	}
	
	
}
