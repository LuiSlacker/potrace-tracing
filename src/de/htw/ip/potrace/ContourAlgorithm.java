package de.htw.ip.potrace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.htw.ip.basics.AbsoluteDirection;
import de.htw.ip.basics.Path;
import de.htw.ip.basics.RelativeDirection;

public class ContourAlgorithm {
	
	private final static int BLACK = 0xFF000000;
	private final static int WHITE = 0xFFFFFFFF;
	
	public static List<List<Integer>> potrace(int[] pixels, int imgWidth, int imgHeight){
		List<List<Integer>> paths = new ArrayList<List<Integer>>();
		findPaths(pixels, imgWidth, imgHeight, paths);
		setType(pixels, paths);
		return paths;
	}

	/**
	 * Finds all closedRegions within the image and saves its path vertices
	 *
	 */
	private static void findPaths(int[] pixels, int imgWidth, int imgHeight, List<List<Integer>> paths){
		for (int i = 0; i < pixels.length; i++){
			if (pixels[i] == BLACK){
				Path<Integer> path = findPath(pixels, i, imgWidth);
				paths.add(path);
				int[] invertedPixels = invertClosedRegion(pixels, path, imgWidth);
				findPaths(invertedPixels, imgWidth, imgHeight, paths);
				break;
			}
		}
	}
	
	/**
	 * Sets the type of the contour [outer | inner] 
	 */
	private static void setType(int[] pixels, List<List<Integer>> paths) {
		for (int i = 0; i < paths.size(); i++) {
			Path<Integer> path = (Path<Integer>)paths.get(i);
			path.setType((pixels[path.get(0)] == BLACK) ? true: false);
		}
	}
	
	/**
	 * Finds and returns one contour
	 *
	 */
	private static Path<Integer> findPath(int[] pixels, int pos, int imgWidth){
		Path<Integer> path = new Path<Integer>();
		path.add(pos);
		path.add(pos+imgWidth);
		int newPxl = findNewPathPixel(pixels, path, imgWidth);
		int overlap = Integer.MIN_VALUE;
		while(true){
			if(overlap != Integer.MIN_VALUE){
				if(path.get(overlap+1) == newPxl) break;
				else overlap = Integer.MIN_VALUE;
			}
			if(path.contains(newPxl)) overlap = path.indexOf(newPxl);
			path.add(newPxl);
			newPxl = findNewPathPixel(pixels, path, imgWidth);
		}
		return path;
	}
	
	/**
	 * Finds and returns the next pixel in the contour
	 *
	 */
	private static int findNewPathPixel(int[] pixels, Path<Integer> path, int imgWidth){
		AbsoluteDirection origin = getAbsoluteOrigin(path, imgWidth);
		int last = path.get(path.size()-1);
		int[] LR = getPixelsToCompare(origin, imgWidth, last);
		if(pixels[LR[1]] == BLACK){
			// go rel. right
			return getNextPathPixel(origin, RelativeDirection.RIGHT, last, imgWidth);
		} else {
			if(pixels[LR[0]] == BLACK){
				// go rel. straight
				return getNextPathPixel(origin, RelativeDirection.STRAIGHT, last, imgWidth);
			} else {
				// go rel. lefts
				return getNextPathPixel(origin, RelativeDirection.LEFT, last, imgWidth);
			}
		}
	}
	
	/**
	 * Get the absolute direction of last step within the contour
	 *
	 */
	private static AbsoluteDirection getAbsoluteOrigin(Path<Integer> path, int imgWidth){
		AbsoluteDirection aD = null;
		int last = path.get(path.size()-1);
		int penultimate = path.get(path.size()-2);
		if (last-penultimate == -imgWidth) aD = AbsoluteDirection.TOP; 
		if (last-penultimate == 1) aD = AbsoluteDirection.RIGHT; 
		if (last-penultimate == imgWidth) aD = AbsoluteDirection.BOTTOM; 
		if (last-penultimate == -1) aD = AbsoluteDirection.LEFT; 
		return aD;
	}
	
	/**
	 * Returns the two pixels (Left and Right in relative direction) to compare
	 * 
	 */
	private static int[] getPixelsToCompare(AbsoluteDirection d, int imgWidth, int pos){
		int[] a = new int[2];
		switch (d) {
		case BOTTOM:
			a[0] = pos;
			a[1] = pos-1;
			break;
		case LEFT:
			a[0] = pos-1;
			a[1] = pos-imgWidth-1;
			break;
		case TOP:
			a[0] = pos-imgWidth-1;
			a[1] = pos-imgWidth;
			break;
		case RIGHT:
			a[0] = pos-imgWidth;
			a[1] = pos;
			break;
		}
		return a;
	}
	
	/**
	 * Get the next Pixel based upon absolute and relative directions
	 *
	 */
	private static int getNextPathPixel(AbsoluteDirection origin, RelativeDirection direction, int pos, int imgWidth){
		AbsoluteDirection targetDirection = mapRelativeToAbsolutePosition(origin, direction);
		int tmp = 0;
		switch (targetDirection) {
		case TOP:
			tmp = pos-imgWidth;
			break;
		case RIGHT:
			tmp = pos+1;
			break;
		case BOTTOM:
			tmp = pos+imgWidth;
			break;
		case LEFT:
			tmp = pos-1;
			break;

		default:
			break;
		}
		return tmp;
	}
	
	/**
	 * Maps a relative to an absolute position
	 *
	 */
	public static AbsoluteDirection mapRelativeToAbsolutePosition(AbsoluteDirection origin, RelativeDirection rD){
		AbsoluteDirection aD = null;
		switch (rD) {
		case LEFT:
			aD = origin.previous();
			break;
		case STRAIGHT:
			aD = origin;
			break;
		case RIGHT:
			aD = origin.next();
			break;
		default:
			break;
		}
		return aD;
	}
	
	/**
	 * Inverts an entire closed Region 
	 */
	private static int[] invertClosedRegion(int[] pixels, Path<Integer> path, int imgWidth){
		int[] invertedPixels = Arrays.copyOf(pixels, pixels.length);
		for (int i = 1; i < path.size(); i++) {
			int current = path.get(i);
			int previous  = path.get(i-1);
			if (current-previous == imgWidth){
				invertPixels(invertedPixels, previous, imgWidth);
			} else if(current-previous == -imgWidth){
				invertPixels(invertedPixels, current, imgWidth);
			}
		}
		return invertedPixels;
	}
	
	/**
	 * Inverts Pixels from a startPixel up until the end of the row of the image
	 *
	 */
	private static void invertPixels(int[] invertedPixels, int start, int imgWidth){
		int current = start;
		while(current % imgWidth != 0){
			invertedPixels[current] = (invertedPixels[current] == BLACK) ? WHITE: BLACK;
			current++;
		}
	}

}
