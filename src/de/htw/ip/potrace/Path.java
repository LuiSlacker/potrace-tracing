package de.htw.ip.potrace;

import java.util.ArrayList;

public class Path<T> extends ArrayList<T>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// true: outer contour, false: inner contour
	private boolean type;
	
	public Path(){
	}

	public boolean getType() {
		return this.type;
	}
	
	public void setType(boolean type) {
		this.type = type;
	}
	
}