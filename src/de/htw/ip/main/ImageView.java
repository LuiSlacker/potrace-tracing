package de.htw.ip.main;
// Copyright (C) 2010 by Klaus Jung
// All rights reserved.
// Date: 2010-03-15

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import de.htw.ip.basics.Path;


public class ImageView extends JScrollPane{

	private static final long serialVersionUID = 1L;
	
	private ImageScreen	screen = null;
	private Dimension maxSize = null;
	private int borderX = -1;
	private int borderY = -1;
	private double maxViewMagnification = 0.0;		// use 0.0 to disable limits 
	private boolean keepAspectRatio = true;
	private boolean centered = true;
	
	int pixels[] = null;		// pixel array in ARGB format
	private double zoom = 1.0;
	
	public double getZoom() {
		return zoom;
	}

	public void setZoom(double zoom) {
		this.zoom = zoom;
		screen.revalidate();
	}

	public ImageView(int width, int height) {
		// construct empty image of given size
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		init(bi, true);
	}
	
	public ImageView(File file) {
		// construct image from file
		loadImage(file);
	}
	
	public void setMaxSize(Dimension dim) {
		// limit the size of the image view
		maxSize = new Dimension(dim);
		
		Dimension size = new Dimension(maxSize);
		if(size.width - borderX > screen.image.getWidth()) size.width = screen.image.getWidth() + borderX;
		if(size.height - borderY > screen.image.getHeight()) size.height = screen.image.getHeight() + borderY;
		setPreferredSize(size);
	}
	
	public int getImgWidth() {
		return screen.image.getWidth();
	}

	public int getImgHeight() {
		return screen.image.getHeight();
	}
	
	public void resetToSize(int width, int height) {
		// resize image and erase all content
		if(width == getImgWidth() && height == getImgHeight()) return;
		
		screen.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		pixels = new int[getImgWidth() * getImgHeight()];
		screen.image.getRGB(0, 0, getImgWidth(), getImgHeight(), pixels, 0, getImgWidth());
		
		Dimension size = new Dimension(maxSize);
		if(size.width - borderX > width) size.width = width + borderX;
		if(size.height - borderY > height) size.height = height + borderY;
		setPreferredSize(size);

		screen.invalidate();
		screen.repaint();
	}
	
	public int[] getPixels() {
		// get reference to internal pixels array
		if(pixels == null) {
			pixels = new int[getImgWidth() * getImgHeight()];
			screen.image.getRGB(0, 0, getImgWidth(), getImgHeight(), pixels, 0, getImgWidth());
		}
		return pixels;
	}

	public void applyChanges() {
		// if the pixels array obtained by getPixels() has been modified,
		// call this method to make your changes visible
		if(pixels != null) setPixels(pixels);
	}
	
	public void setPixels(int[] pix) {
		// set pixels with same dimension
		setPixels(pix, getImgWidth(), getImgHeight());
	}
	
	public void setPixels(int[] pix, int width, int height) {
		// set pixels with arbitrary dimension
		if(pix == null || pix.length != width * height) throw new IndexOutOfBoundsException();
	
		if(width != getImgWidth() || height != getImgHeight()) {
			// image dimension changed
			screen.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			pixels = null;
		}
		
		screen.image.setRGB(0, 0, width, height, pix, 0, width);
		
		if(pixels != null && pix != pixels) {
			// update internal pixels array
			System.arraycopy(pix, 0, pixels, 0, Math.min(pix.length, pixels.length));
		}
		
		Dimension size = new Dimension(maxSize);
		if(size.width - borderX > width) size.width = width + borderX;
		if(size.height - borderY > height) size.height = height + borderY;
		setPreferredSize(size);

		screen.invalidate();
		screen.repaint();
	}
	
	public double getMaxViewMagnification() {
		return maxViewMagnification;
	}
	
	public ImageScreen getScreen(){
		return screen;
	}
	
	// set 0.0 to disable limits
	//
	public void setMaxViewMagnification(double mag) {
		maxViewMagnification = mag;
	}
	
	public boolean getKeepAspectRatio() {
		return keepAspectRatio;
	}
	
	public void setKeepAspectRatio(boolean keep) {
		keepAspectRatio = keep;
	}
	
	public void setCentered(boolean centered) {
		this.centered = centered;
	}

	public void printText(int x, int y, String text) {
		Graphics2D g = screen.image.createGraphics();
		 
		Font font = new Font("TimesRoman", Font.BOLD, 12);
		g.setFont(font);
		g.setPaint(Color.black);
		g.drawString(text, x, y);		
		g.dispose();
		
		updatePixels();	// update the internal pixels array
	}
	
	public void clearImage() {
		Graphics2D g = screen.image.createGraphics();
		
		g.setColor(Color.white);
		g.fillRect(0, 0, getImgWidth(), getImgHeight());
		g.dispose();

		updatePixels();	// update the internal pixels array
	}
	
	public void loadImage(File file) {
		// load image from file
		BufferedImage bi = null;
		boolean success = false;
		
		try {
			bi = ImageIO.read(file);
			success = true;
		} catch (Exception e) {
   		 	JOptionPane.showMessageDialog(this, "Bild konnte nicht geladen werden.", "Fehler", JOptionPane.ERROR_MESSAGE);
   		 	bi = new BufferedImage(200, 150, BufferedImage.TYPE_INT_RGB);
		}
				
		init(bi, !success);
		
		if(!success) printText(5, getImgHeight()/2, "Bild konnte nicht geladen werden.");
	}
	
	public void saveImage(String fileName) {
		try {
			File file = new File(fileName);
			String ext = (fileName.lastIndexOf(".")==-1)?"":fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());
			if(!ImageIO.write(screen.image, ext, file)) throw new Exception("Image save failed");
		} catch(Exception e)  {
   		 	JOptionPane.showMessageDialog(this, "Bild konnte nicht geschrieben werden.", "Fehler", JOptionPane.ERROR_MESSAGE);			
		}
	}

	private void init(BufferedImage bi, boolean clear)
	{
		screen = new ImageScreen(bi);
		setViewportView(screen);
				
		maxSize = new Dimension(getPreferredSize());
		
		if(borderX < 0) borderX = maxSize.width - bi.getWidth();
		if(borderY < 0) borderY = maxSize.height - bi.getHeight();
		
		if(clear) clearImage();
		
		pixels = null;
	}
	
	private void updatePixels() {
		if(pixels != null) screen.image.getRGB(0, 0, getImgWidth(), getImgHeight(), pixels, 0, getImgWidth());
	}
	
	class ImageScreen extends JComponent {
		
		private static final long serialVersionUID = 1L;
		
		private BufferedImage image = null;
		private List<List<Integer>> paths = null;

		public ImageScreen(BufferedImage bi) {
			super();
			image = bi;
		}
		
		/**
		 * Sets the contour path of all closed regions
		 *
		 */
		public void setPaths(List<List<Integer>> paths){
			this.paths = paths;
		}
		
		public void paintComponent(Graphics g) {
			
			if (image != null) {
				Rectangle r = this.getBounds();
								
				// limit image view magnification
				if(maxViewMagnification > 0.0) {
					int maxWidth = (int)(image.getWidth() * maxViewMagnification + 0.5);
					int maxHeight = (int)(image.getHeight() * maxViewMagnification + 0.5);
					
					if(r.width  > maxWidth) r.width = maxWidth;
					if(r.height  > maxHeight) r.height = maxHeight;
				}
				
				// keep aspect ratio
				if(keepAspectRatio) {
					double ratioX = (double)r.width / image.getWidth();
					double ratioY = (double)r.height / image.getHeight();
					if(ratioX < ratioY)
						r.height = (int)(ratioX * image.getHeight() + 0.5);
					else
						r.width = (int)(ratioY * image.getWidth() + 0.5);
				}
				
				int offsetX = 0;
				int offsetY = 0;
				Graphics2D g2 = (Graphics2D) g;
				
				// set background for regions not covered by image
				if(r.height < getBounds().height) {
					g.setColor(SystemColor.window);
					if(centered) offsetY = (getBounds().height - r.height)/2;
					g.fillRect(0, 0, getBounds().width, offsetY);
					g.fillRect(0, r.height + offsetY, getBounds().width, getBounds().height - r.height - offsetY);
				}
				
				if(r.width < getBounds().width) {
					g.setColor(SystemColor.window);
					if(centered) offsetX = (getBounds().width - r.width)/2;
					g.fillRect(0, offsetY, offsetX, r.height);
					g.fillRect(r.width + offsetX, offsetY, getBounds().width - r.width - offsetX, r.height);
				}
				
				// draw image
				g.drawImage(image, offsetX, offsetY, r.width, r.height, this);

				// draw grid
				if (zoom > 4) {
					int w = (int)(image.getWidth() * zoom);
					int h = (int)(image.getHeight() * zoom);
					g.setColor(SystemColor.lightGray);
					g2.setStroke(new BasicStroke(2));
					for (double x = 0; x < w; x+= zoom) {
						g.drawLine((int)x+offsetX, 0+offsetY, (int)x+offsetX, h+offsetY);
					}
					for (double y = 0; y < h; y+= zoom) {
						g.drawLine(0+offsetX, (int)y+offsetY, w+offsetX, (int)y+offsetY);
					}
				}
				if(this.paths != null){
					g2.setStroke(new BasicStroke(2));
					for (int i = 0; i < paths.size(); i++) {
						Path<Integer> path = (Path<Integer>)paths.get(i);
						g.setColor(path.getType() ? SystemColor.RED: SystemColor.GREEN); 
						for (int j = 1; j < path.size(); j++) {
							int current = path.get(j);
							int currentX = current % (int)(image.getWidth());
							int currentY = current / (int)(image.getWidth());
							int penultimate = path.get(j-1);
							int penultimateX = penultimate % (int)image.getWidth();
							int penultimateY = penultimate / (int)image.getWidth();
							g.drawLine(
									(int)((penultimateX + offsetX) * zoom),
									(int)((penultimateY + offsetY) * zoom),
									(int)((currentX + offsetX) * zoom),
									(int)((currentY + offsetY) * zoom));
							
						}
						
					}
				}
			}
		}
		
		public Dimension getPreferredSize() {
			if(image != null) 
				return new Dimension((int) (zoom * image.getWidth()), (int) (zoom * image.getHeight()));
			else
				return new Dimension(100, 60);
		}
	}

}
