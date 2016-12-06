package de.htw.ip.potrace;

import java.awt.Point;
import java.util.ArrayList;

import de.htw.ip.basics.DirectionOrigin;
import de.htw.ip.basics.Labeling;
import de.htw.ip.basics.NextDirection;
import de.htw.ip.basics.Path;
import de.htw.ip.main.ImageView;

public class ContourDodo {
	
	public static ArrayList<Path> contourtracking(int[] pixels, int width, int height) {

//		int[] binarizedPix = Labeling.isoDataAlgo(pixels);
//		int[] pixelsLabeled = Labeling.initialLabeling(width, height, binarizedPix);
		int[] pixelsLabeled = Labeling.binarizeAndLabel(pixels, 128);
		ArrayList<Path> listOfPaths = new ArrayList<Path>();

		portraceAlgorithm(pixelsLabeled, listOfPaths, width, height);


//		for (Path p : listOfPaths) {
//			for (int i = 0; i < p.size(); i++) {
//				System.out.print("[" + p.get(i).getX() + ", " + p.get(i).getY() + "]");
//			}
//			System.out.println("");
//		}
		return listOfPaths;
	}

	public static void portraceAlgorithm(int[] pixelsLabeled, ArrayList<Path> listOfPaths, int width, int height) {

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (pixelsLabeled[y * width + x] == 1) {

					NextDirection nextdirection = NextDirection.RIGHT;
					Path path = new Path();

					DirectionOrigin origindirection = DirectionOrigin.DOWN;
					path.add(new Point(x, y));
					path.add(new Point(x, y + 1));

					int index = 1;

					while (!(path.get(0).equals(path.get(index)))) {

						Point currentPathPoint = (Point) path.get(index);
						x = currentPathPoint.x;
						y = currentPathPoint.y;
						index++;

						int[] neigbours = new int[4];

						int[] labelNeigbour = new int[4];

						// neigbours = findNeigbours(width, height, x, y);
						//
						// //Check if y<=height und x<=width
						// for (int i = 0; i < neigbours.length; i++) {
						// if (neigbours[i] == -1) {
						// labelNeigbour[i] = 0;
						// } else {
						// labelNeigbour[i] = pixelsLabeled[neigbours[i]];
						// }
						//
						// }

						neigbours[0] = (y - 1) * width + (x - 1);
						neigbours[1] = (y - 1) * width + x;
						neigbours[2] = y * width + (x - 1);
						neigbours[3] = y * width + x;

						labelNeigbour[0] = pixelsLabeled[neigbours[0]];
						labelNeigbour[1] = pixelsLabeled[neigbours[1]];
						labelNeigbour[2] = pixelsLabeled[neigbours[2]];
						labelNeigbour[3] = pixelsLabeled[neigbours[3]];

						int labelImportantOne = 0;
						int labelImportantTwo = 0;

						switch (origindirection) {
						case DOWN:
							labelImportantOne = labelNeigbour[2];
							labelImportantTwo = labelNeigbour[3];

							if (labelImportantOne == 1 && labelImportantTwo == 1) {
								nextdirection = NextDirection.RIGHT;
							} else if (labelImportantOne == 0 && labelImportantTwo == 0) {
								nextdirection = NextDirection.LEFT;
							} else if (labelImportantTwo == 1) {
								nextdirection = NextDirection.STRAIGHT;
							} else {
								nextdirection = NextDirection.RIGHT;
							}

							break;

						case LEFT:
							labelImportantOne = labelNeigbour[0];
							labelImportantTwo = labelNeigbour[2];

							if (labelImportantOne == 1 && labelImportantTwo == 1) {
								nextdirection = NextDirection.RIGHT;
							} else if (labelImportantOne == 0 && labelImportantTwo == 0) {
								nextdirection = NextDirection.LEFT;
							} else if (labelImportantTwo == 1) {
								nextdirection = NextDirection.STRAIGHT;
							} else {
								nextdirection = NextDirection.RIGHT;
							}

							break;

						case UP:
							labelImportantOne = labelNeigbour[0];
							labelImportantTwo = labelNeigbour[1];

							if (labelImportantOne == 1 && labelImportantTwo == 1) {
								nextdirection = NextDirection.RIGHT;
							} else if (labelImportantOne == 0 && labelImportantTwo == 0) {
								nextdirection = NextDirection.LEFT;
							} else if (labelImportantOne == 1) {
								nextdirection = NextDirection.STRAIGHT;
							} else {
								nextdirection = NextDirection.RIGHT;
							}

							break;

						case RIGHT:
							labelImportantOne = labelNeigbour[1];
							labelImportantTwo = labelNeigbour[3];

							if (labelImportantOne == 1 && labelImportantTwo == 1) {
								nextdirection = NextDirection.RIGHT;
							} else if (labelImportantOne == 0 && labelImportantTwo == 0) {
								nextdirection = NextDirection.LEFT;
							} else if (labelImportantOne == 1) {
								nextdirection = NextDirection.STRAIGHT;
							} else {
								nextdirection = NextDirection.RIGHT;
							}

							break;
						}

						if (origindirection == DirectionOrigin.DOWN && nextdirection == NextDirection.STRAIGHT
								|| origindirection == DirectionOrigin.LEFT && nextdirection == NextDirection.LEFT
								|| origindirection == DirectionOrigin.RIGHT && nextdirection == NextDirection.RIGHT) {

							path.add(index, new Point(x, y + 1));
							origindirection = DirectionOrigin.DOWN;

						} else if (origindirection == DirectionOrigin.DOWN && nextdirection == NextDirection.LEFT
								|| origindirection == DirectionOrigin.UP && nextdirection == NextDirection.RIGHT
								|| origindirection == DirectionOrigin.RIGHT
										&& nextdirection == NextDirection.STRAIGHT) {

							path.add(index, new Point(x + 1, y));
							origindirection = DirectionOrigin.RIGHT;

						} else if (origindirection == DirectionOrigin.DOWN && nextdirection == NextDirection.RIGHT
								|| origindirection == DirectionOrigin.LEFT && nextdirection == NextDirection.STRAIGHT
								|| origindirection == DirectionOrigin.UP && nextdirection == NextDirection.LEFT) {

							path.add(index, new Point(x - 1, y));
							origindirection = DirectionOrigin.LEFT;

						} else if (origindirection == DirectionOrigin.LEFT && nextdirection == NextDirection.RIGHT
								|| origindirection == DirectionOrigin.UP && nextdirection == NextDirection.STRAIGHT
								|| origindirection == DirectionOrigin.RIGHT && nextdirection == NextDirection.LEFT) {

							path.add(index, new Point(x, y - 1));
							origindirection = DirectionOrigin.UP;

						}

					}

					for (int i = 1; i < path.size(); i++) {
						Point thisP = (Point) path.get(i - 1);
						Point nextP = (Point) path.get(i);

						if ((thisP.y - nextP.y) == 1) {
							pixelsLabeled = invertTheRestOfTheLine(pixelsLabeled, width, nextP.x, nextP.y);

						} else if ((thisP.y - nextP.y) == -1) {
							pixelsLabeled = invertTheRestOfTheLine(pixelsLabeled, width, thisP.x, thisP.y);
						}
					}

					listOfPaths.add(path);
					x = width;
					y = height;
				}
			}
		}

		boolean allWhite = true;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (pixelsLabeled[y * width + x] == 1) {
					allWhite = false;
				}
			}
		}

		if (!allWhite) {
			portraceAlgorithm(pixelsLabeled, listOfPaths, width, height);
		}

	}

	protected int[] findNeigbours(int width, int height, int x, int y) {
		int[] neigbours = new int[4];
		neigbours[0] = (y - 1) * width + (x - 1);
		neigbours[1] = (y - 1) * width + x;
		neigbours[2] = y * width + (x - 1);
		neigbours[3] = y * width + x;

		if (x == 0 && y == 0) {
			neigbours[0] = neigbours[1] = neigbours[2] = -1;

		} else if (y >= (height - 1) && x >= (width - 1)) {
			neigbours[1] = neigbours[2] = neigbours[3] = -1;

		} else if (x == 0 && y >= (height - 1)) {
			neigbours[0] = neigbours[2] = neigbours[3];

		} else if (y == 0 && x >= (width - 1)) {
			neigbours[0] = neigbours[1] = neigbours[3] = -1;

		} else if (x == 0) {
			neigbours[0] = neigbours[2] = -1;

		} else if (y == 0) {
			neigbours[0] = neigbours[1] = -1;

		} else if (y >= (height - 1)) {
			neigbours[2] = neigbours[3] = -1;

		} else if (x >= (width - 1)) {
			neigbours[1] = neigbours[3] = -1;

		}
		return neigbours;
	}

	protected static int[] invertTheRestOfTheLine(int[] pixels, int width, int x, int y) {

		for (int restX = x; restX < width; restX++) {
			if (pixels[(y) * width + restX] == 0) {
				pixels[(y) * width + restX] = 1;
			} else {
				pixels[(y) * width + restX] = 0;
			}
		}
		return pixels;
	}
}
