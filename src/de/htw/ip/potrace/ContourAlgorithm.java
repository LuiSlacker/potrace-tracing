package de.htw.ip.potrace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ContourAlgorithm {
	
	private final static int BLACK = 0xFF000000;
	private final static int WHITE = 0xFFFFFFFF;
	
	public static void potrace(int[] pixels, int imgWidth, int imgHeight){
		List<List<Integer>> paths = new ArrayList<List<Integer>>();
		findPaths(pixels, imgWidth, imgHeight, paths);
		System.out.println(paths);
		for (int i = 0; i < paths.size();i++) {
			System.out.println(paths.get(i));
			
		}
		
		drawContours(pixels, paths);
	}
	
	private static void drawContours(int[] pixels, List<List<Integer>> paths) {
		
	}

	private static void findPaths(int[] pixels, int imgWidth, int imgHeight, List<List<Integer>> paths){
		outerloop:
		for (int h = 0; h < imgHeight; h++){
			for (int w = 0; w < imgWidth; w++) {
				int pos = h * imgWidth + w;
				if (pixels[pos] == BLACK){
					Path<Integer> path = findPath(pixels, pos, imgWidth);
					paths.add(path);
					int[] invertedPixels = invertClosedRegion(pixels, path, imgWidth);
					findPaths(invertedPixels, imgWidth, imgHeight, paths);
					break outerloop;
				}
			}
		}
	}
	
	private static Path<Integer> findPath(int[] pixels, int pos, int imgWidth){
		Path<Integer> path = new Path<Integer>();
		path.add(pos);
		path.add(pos+imgWidth);
		int newPxl = findNewPathPixel(pixels, path, imgWidth);
		while(!path.contains(newPxl)){
			path.add(newPxl);
			newPxl = findNewPathPixel(pixels, path, imgWidth);
		}
//		path.setType(true);
		return path;
	}
	
	private static int findNewPathPixel(int[] pixels, Path<Integer> path, int imgWidth){
		AbsoluteDirection origin = getOrigin(path, imgWidth);
		int last = path.get(path.size()-1);
		int[] LR = getPixelsToCompare(origin, imgWidth, last);
		if(pixels[LR[1]] == BLACK){
			// gehe rel. rechts
			return getNextPathPixel(origin, RelativeDirection.RIGHT, last, imgWidth);
			
		} else {
			if(pixels[LR[0]] == BLACK){
				// gehe rel. gerade
				return getNextPathPixel(origin, RelativeDirection.STRAIGHT, last, imgWidth);
			} else {
				// gehe rel. links
				return getNextPathPixel(origin, RelativeDirection.LEFT, last, imgWidth);
			}
		}
	}
	
	private static AbsoluteDirection getOrigin(Path<Integer> path, int imgWidth){
		int last = path.get(path.size()-1);
		AbsoluteDirection tmp = null;
		int penultimate = path.get(path.size()-2);
		if (last-penultimate == -imgWidth) tmp = AbsoluteDirection.TOP; 
		if (last-penultimate == 1) tmp = AbsoluteDirection.RIGHT; 
		if (last-penultimate == imgWidth) tmp = AbsoluteDirection.BOTTOM; 
		if (last-penultimate == -1) tmp = AbsoluteDirection.LEFT; 
		return tmp;
	}
	
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
		default:
			break;
		}
		return a;
	}
	
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
	
	private static void invertPixels(int[] invertedPixels, int start, int imgWidth){
		int current = start;
		while(current % imgWidth != 0){
			invertedPixels[current] = (invertedPixels[current] == BLACK) ? WHITE:BLACK;
//			System.out.print(invertedPixels[current]);
			current++;
		}
//		System.out.println("");
	}

}
