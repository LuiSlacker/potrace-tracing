package de.htw.ip.basics;

import java.awt.geom.Point2D;

public class LineElement extends CurveElement{
	Point2D.Double a;
	Point2D.Double midPoint2;
	
	public LineElement(Point2D.Double a, Point2D.Double midPoint2){
		super();
		this.a = a;
		this.midPoint2 = midPoint2;
	}
	
}
