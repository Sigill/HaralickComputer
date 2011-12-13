package HaralickComputer.controllers;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;

import HaralickComputer.core.Bitmap2D;
import HaralickComputer.core.GLCM;
import HaralickComputer.core.TextureFeaturesImage;

public class TextureFeaturesComputer {
	private Bitmap2D imageSource, imagePosterized;
	private GLCM glcm;
	private int numberOfGrayLevels;
	private int windowRadius;
	private TextureFeaturesImage _tfi;
	private int xOffset, yOffset;
	private boolean symmetricOffset;
	
	public final static int DEFAULT_X_OFFSET = 1;
	public final static int DEFAULT_Y_OFFSET = 0;
	public final static int DEFAULT_NUMBER_OF_GRAYLEVELS = 16;
	public final static int DEFAULT_WINDOW_RADIUS = 2;
	public final static boolean DEFAULT_SYMMETRIC_OFFSET = false;
	
	public TextureFeaturesComputer() {	
		this.imageSource = null;
		this.imagePosterized = null;
		setNumberOfGrayLevels(DEFAULT_NUMBER_OF_GRAYLEVELS);
		this.windowRadius = DEFAULT_WINDOW_RADIUS;
		this.xOffset = DEFAULT_X_OFFSET;
		this.yOffset = DEFAULT_Y_OFFSET;
		this.symmetricOffset = DEFAULT_SYMMETRIC_OFFSET;
	}
	
	public Dimension getImageDimensions() {
		return new Dimension(this.imageSource.getWidth(), this.imageSource.getHeight());
	}
	
	public int getNumberOfGraylevels() { return this.numberOfGrayLevels; }
	
	public int getxOffset() { return xOffset; }
	public void setxOffset(int xOffset) { this.xOffset = xOffset; }

	public int getyOffset() { return yOffset; }
	public void setyOffset(int yOffset) { this.yOffset = yOffset; }

	public boolean isSymmetricOffset() { return symmetricOffset; }
	public void setSymmetricOffset(boolean symmetricOffset) { this.symmetricOffset = symmetricOffset; }

	public int getImageWidth() { return this.imageSource.getWidth(); };
	public int getImageHeight() { return this.imageSource.getHeight(); };
	
	public void setNumberOfGrayLevels(int count) {
		if(this.numberOfGrayLevels == count)
			return;
		
		this.numberOfGrayLevels = count;
		this.glcm = new GLCM(this.numberOfGrayLevels);
	}
	
	public void setSizeOfWindow(int size) {
		if(this.windowRadius == size)
			return;
		
		this.windowRadius = size;
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
		
		for(j = (this.yOffset < 0 ? -this.yOffset : 0); j < (this.yOffset > 0 ? h - this.yOffset : h); ++j) {
			
			for(i = (this.xOffset < 0 ? -this.xOffset : 0); i < (this.xOffset > 0 ? w - this.xOffset : w); ++i) {
				
				this.glcm.reset();

				computeForPixel(this.glcm, i, j, w, h);
				
				//this.glcm.normalize();
				this.glcm.compute();
				this._tfi.setFromGLCM(i, j, this.glcm);
				
			}
			
		}
	}
	
	public synchronized void computeForPixel(GLCM glcm, int x, int y, int w, int h) {
		int k, l;

		for(l = y - this.windowRadius; l < y + this.windowRadius + 1; ++l) {

			if((l >= (this.yOffset < 0 ? -this.yOffset : 0)) && (l < (this.yOffset > 0 ? h - this.yOffset - 1 : h))) {
				
				for(k = x - this.windowRadius; k < x + this.windowRadius + 1; ++k) {

					if((k >= (this.xOffset < 0 ? -this.xOffset : 0)) && (k < (this.xOffset > 0 ? w - this.xOffset - 1 : w))) {

						glcm.inc(this.imagePosterized.get(k, l), this.imagePosterized.get(k + this.xOffset, l + this.yOffset));
						
						if(this.symmetricOffset)
							glcm.inc(this.imagePosterized.get(k + this.xOffset, l + this.yOffset), this.imagePosterized.get(k, l));

					}
					
				}
				
			}
			
		}
	}
	
	public void exportCSV(File file) {
		this._tfi.exportCSV(file);
	}

}
