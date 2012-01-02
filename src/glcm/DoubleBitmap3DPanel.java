package glcm;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Observable;

import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class DoubleBitmap3DPanel extends JPanel implements MouseMotionListener {
	private static final long serialVersionUID = 7839296549216684498L;
	
	public enum Orientation { Horizontal, Vertical };
	private Orientation orientation = Orientation.Horizontal;
	private int xOffset, yOffset;
	private float scale;
	
	private JSlider sliderSlice;
	private JPanel panelImage;
	
	private BufferedImage[] leftSlices = null, rightSlices = null;
	
	private MouseObservable observable;
	
	private Point mousePosition;
	
	public DoubleBitmap3DPanel() {
		this.mousePosition = new Point();
		this.observable = new MouseObservable();
		this.addMouseMotionListener(this);
		
		setLayout(new BorderLayout(0, 0));
		
		sliderSlice = new JSlider();
		sliderSlice.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider sl = (JSlider) e.getSource();
				System.out.println(sl.getValue());
				repaint();
			}
		});
		add(sliderSlice, BorderLayout.SOUTH);
		
		panelImage = new JPanel();
		add(panelImage, BorderLayout.CENTER);
	}
	
	private static BufferedImage[] slice(Bitmap3D image) {
		BufferedImage[] slices = new BufferedImage[image.dimensions[2]];
		
		for(int i = 0; i < slices.length; ++i) {
			slices[i] = new BufferedImage(image.dimensions[0], image.dimensions[1], BufferedImage.TYPE_INT_RGB);
		}
		
		Bitmap3D.ImageIterator it = image.new ImageIterator();
		short v;
		int[] coordinates;
		Color c;
		while(it.isNotAtTheEnd()) {
			v = it.get();
			coordinates = it.getCoordinates();
			c = new Color(v, v, v);
			slices[coordinates[2]].setRGB(coordinates[0], coordinates[1], c.getRGB());
			it.move();
		}
		
		return slices;
	}
	
	public void setLeftImage(Bitmap3D image) {
		this.leftSlices = null;
		if(image == null) return;
		sliderSlice.setMaximum(image.dimensions[2] - 1);
		this.leftSlices = slice(image);
	}
	
	public void setRightImage(Bitmap3D image) {
		this.rightSlices = null;
		if(image == null) return;
		this.rightSlices = slice(image);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		
		int slice = sliderSlice.getValue();
		BufferedImage leftSlice, rightSlice;
		
		if(leftSlices != null) {
			Dimension d = this.panelImage.getSize();
			int imageWidth, imageHeight;
			float containerRatio;
			leftSlice = leftSlices[slice];
			
			if(this.orientation == Orientation.Horizontal)
				containerRatio = (float)(d.height / (float)(d.width / 2.0));
			else
				containerRatio = (float)(d.height / 2.0) / (float)(d.width);
				
			float imageRatio = leftSlice.getHeight() / (float)leftSlice.getWidth();
		
			if(containerRatio > imageRatio) {
				if(this.orientation == Orientation.Horizontal)
					scale = (float) ((d.width/2.0) / (float)leftSlice.getWidth());
				else
					scale = (float) ((d.width) / (float)leftSlice.getWidth());
			} else {
				if(this.orientation == Orientation.Horizontal)
					scale = d.height / (float)leftSlice.getHeight();
				else
					scale = (float) ((d.height / 2.0) / (float)leftSlice.getHeight());
			}
			
			imageWidth = (int)(leftSlice.getWidth() * scale);
			imageHeight = (int)(leftSlice.getHeight() * scale);
			
			if(this.orientation == Orientation.Horizontal) {
				xOffset = (int) ((d.width - 2 * imageWidth) / 3.0);
				yOffset = (d.height - imageHeight) / 2;
			} else {
				xOffset = (d.width - imageWidth) / 2;
				yOffset = (int) ((d.height - 2 * imageHeight) / 3.0);
			}
			
			g.drawImage(leftSlice, xOffset, yOffset, imageWidth, imageHeight, null);
			
			if(rightSlices != null) {
				rightSlice = rightSlices[slice];
				if(this.orientation == Orientation.Horizontal) 
					g.drawImage(rightSlice, 2 * xOffset + imageWidth, yOffset, imageWidth, imageHeight, null);
				else
					g.drawImage(rightSlice, xOffset, 2 * yOffset + imageHeight, imageWidth, imageHeight, null);
			}
		}
	}
	
	public Orientation getOrientation() {
		return this.orientation;
	}
	
	public void setOrientation(Orientation dir) {
		this.orientation = dir;
	}
	
	public void changeOrientation() {
		if(orientation == Orientation.Horizontal)
			setOrientation(Orientation.Vertical);
		else
			setOrientation(Orientation.Horizontal);
	}
	
	public int getSliceNumber() {
		return this.sliderSlice.getValue();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if(this.leftSlices == null)
			return;
		
		int x_win = e.getX(), y_win = e.getY();
				
		mousePosition.x = (int) ((x_win - xOffset) / scale);
		mousePosition.y = (int) ((y_win - yOffset) / scale);
		
		this.observable.setChanged();
		this.observable.notifyObservers();
	}
	
public MouseObservable getObservable() { return this.observable; }
	
	public Point getMousePositionOnImage() {
		return this.mousePosition;
	}
	
	public class MouseObservable extends Observable {
		@Override
		protected synchronized void setChanged() {
			super.setChanged();
		}
	}
}
