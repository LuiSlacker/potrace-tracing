package de.htw.ip.main;
// Copyright (C) 2014 by Klaus Jung
// All rights reserved.
// Date: 2014-10-02

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.htw.ip.basics.BasicAlgorithms;
import de.htw.ip.basics.Path;
import de.htw.ip.potrace.ContourDodo;
import de.htw.ip.potrace.PolygonAlgorithm;

public class Binarize extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private static final int border = 10;
	private static final int maxWidth = 1600;
	private static final int maxHeight = 900;
	private static final File openPath = new File(".");
	private static final String title = "Potrace";
	private static final String author = "Goohsen-Sacker";
	private static final String initalOpen = "./images/head.png";
	
	private static JFrame frame;
	
	private ImageView dstView;				// binarized image view
	private int dstPixels[];
	
	private JLabel statusLine;				// to print some status text
	private JLabel zoomLabel = new JLabel("Zoom:");

	public Binarize() {
        super(new BorderLayout(border, border));
        
        // load the default image
        File input = new File(initalOpen);
        
        if(!input.canRead()) input = openFile(); // file not found, choose another image
        
        // create an empty destination image
        dstView = new ImageView(input);
        dstView.setMaxSize(new Dimension(maxWidth, maxHeight));
       
		// create an empty destination image


		
		// load image button
        JButton load = new JButton("Bild öffnen");
        load.addActionListener(new ActionListener() {
        	@Override
			public void actionPerformed(ActionEvent e) {
        		File input = openFile();
        		if(input != null) {
        			dstView.setZoom(1);
        			dstView.loadImage(input);
        			dstView.setMaxSize(new Dimension(maxWidth, maxHeight));
	                binarizeImage();
	                
        		}
        	}        	
        });
         
        // some status text
        statusLine = new JLabel(" ");
        
        final int sliderGranularity = 100;
        JSlider zoomSlider = new JSlider(SwingConstants.HORIZONTAL, 1*sliderGranularity, 200*sliderGranularity, 1*sliderGranularity);
        zoomSlider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				double val = (double)((JSlider)e.getSource()).getValue() / 100;
				dstView.setZoom(val);
			}
		});
        
        JCheckBox vertices = new JCheckBox("Vertices");
        vertices.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dstView.setShowVertices(vertices.isSelected() ? true: false);
			}
		});
        
        JCheckBox polygons = new JCheckBox("Polygons");
        polygons.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dstView.setShowPolygons(polygons.isSelected()? true: false);
			}
		});
        
        JCheckBox paths = new JCheckBox("Path");
        paths.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dstView.setShowPaths(paths.isSelected()? true: false);
			}
		});
        
        JCheckBox pxls = new JCheckBox("Pixels");
        pxls.setSelected(true);
        pxls.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dstView.setShowPixels(pxls.isSelected()? true: false);
			}
		});
        
        // arrange all controls
        JPanel controls = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0,border,0,0);
        controls.add(load, c);
        controls.add(zoomLabel, c);
        controls.add(zoomSlider, c);
        
        JPanel images = new JPanel(new FlowLayout());
        images.add(dstView);
        
        JPanel controlsBottom = new JPanel(new GridBagLayout());
        controlsBottom.add(pxls, c);
        controlsBottom.add(paths, c);
        controlsBottom.add(polygons, c);
        controlsBottom.add(vertices, c);
        
        add(controls, BorderLayout.NORTH);
        add(images, BorderLayout.CENTER);
        add(controlsBottom, BorderLayout.SOUTH);
        //add(statusLine, BorderLayout.SOUTH);
               
        setBorder(BorderFactory.createEmptyBorder(border,border,border,border));
        
        // perform the initial binarization
        binarizeImage();
	}
	
	private File openFile() {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Images (*.jpg, *.png, *.gif)", "jpg", "png", "gif");
        chooser.setFileFilter(filter);
        chooser.setCurrentDirectory(openPath);
        int ret = chooser.showOpenDialog(this);
        if(ret == JFileChooser.APPROVE_OPTION) {
    		frame.setTitle(title + chooser.getSelectedFile().getName());
        	return chooser.getSelectedFile();
        }
        return null;		
	}
	
	private static void createAndShowGUI() {
		// create and setup the window
		frame = new JFrame(title + " - " + author + " - " + initalOpen);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JComponent newContentPane = new Binarize();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        // display the window.
        frame.pack();
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        frame.setLocation((screenSize.width - frame.getWidth()) / 2, (screenSize.height - frame.getHeight()) / 2);
        frame.setVisible(true);
	}

	public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
			public void run() {
                createAndShowGUI();
            }
        });
	}
	
    protected void binarizeImage() {
  
        // image dimensions
        int width = dstView.getImgWidth();
        int height = dstView.getImgHeight();
    	
    	// get pixels arrays
    	dstPixels = dstView.getPixels();
    	
		long startTime = System.currentTimeMillis();
		
//		 potrace contour Algorithm
		ArrayList<Path<Point>> listpaths = ContourDodo.contourtracking(dstPixels, width, height);
		listpaths.forEach(listpath -> {
			listpath.remove(listpath.size()-1);
		});
		
		for(Path<Point> p: listpaths){
			Point a = p.get(0);
			if(dstPixels[a.y * width + a.x]==-16777216){
				
				p.setType(true);
			}else{
				p.setType(false);
			}
		}
		
		List<List<Point>> polygons = PolygonAlgorithm.optimizedPolygons(listpaths, width);

		dstView.setPaths(listpaths);
		dstView.setPolygons(polygons);
		
		long time = System.currentTimeMillis() - startTime;
		   	
		statusLine.setText("Kontourfindung mit potrace in " + time + " ms");
		dstView.setPixels(dstPixels, width, height);
        frame.pack();
    }
    
    void binarize(int pixels[], int threshold) {
    	for(int i = 0; i < pixels.length; i++) {
    		int gray = BasicAlgorithms.convertRGBtoGrayValue(pixels[i]);
    		pixels[i] = gray < threshold ? 0xff000000 : 0xffffffff;
    	}
    }

}
    
