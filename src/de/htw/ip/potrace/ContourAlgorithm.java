package de.htw.ip.potrace;

import java.util.ArrayList;
import java.util.List;

public class ContourAlgorithm {
	
	private final static int BLACK = 0xFF000000;
	private final static int WHITE = 0xFFFFFFFF;
	
	public static void potrace(int[] pixels, int imgWidth, int imgHeight){
		List<List<Integer>> paths = new ArrayList<List<Integer>>();
		Path<Integer> path = new Path<Integer>();
		outerloop:
		for (int h = 0; h < imgHeight; h++){
			for (int w = 0; w < imgWidth; w++) {
				int pos = h * imgWidth + w;
				if (pixels[pos] == BLACK){
					path.add(pos);
					path.add(pos+imgWidth);
					int newPxl = findPath(pixels, path, imgWidth);
					while(!path.contains(newPxl)){
						path.add(newPxl);
						newPxl = findPath(pixels, path, imgWidth);
					}
					break outerloop;
//					path.setType(true);
				}
			}
		}
		paths.add(path);
		System.out.println(paths);
	}
	
	private static int findPath(int[] pixels, Path<Integer> path, int imgWidth){
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

}
