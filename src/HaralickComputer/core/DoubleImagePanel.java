package HaralickComputer.core;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class DoubleImagePanel extends JPanel {
	private static final long serialVersionUID = -8223819172295265072L;
	
	public enum Orientation { Horizontal, Vertical };
	
	private BufferedImage leftImage = null, rightImage = null;
	private Orientation direction = Orientation.Horizontal;
	
	public void setLeftImage(BufferedImage img) {
		this.leftImage = img;
	}
	
	public void setRightImage(BufferedImage img) {
		this.rightImage = img;
	}
	
	public Orientation getDirection() {
		return this.direction;
	}
	
	public void setDirection(Orientation dir) {
		this.direction = dir;
	}
	
	public void changeDirection() {
		if(direction == Orientation.Horizontal)
			setDirection(Orientation.Vertical);
		else
			setDirection(Orientation.Horizontal);
	}
	
	public void paintComponent(Graphics g) {
		if(leftImage != null) {
			Dimension d = getSize();
			int imageWidth, imageHeight, xOffset, yOffset;
			float containerRatio;
			
			if(this.direction == Orientation.Horizontal)
				containerRatio = (float)(d.height / (float)(d.width / 2.0));
			else
				containerRatio = (float)(d.height / 2.0) / (float)(d.width);
				
			float imageRatio = leftImage.getHeight() / (float)leftImage.getWidth();
			float scale;
		
			if(containerRatio > imageRatio) {
				if(this.direction == Orientation.Horizontal)
					scale = (float) ((d.width/2.0) / (float)leftImage.getWidth());
				else
					scale = (float) ((d.width) / (float)leftImage.getWidth());
			} else {
				if(this.direction == Orientation.Horizontal)
					scale = d.height / (float)leftImage.getHeight();
				else
					scale = (float) ((d.height / 2.0) / (float)leftImage.getHeight());
			}
			
			imageWidth = (int)(leftImage.getWidth() * scale);
			imageHeight = (int)(leftImage.getHeight() * scale);
			
			if(this.direction == Orientation.Horizontal) {
				xOffset = (int) ((d.width - 2 * imageWidth) / 3.0);
				yOffset = (d.height - imageHeight) / 2;
			} else {
				xOffset = (d.width - imageWidth) / 2;
				yOffset = (int) ((d.height - 2 * imageHeight) / 3.0);
			}
			
			g.drawImage(leftImage, xOffset, yOffset, imageWidth, imageHeight, null);
			
			if(rightImage != null) {
				if(this.direction == Orientation.Horizontal) 
					g.drawImage(rightImage, 2 * xOffset + imageWidth, yOffset, imageWidth, imageHeight, null);
				else
					g.drawImage(rightImage, xOffset, 2 * yOffset + imageHeight, imageWidth, imageHeight, null);
			}
		}
	}
}
