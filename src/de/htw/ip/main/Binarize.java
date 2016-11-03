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
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import de.htw.ip.basics.BasicAlgorithms;

public class Binarize extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private static final int border = 10;
	private static final int maxWidth = 400;
	private static final int maxHeight = 400;
	private static final File openPath = new File(".");
	private static final String title = "Binarisierung";
	private static final String author = "Goohsen";
	private static final String initalOpen = "sample.png";
	
	private static JFrame frame;
	
	private ImageView srcView;				// source image view
	private ImageView dstView;				// binarized image view
	private int dstPixels[];
	
	private JComboBox<String> methodList;	// the selected binarization method
	private JLabel statusLine;				// to print some status text
	


	public Binarize() {
        super(new BorderLayout(border, border));
        
        // load the default image
        File input = new File(initalOpen);
        
        if(!input.canRead()) input = openFile(); // file not found, choose another image
        
        srcView = new ImageView(input);
        srcView.setMaxSize(new Dimension(maxWidth, maxHeight));
       
		// create an empty destination image
		dstView = new ImageView(srcView.getImgWidth(), srcView.getImgHeight());
		dstView.setMaxSize(new Dimension(maxWidth, maxHeight));
		
		// load image button
        JButton load = new JButton("Bild öffnen");
        load.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		File input = openFile();
        		if(input != null) {
	        		srcView.loadImage(input);
	        		srcView.setMaxSize(new Dimension(maxWidth, maxHeight));
	                binarizeImage();
        		}
        	}        	
        });
         
        // selector for the binarization method
        JLabel methodText = new JLabel("Methode:");
//        String[] methodNames = {"Manueller Schwellwert", "Iso-Data-Algorithmus", "Regioning"};
        String[] methodNames = {
	        		"Depth First", "Depth First Optimized", "Breadt First", 
	        		"Breadt First Optimized", "Sequential"
        		};
        
        methodList = new JComboBox<String>(methodNames);
        methodList.setSelectedIndex(0);		// set initial method
        methodList.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
                binarizeImage();
        	}
        });
        
        JButton erodeBtn = new JButton("Erode");
        erodeBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dstPixels = BasicAlgorithms.erode(dstPixels, srcView.getImgWidth(), srcView.getImgHeight());
				dstView.setPixels(dstPixels, srcView.getImgWidth(), srcView.getImgHeight());
			}
		});
        
        JButton dilateBtn = new JButton("Dilate");
        dilateBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dstPixels = BasicAlgorithms.dilateOptimized(dstPixels, srcView.getImgWidth(), srcView.getImgHeight());
				dstView.setPixels(dstPixels, srcView.getImgWidth(), srcView.getImgHeight());
			}
		});
        
        JButton invertBtn = new JButton("Invert");
        invertBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dstPixels = BasicAlgorithms.invert(dstPixels);
				dstView.setPixels(BasicAlgorithms.invert(dstPixels), srcView.getImgWidth(), srcView.getImgHeight());
			}
		});
        
        // some status text
        statusLine = new JLabel(" ");
        
        final int sliderGranularity = 100;
        JSlider zoomSlider = new JSlider(JSlider.HORIZONTAL, 1*sliderGranularity, 20*sliderGranularity, 1*sliderGranularity);
        zoomSlider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				double val = (double)((JSlider)e.getSource()).getValue() / 100;
				dstView.setZoom(val);
			}
		});
        
        // arrange all controls
        JPanel controls = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0,border,0,0);
        controls.add(load, c);
        controls.add(methodText, c);
        controls.add(methodList, c);
        controls.add(zoomSlider, c);
        
        JPanel images = new JPanel(new FlowLayout());
        images.add(srcView);
        images.add(dstView);
        
        images.add(erodeBtn);
        images.add(dilateBtn);
        images.add(invertBtn);
        
        add(controls, BorderLayout.NORTH);
        add(images, BorderLayout.CENTER);
        add(statusLine, BorderLayout.SOUTH);
               
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
            public void run() {
                createAndShowGUI();
            }
        });
	}
	
	
    protected void binarizeImage() {
  
        String methodName = (String)methodList.getSelectedItem();
        
        // image dimensions
        int width = srcView.getImgWidth();
        int height = srcView.getImgHeight();
    	
    	// get pixels arrays
    	int srcPixels[] = srcView.getPixels();
    	dstPixels = java.util.Arrays.copyOf(srcPixels, srcPixels.length);
    	
    	int threshold;
    	
//    	//TODO uncomment for Benchmark (and comment entire switch statement) --------
//    	threshold = BasicAlgorithms.getIsoDataThreshold(dstPixels);
//		binarize(dstPixels, threshold);
//    	System.out.printf("Benchmark FloodFill Depth First: %s\n", RegioningBenchmark.benchmarkRegioningFloodFill(dstPixels, width, height, FloodFill.DEPTH_FIRST));
//    	System.out.printf("Benchmark FloodFill Depth First Optimized: %s\n", RegioningBenchmark.benchmarkRegioningFloodFill(dstPixels, width, height, FloodFill.DEPTH_FIRST_OPTIMIZED));
//    	System.out.printf("Benchmark FloodFill Breadth First: %s\n", RegioningBenchmark.benchmarkRegioningFloodFill(dstPixels, width, height, FloodFill.BREADTH_FIRST));
//    	System.out.printf("Benchmark FloodFill Breadth First Optimized: %s\n", RegioningBenchmark.benchmarkRegioningFloodFill(dstPixels, width, height, FloodFill.BREADTH_FIRST_OPTIMIZED));
//    	System.out.printf("Benchmark Sequential Regioning: %s\n\n", RegioningBenchmark.benchmarkSequentialRegioning(dstPixels, width, height));
//    	// End Benchmark --------------------------------------------------------
    	
    	String message = "Binarisieren mit \"" + methodName + "\"";

    	statusLine.setText(message);

		long startTime = System.currentTimeMillis();
		
		threshold = BasicAlgorithms.getIsoDataThreshold(dstPixels);
		binarize(dstPixels, threshold);
		
//    	switch(methodList.getSelectedIndex()) {
//    	case 0:	// 50% Schwellwert
//    		thresholdSlider.setEnabled(true);
//    		binarize(dstPixels, thresholdSlider.getValue());
//    		break;
//    	case 1:	// ISO-Data-Algorithmus
//    		thresholdSlider.setEnabled(false);
//    		threshold = BasicAlgorithms.getIsoDataThreshold(dstPixels);
//    		binarize(dstPixels, threshold);
//    		thresholdSlider.setValue(threshold);
//    		break;
   

		long time = System.currentTimeMillis() - startTime;
		   	
        dstView.setPixels(dstPixels, width, height);
        
        //dstView.saveImage("out.png");
    	
        frame.pack();
        
    	statusLine.setText(message + " in " + time + " ms");
    }
    
    void binarize(int pixels[], int threshold) {
    	for(int i = 0; i < pixels.length; i++) {
    		int gray = BasicAlgorithms.convertRGBtoGrayValue(pixels[i]);
    		pixels[i] = gray < threshold ? 0xff000000 : 0xffffffff;
    	}
    }
    

}
    
