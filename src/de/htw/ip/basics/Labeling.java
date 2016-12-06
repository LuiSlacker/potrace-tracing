package de.htw.ip.basics;
import java.util.Stack;

public class Labeling {

	public static int[] findShapes(int[] pixels, int width, int height) {
		int pos;
		int label = 2;
		int[] pixelsLabeled = new int[pixels.length];
		int[] binarizedPix = isoDataAlgo(pixels);

		pixelsLabeled = initialLabeling(width, height, binarizedPix);

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				pos = y * width + x;

				if (pixelsLabeled[pos] == 1) {
						pixelsLabeled = findShapesDepthFirst(pixelsLabeled, x, y, label, width, height);
					label++;
				}
			}
		}
		
		return pixels;
	}
	
	private static int[] findShapesDepthFirst(int[] pixels, int u, int v, int label, int width, int height) {

		Stack<Integer[]> stack = new Stack<Integer[]>();
		Integer[] xy = { v, u };
		stack.push(xy);
		while (!stack.empty()) {
			Integer[] currentxy = stack.pop();
			int y = currentxy[0];
			int x = currentxy[1];
			int newpos = y * width + x;

			if ((y >= 0) && (y < height) && (x >= 0) && (x < width)) {
				if ((pixels[newpos] == 1)) {
					pixels[newpos] = label;
					//innerhalb des Bildes liegen oder Vordergrundpixel (I(u,v)=1) 
					if(isInImageAndForegroundStatic(x, y-1, pixels, width, height))stack.push(new Integer[] { y - 1, x });
					if(isInImageAndForegroundStatic(x+1, y-1, pixels, width, height))stack.push(new Integer[] { y - 1, x + 1 });
					if(isInImageAndForegroundStatic(x+1, y, pixels, width, height))stack.push(new Integer[] { y, x + 1 });
					if(isInImageAndForegroundStatic(x+1, y+1, pixels, width, height))stack.push(new Integer[] { y + 1, x + 1 });
					if(isInImageAndForegroundStatic(x, y+1, pixels, width, height))stack.push(new Integer[] { y + 1, x });
					if(isInImageAndForegroundStatic(x-1, y+1, pixels, width, height))stack.push(new Integer[] { y + 1, x - 1 });
					if(isInImageAndForegroundStatic(x-1, y, pixels, width, height))stack.push(new Integer[] { y, x - 1 });
					if(isInImageAndForegroundStatic(x-1, y-1, pixels, width, height))stack.push(new Integer[] { y - 1, x - 1 });
				}
			}
		}
		return pixels;
	}
	
	private static boolean isInImageAndForegroundStatic(int x, int y, int[] pixel, int width, int height){

		if((y >= 0) && (y < height) && (x >= 0) && (x < width) && (pixel[y * width + x]==1) ){
			return true;
		}else{
			return false;
		}
		
	}
	
	static int[] binarize(int pixels[], int schwellwert) {
		int[] newpix = new int[pixels.length];
		for (int i = 0; i < pixels.length; i++) {
			int gray = ((pixels[i] & 0xff) + ((pixels[i] & 0xff00) >> 8) + ((pixels[i] & 0xff0000) >> 16)) / 3;
			newpix[i] = gray < schwellwert ? 0xff000000 : 0xffffffff;
		}
		return newpix;
	}
	
	public static int[] binarizeAndLabel(int pixels[], int schwellwert) {
		int[] newpix = new int[pixels.length];
		for (int i = 0; i < pixels.length; i++) {
			int gray = ((pixels[i] & 0xff) + ((pixels[i] & 0xff00) >> 8) + ((pixels[i] & 0xff0000) >> 16)) / 3;
			newpix[i] = gray < schwellwert ? 1 : 0;
		}
		return newpix;
	}
	
	protected static int[] isoDataAlgo(int pixels[]) {
		int colors[] = new int[pixels.length];
		for (int i = 0; i < pixels.length; i++) {
			colors[i] = ((pixels[i] & 0xff) + ((pixels[i] & 0xff00) >> 8) + ((pixels[i] & 0xff0000) >> 16)) / 3;
		}
		int[] histogram = calcHistogram(colors);
		int schwellwert = 0;
		int tmpSchwellwert = 0;
		boolean isfirstSchwellwert = true;

		if (isfirstSchwellwert) {
			schwellwert = findFirstSchwellwert(histogram);
			isfirstSchwellwert = false;
		}

		double[] p = calcP(pixels, histogram);

		while (!(schwellwert == tmpSchwellwert) || (schwellwert == tmpSchwellwert + 1)
				|| (schwellwert == tmpSchwellwert - 1)) {
			tmpSchwellwert = schwellwert;

			double pA = 0;
			double sumPjA = 0;
			for (int i = 0; i < schwellwert; i++) {
				pA += p[i];
				sumPjA += i * p[i];
			}
			double pB = 0;
			double sumPjB = 0;
			for (int i = schwellwert; i < 256; i++) {
				pB += p[i];
				sumPjB += i * p[i];
			}

			double mittelWertA = (1 / pA * sumPjA);
			double mittelWertB = (1 / pB * sumPjB);
			schwellwert = (int) ((mittelWertA + mittelWertB) / 2);
		}


		return binarize(pixels, schwellwert);

	}

	private static int findFirstSchwellwert(int[] histogram) {
		int minIndex = 0;
		int maxIndex = 256;
		boolean first = true;
		for (int i = 0; i < histogram.length; i++) {
			if (histogram[i] != 0) {
				if (first) {
					minIndex = i;
					first = false;
				}
				maxIndex = i;
			}
		}
		int schwellwert = (minIndex + maxIndex) / 2;
		return schwellwert;
	}

	private static double[] calcP(int[] pixels, int[] histogram) {
		double[] p = new double[256];

		for (int i = 0; i < histogram.length; i++) {
			p[i] = ((double) histogram[i]) / pixels.length;
		}
		return p;
	}

	private static int[] calcHistogram(int[] data) {
		final int[] result = new int[256];
		java.util.Arrays.fill(result, 0);
		for (int d : data) {
			result[d] += 1;
		}
		return result;
	}
	
	protected static int[] initialLabeling(int width, int height, int[] binarizedPix) {
		int pos;
		int[] pixelsLabeled = new int[binarizedPix.length];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				pos = y * width + x;

				int whiteOrBlack = ((binarizedPix[pos] & 0xff) + ((binarizedPix[pos] & 0xff00) >> 8)
						+ ((binarizedPix[pos] & 0xff0000) >> 16)) / 3;

				if (whiteOrBlack == 255) {
					pixelsLabeled[pos] = 0;
				} else {
					pixelsLabeled[pos] = 1;
				}
			}
		}
		return pixelsLabeled;
	}
	
	
}
