package de.htw.ip.basics;
import java.util.Arrays;

public class BasicAlgorithms {
	
	private final static int BLACK = 0xFF000000;
	private final static int WHITE = 0xFFFFFFFF;
	private final static int DILATE_FLAG = 0x00000000;
	
	/**
	 * Calculates the optimal threshold to binarize an image
	 * @param pixels
	 * @return
	 */
	public static int getIsoDataThreshold(int[] pixels){
		int t  = 128;
		int t_old = Integer.MIN_VALUE;
		double[] probabilities = generateProbabilities(pixels);
		
		double pA;
		double pB;
		double uA;
		double uB;
		
		while(Math.abs(t-t_old) > 1 ) {
			pA = 0;
			pB = 0;
			uA = 0;
			uB = 0;
			
			//left side
			for (int i = 0; i < t; i++) {
				pA += probabilities[i]; 
			}
			for (int j = 0; j < t; j++) {
				uA += (j * probabilities[j]);
			}
			uA = (pA > 0)? uA/pA: t;
			
			// right side
			for (int h = t; h < 256; h++) {
				pB += probabilities[h]; 
			}
			for (int k = t; k < 256; k++) {
				uB += (k * probabilities[k]);
			}
			uB = (pB > 0)? uB/pB: t;
			
			t_old = t;			
			t = (int)((uA + uB) / 2);
		}
		return t;
	}

	/**
	 * Dilates a binary image with structuring element with 4 pixels
	 * @param pixels
	 * @param imgWidth
	 * @param imgHeight
	 * @return the dilated image
	 */
	public static int[] dilate(int[] pixels, int imgWidth, int imgHeight){
		int[] dilatedPixels = java.util.Arrays.copyOf(pixels, pixels.length);
		for (int h = 0; h < imgHeight; h++){
			for (int w = 0; w < imgWidth; w++) {
				int pos = h * imgWidth + w;
				if(pixels[pos] == BLACK){
					if(isEdgePixel(w, h, imgHeight, imgHeight)) {
						dilateEdgePixel(dilatedPixels, imgWidth, imgHeight, w, h, pos);
					} else {
						dilatedPixels[pos-1] = BLACK;
						dilatedPixels[pos+1] = BLACK;
						dilatedPixels[pos-imgWidth] = BLACK;
						dilatedPixels[pos+imgWidth] = BLACK;
						
					}
				}
			}
		}
		return dilatedPixels;
	}
	
	/**
	 * Dilates a binary image with structuring element with 4 pixels without allocating another pixel-array.
	 * THIS IS AN OPTIMIZED VERSION OF THE DILATE ALGORITHM
	 * @param pixels
	 * @param imgWidth
	 * @param imgHeight
	 * @return
	 */
	public static int[] dilateOptimized(int[] pixels, int imgWidth, int imgHeight) {
		for (int h = 0; h < imgHeight; h++) {
			for (int w = 0; w < imgWidth; w++) {
				int pos = h * imgWidth + w;
				if(pixels[pos] == BLACK){
					if(isEdgePixel(w, h, imgWidth, imgHeight)){
						dilateEdgePixelOptimized(pixels, imgWidth, imgHeight, w, h, pos);
					} else {
						if(pixels[pos-1] == WHITE) pixels[pos-1] = DILATE_FLAG;
						if(pixels[pos+1] == WHITE) pixels[pos+1] = DILATE_FLAG;
						if(pixels[pos-imgWidth] == WHITE) pixels[pos-imgWidth] = DILATE_FLAG;
						if(pixels[pos+imgWidth] == WHITE) pixels[pos+imgWidth] = DILATE_FLAG;
					}
				}
			}
		}
		
		for (int i = 0; i < pixels.length; i++) {
			if(pixels[i]== DILATE_FLAG) pixels[i]=BLACK; 
		}
		return pixels;
	}
	
	/**
	 * Erodes a binary image with structuring element with 4 pixels
	 * @param pixels
	 * @param imgWidth
	 * @param imgHeight
	 * @return
	 */
	public static int[] erode(int[] pixels, int imgWidth, int imgHeight){
		int[] erodedPixels = java.util.Arrays.copyOf(pixels, pixels.length);
		for (int h = 0; h < imgHeight; h++){
			for (int w = 0; w < imgWidth; w++) {
				int pos = h * imgWidth + w;
				if (pixels[pos] == BLACK){
					if(isEdgePixel(w, h, imgHeight, imgHeight)) {
						erodedPixels[pos] = WHITE;
					} else if (!isStructureElementMatching(pixels, imgWidth, imgHeight, pos)){
						erodedPixels[pos] = WHITE;
					}
				}
			}
		}
		return erodedPixels;
	}
	
	/**
	 * Inverts a binary image
	 * @param pixels
	 * @return
	 */
	public static int[] invert(int[] pixels){
		int[] inverted = Arrays.copyOf(pixels, pixels.length);
		for (int i = 0; i < pixels.length; i++) {
			inverted[i] = (pixels[i] == 0xff000000 )? 0xffffffff : 0xff000000;
		}
		return inverted;
	}
	
	/**
	 * Generates an outline of a binary image
	 * @param pixels
	 * @param imgWidth
	 * @param imgHeight
	 * @return
	 */
	public static int[] outline(int[] pixels, int imgWidth, int imgHeight){
		int[] eroded = erode(pixels, imgWidth, imgHeight);
		int[] inverted = invert(eroded);
		return combineLogicallyAND(pixels, inverted);
	}
	
	/**
	 * Converts a RGB Image into grayscale
	 * @param pixel
	 * @return
	 */
	public static int convertRGBtoGrayValue(int pixel){
		return ((pixel & 0xff) + ((pixel & 0xff00) >> 8) + ((pixel & 0xff0000) >> 16)) / 3;
	}
	
	// Helpers -----------------------------------------------------------------------------
	
	/**
	 * Returns an array with probability of each grayscale value
	 * @param pixels the pixel array representing the image
	 * @return probabilities
	 */
	private static double[] generateProbabilities(int[] pixels){
		double[] frequencies = new double[256];
		double numPixels = (double)pixels.length;
		
		for(int i = 0; i < pixels.length; i++) {
			int gray = convertRGBtoGrayValue(pixels[i]);
			frequencies[gray]++;
		}
		for (int i = 0; i < frequencies.length; i++) {
			frequencies[i] /= numPixels;
		}
		return frequencies;
	}

	public static boolean isEdgePixel(int width, int height, int imgWidth, int imgHeight){
		return (width == 0 | width == imgWidth-1 | height == 0 | height == imgHeight-1);
	}
	
	private static void dilateEdgePixel(int[] pixel, int imgWidth, int imgHeight, int w, int h, int pos){
		//first and last columns
		if(w == 0){
			pixel[pos+1] = BLACK;
		} else if(w == imgWidth-1) {
			pixel[pos-1] = BLACK;
		}
		// first and last row
		if (h == 0){
			pixel[pos+imgWidth] = BLACK;
		} else if(h == imgHeight-1){
			pixel[pos-imgWidth] = BLACK;
		}
	}
	
	private static void dilateEdgePixelOptimized(int[] pixels, int imgWidth, int imgHeight, int w, int h, int pos){
		//first and last columns
		if(w == 0){
			if(pixels[pos+1] == WHITE) pixels[pos+1] = DILATE_FLAG;
		} else if(w == imgWidth-1) {
			if(pixels[pos-1] == WHITE) pixels[pos-1] = DILATE_FLAG;
		}
		// first and last row
		if (h == 0){
			if(pixels[pos+imgWidth] == WHITE) pixels[pos+imgWidth] = DILATE_FLAG;
		} else if(h == imgHeight-1){
			if(pixels[pos-imgWidth] == WHITE) pixels[pos-imgWidth] = DILATE_FLAG;
		}
	}
	
	private static boolean isStructureElementMatching(int[] pixels, int imgWidth, int imgHeight, int pos){
		return (pixels[pos-1] == BLACK && pixels[pos+1] == BLACK && pixels[pos-imgWidth] == BLACK && pixels[pos+imgWidth] == BLACK);
	}
	
	private static int[] combineLogicallyAND(int[] img1, int[] img2){
		int[] combined = Arrays.copyOf(img1, img1.length);
		for (int i = 0; i < img1.length; i++) {
			if(img1[i] == BLACK && img2[i] == BLACK) {
				combined[i] = BLACK;
			} else {
				combined[i] = WHITE;
			}
		}
		return combined;
	}
}
