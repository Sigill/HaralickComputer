package HaralickComputer.controllers;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import HaralickComputer.core.Bitmap2D;
import HaralickComputer.core.GLCM;
import HaralickComputer.core.TextureFeaturesImage;

public class TextureFeaturesComputer {
	private Bitmap2D imageSource, imagePosterized;
	private GLCM glcm;
	private int numberOfGrayLevels;
	private int windowSize;
	private TextureFeaturesImage _tfi;
	
	public TextureFeaturesComputer() {	
		this.imageSource = null;
		this.imagePosterized = null;
		setNumberOfGrayLevels(16);
		this.windowSize = 2;
	}
	
	public Dimension getImageDimensions() {
		return new Dimension(this.imageSource.getWidth(), this.imageSource.getHeight());
	}
	
	public int getNumberOfGraylevels() { return this.numberOfGrayLevels; }
	
	public int getImageWidth() { return this.imageSource.getWidth(); };
	public int getImageHeight() { return this.imageSource.getHeight(); };
	
	public void setNumberOfGrayLevels(int count) {
		if(this.numberOfGrayLevels == count)
			return;
		
		this.numberOfGrayLevels = count;
		this.glcm = new GLCM(this.numberOfGrayLevels);
	}
	
	public void setSizeOfWindow(int size) {
		if(this.windowSize == size)
			return;
		
		this.windowSize = size;
	}
	
	public void setImageSource(BufferedImage img) {
		this.imagePosterized = null;
		this.imageSource = new Bitmap2D(img);
		this._tfi = new TextureFeaturesImage(this.imageSource.getWidth(), this.imageSource.getHeight());
	};
	
	public void posterizeSource() {
		if(this.imageSource == null)
			return;
		
		this.imagePosterized = new Bitmap2D(this.imageSource);
		this.imagePosterized.posterize(this.numberOfGrayLevels);
	}
	
	public synchronized BufferedImage getSourceImage() { return this.imageSource.toBufferedImage(); }
	public synchronized BufferedImage getHaralickImage(int feature) { return this._tfi.toBufferedImage(feature); }
	
	public synchronized void compute() {
		int i, j;
		int w, h;
		
		if(this.imageSource == null)
			return;
		
		w = this.imageSource.getWidth();
		h = this.imageSource.getHeight();
		
		posterizeSource();
		
		for(i = 0; i < w; i++) {
			
			for(j = 0; j < h; j++) {
				
				this.glcm.reset();

				computeForPixel(this.glcm, i, j, w, h);
				
				this.glcm.normalize();
				this.glcm.compute();
				this._tfi.setFromGLCM(i, j, this.glcm);
				
			}
			
		}
	}
	
	public synchronized void computeForPixel(GLCM glcm, int x, int y, int width, int height) {
		int k, l;
		
		for(k = x - this.windowSize; k < x + this.windowSize; k++) {
			
			if((k > 0) && (k < width-1)) {
				
				for(l = y - this.windowSize; l < y + this.windowSize; l++) {
					
					if((l > 0) && (l < height-1)) {
						
						glcm.inc(this.imagePosterized.get(k, l), this.imagePosterized.get(k, l+1));
						glcm.inc(this.imagePosterized.get(k, l+1), this.imagePosterized.get(k, l));
						
					}
					
				}
				
			}
			
		}
	}

}
