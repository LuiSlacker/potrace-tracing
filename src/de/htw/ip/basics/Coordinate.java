package de.htw.ip.basics;

import java.util.Objects;

/**
 * Class representing one coordinate(pixel) of an image
 * @author Ludwig Goohsen
 *
 */
public class Coordinate {
	
	private final static int BLACK = 0xFF000000;

	private int w;
	private int h;
	private int imgWidth;
	private int imgHeight;
	
	public Coordinate(int w, int h, int imgWidth, int imgHeight) {
		this.w = w;
		this.h = h;
		this.imgWidth = imgWidth;
		this.imgHeight = imgHeight;
	}
	
	public int pos(){
		return this.h * this.imgWidth + w;
	}
	
	public int value(int[] pixels) {
		return pixels[this.pos()];
	}
	public boolean isValid(){
		return (this.w >= 0 && this.w < this.imgWidth && this.h >= 0 && this.h < this.imgHeight);
	}
	
	public boolean isBlack(int[] pixels) {
		return pixels[this.pos()] == BLACK;
	}
	
	public int w(){
		return this.w;
	}
	
	public int h(){
		return this.h;
	}
	
	public boolean isLabeled(int[] pixels) {
		return pixels[this.pos()] > 1;
	}
	
	public void label(int[] pixels, int label){
		pixels[this.pos()] = label;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
        if (!(obj instanceof Coordinate)) return false;
        Coordinate c = (Coordinate) obj;
        return this.w == c.w && this.h == c.h; 
	}
	
	@Override
    public int hashCode() {
        return Objects.hash(w, h);
    }

	// getter for neighboring coordinates -----------------------------------------
	public Coordinate top(){
		return new Coordinate(this.w, this.h-1, this.imgWidth, this.imgHeight);
	}
	
	public Coordinate topRight(){
		return new Coordinate(this.w+1, this.h-1, this.imgWidth, this.imgHeight);
	}
	
	public Coordinate right(){
		return new Coordinate(this.w+1, this.h, this.imgWidth, this.imgHeight);
	}
	
	public Coordinate bottomRight(){
		return new Coordinate(this.w+1, this.h+1, this.imgWidth, this.imgHeight);
	}
	
	public Coordinate bottom(){
		return new Coordinate(this.w, this.h+1, this.imgWidth, this.imgHeight);
	}
	
	public Coordinate bottomLeft(){
		return new Coordinate(this.w-1, this.h+1, this.imgWidth, this.imgHeight);
	}
	
	public Coordinate left(){
		return new Coordinate(this.w-1, this.h, this.imgWidth, this.imgHeight);
	}
	
	public Coordinate topLeft(){
		return new Coordinate(this.w-1, this.h-1, this.imgWidth, this.imgHeight);
	}
	//  ------------------------------------------------------------------------------
	
}
