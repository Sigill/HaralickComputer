package HaralickComputer.core;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.Observable;

import javax.swing.JPanel;

public class DoubleImagePanel extends JPanel implements MouseMotionListener {
	private static final long serialVersionUID = -8223819172295265072L;
	
	public enum Orientation { Horizontal, Vertical };
	
	private BufferedImage leftImage = null, rightImage = null;
	private Orientation orientation = Orientation.Horizontal;
	private int xOffset, yOffset;
	private float scale;
	private MouseObservable observable;
	
	private Point mousePosition;
	
	public DoubleImagePanel() {
		this.mousePosition = new Point();
		this.observable = new MouseObservable();
		this.addMouseMotionListener(this);
	}
	
	public MouseObservable getObservable() { return this.observable; }
	
	public Point getMousePositionOnImage() {
		return this.mousePosition;
	}
	
	public void setLeftImage(BufferedImage img) {
		this.leftImage = img;
	}
	
	public void setRightImage(BufferedImage img) {
		this.rightImage = img;
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
	
	public void paintComponent(Graphics g) {
		if(leftImage != null) {
			Dimension d = getSize();
			int imageWidth, imageHeight;
			float containerRatio;
			
			if(this.orientation == Orientation.Horizontal)
				containerRatio = (float)(d.height / (float)(d.width / 2.0));
			else
				containerRatio = (float)(d.height / 2.0) / (float)(d.width);
				
			float imageRatio = leftImage.getHeight() / (float)leftImage.getWidth();
		
			if(containerRatio > imageRatio) {
				if(this.orientation == Orientation.Horizontal)
					scale = (float) ((d.width/2.0) / (float)leftImage.getWidth());
				else
					scale = (float) ((d.width) / (float)leftImage.getWidth());
			} else {
				if(this.orientation == Orientation.Horizontal)
					scale = d.height / (float)leftImage.getHeight();
				else
					scale = (float) ((d.height / 2.0) / (float)leftImage.getHeight());
			}
			
			imageWidth = (int)(leftImage.getWidth() * scale);
			imageHeight = (int)(leftImage.getHeight() * scale);
			
			if(this.orientation == Orientation.Horizontal) {
				xOffset = (int) ((d.width - 2 * imageWidth) / 3.0);
				yOffset = (d.height - imageHeight) / 2;
			} else {
				xOffset = (d.width - imageWidth) / 2;
				yOffset = (int) ((d.height - 2 * imageHeight) / 3.0);
			}
			
			g.drawImage(leftImage, xOffset, yOffset, imageWidth, imageHeight, null);
			
			if(rightImage != null) {
				if(this.orientation == Orientation.Horizontal) 
					g.drawImage(rightImage, 2 * xOffset + imageWidth, yOffset, imageWidth, imageHeight, null);
				else
					g.drawImage(rightImage, xOffset, 2 * yOffset + imageHeight, imageWidth, imageHeight, null);
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if(this.leftImage == null)
			return;
		
		int x_win = e.getX(), y_win = e.getY();
				
		mousePosition.x = (int) ((x_win - xOffset) / scale);
		mousePosition.y = (int) ((y_win - yOffset) / scale);
		
		this.observable.setChanged();
		this.observable.notifyObservers();
	}
	
	public class MouseObservable extends Observable {
		@Override
		protected synchronized void setChanged() {
			super.setChanged();
		}
	}
}
