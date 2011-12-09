package HaralickComputer.controllers;

import java.awt.image.BufferedImage;

import HaralickComputer.core.Bitmap2D;
import HaralickComputer.core.GLCM;
import HaralickComputer.core.TextureFeaturesImage;

public class TextureFeaturesComputer {
	private Bitmap2D _source, _image;
	private GLCM _cooc;
	private int _numberOfcolors;
	private int _maskWidth;
	private TextureFeaturesImage _tfi;
	
	public TextureFeaturesComputer() {	
		this._source = null;
		this._image = null;
		this._numberOfcolors = 16;
		this._cooc = new GLCM(this._numberOfcolors);
		this._maskWidth = 2;
	}
	
	public void setImageSource(BufferedImage img) {
		this._source = new Bitmap2D(img);
		this._image = new Bitmap2D(this._source);
		this._image.posterize(this._numberOfcolors);
		
		this._tfi = new TextureFeaturesImage(this._source.getWidth(), this._source.getHeight());
	};
	
	public synchronized BufferedImage getSourceImage() { return this._source.toBufferedImage(); }
	public synchronized BufferedImage getHaralickImage(int feature) { return this._tfi.toBufferedImage(feature); }
	
	public synchronized void compute() {
		int i, j;
		int w, h;
		w = this._image.getWidth();
		h = this._image.getHeight();
		
		for(i = 0; i < w; i++)
		{
			for(j = 0; j < h; j++)
			{
				this._cooc.reset();

				computeForPixel(i, j, w, h);
				
				this._cooc.normalize();
				this._cooc.compute();
				this._tfi.setFromGLCM(i, j, this._cooc);
			}
		}
	}
	
	public synchronized void computeForPixel(int x, int y, int width, int height) {
		int k, l;
		for(k = x - this._maskWidth; k < x + this._maskWidth; k++) {
			if((k > 0) && (k < width-1)) {
				for(l = y - this._maskWidth; l < y + this._maskWidth; l++)
				{
					if((l > 0) && (l < height-1)) {
						this._cooc.inc(this._image.get(k, l), this._image.get(k, l+1));
						//this._cooc.inc(this._image.get(k, l), this._image.get(k+1, l));
					}
				}
			}
		}
	}

}
