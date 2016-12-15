package de.htw.ip.basics;

import java.awt.geom.Point2D;

public class BezierElement extends CurveElement{
	
	public Point2D.Double z1, z2;
	
	public BezierElement(Point2D.Double z1, Point2D.Double z2, Point2D.Double midPoint2){
		super(midPoint2);
		this.z1 = z1;
		this.z2 = z2;
	}
	
	
}
