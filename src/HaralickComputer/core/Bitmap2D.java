package HaralickComputer.core;

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;

public class Bitmap2D {
	protected int _width, _height;
	protected int[] _data;
	
	public Bitmap2D(int width, int height) {
		this._width = width;
		this._height = height;
		
		this._data = new int[this._width * this._height];
	}
	
	public Bitmap2D(BufferedImage img) {
		this(img.getWidth(), img.getHeight());
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
		ColorConvertOp op = new ColorConvertOp(cs, null);  
		BufferedImage gimg = op.filter(img, null);
		
		int i, j;
		for(j = 0; j < this._height; j++) {
			for(i = 0; i < this._width; i++) {
				this.set(i, j, gimg.getRGB(i, j) & 0xff);
			}
		}
	}
	
	public Bitmap2D(Bitmap2D other) {
		this._width = other._width;
		this._height = other._height;
		
		this._data = other._data.clone();
	}
	
	public int getWidth() { return _width; }
	public int getHeight() { return _height; }

	private int offset(int x, int y) {
		return y * this._width + x;
	}
	
	public void zeroInit() {
		for(int i = 0; i < this._width * this._height; i++) {
			this._data[i] = 0;
		}
	}
	
	private boolean validCoordinates(int x, int y) {
		return (x >= 0) && (y >= 0) && (x < this._width) && (y < this._height);
	}
	
	public int get(int x, int y) throws IndexOutOfBoundsException {
		if(!validCoordinates(x, y)) {
			throw new IndexOutOfBoundsException("Coordinates(" + x + "; " + y + ") outside of image.");
		}
		return this._data[offset(x, y)];
	}
	
	public void set(int x, int y, int v) throws IndexOutOfBoundsException {
		if(!validCoordinates(x, y)) {
			throw new IndexOutOfBoundsException("Coordinates outside of image.");
		}
		this._data[offset(x, y)] = v;
	}
	
	public BufferedImage toBufferedImage() {
		BufferedImage img = new BufferedImage(this._width, this._height, BufferedImage.TYPE_INT_RGB);
		
		int i, j;
		for(j = 0; j < this._height; j++) {
			for(i = 0; i < this._width; i++) {
				int v = this._data[j * this._width + i];
				Color c = new Color(v, v, v);
				img.setRGB(i, j, c.getRGB());
			}
		}
		
		return img;
	}
	
	public void posterize(int level) {
		float numberofcolors = level;
		//float areaSize = (float) (255.0 / (numberofcolors - 1));
		float numberofareas = 256 / numberofcolors;

		int i, j, pix;
		for(j = 0; j < this._height; ++j) {
			for(i = 0; i < this._width; ++i) {
				//pix = (int) Math.rint(Math.floor(this.get(i, j) / numberofareas) * areaSize);
				pix = (int) Math.rint(Math.floor(this.get(i, j) / numberofareas));
				this.set(i, j, pix);
			}

		}
	}
}
