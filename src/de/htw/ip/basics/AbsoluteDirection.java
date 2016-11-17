package de.htw.ip.basics;

public enum AbsoluteDirection {
	TOP, RIGHT, BOTTOM, LEFT;
	
	private static AbsoluteDirection[] vals = values();
	
	public AbsoluteDirection next(){
		return vals[(this.ordinal()+1) % vals.length];
	}
	
	public AbsoluteDirection previous(){
		return vals[(this.ordinal()-1 + vals.length) % vals.length];
	}
}
