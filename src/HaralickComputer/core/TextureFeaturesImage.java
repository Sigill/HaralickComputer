package HaralickComputer.core;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class TextureFeaturesImage {
	private int _width, _height;
	private float[] _data;
	
	public TextureFeaturesImage(int width, int height) {
		this._width = width;
		this._height = height;
		
		this._data = new float[TextureFeatures._nbFeatures * this._width * this._height];
	}
	
	private int getOffset(int x, int y, int f) {
		return f * this._width * this._height + y * this._width + x;
	}
	
	public void set(int x, int y, int f, float v) {
		this._data[getOffset(x, y, f)] = v;
	}
	
	public void setFromGLCM(int x, int y, GLCM glcm) {
		set(x, y, TextureFeatures.Energy, glcm.get(TextureFeatures.Energy));
		set(x, y, TextureFeatures.Entropy, glcm.get(TextureFeatures.Entropy));
		set(x, y, TextureFeatures.Correlation, glcm.get(TextureFeatures.Correlation));
		set(x, y, TextureFeatures.InverseDifferenceMoment, glcm.get(TextureFeatures.InverseDifferenceMoment));
		set(x, y, TextureFeatures.Inertia, glcm.get(TextureFeatures.Inertia));
		set(x, y, TextureFeatures.ClusterShade, glcm.get(TextureFeatures.ClusterShade));
		set(x, y, TextureFeatures.ClusterProminence, glcm.get(TextureFeatures.ClusterProminence));
		set(x, y, TextureFeatures.HaralickCorrelation, glcm.get(TextureFeatures.HaralickCorrelation));
	}
	
	public BufferedImage toBufferedImage(int feature) {
		BufferedImage img = new BufferedImage(this._width, this._height, BufferedImage.TYPE_INT_RGB);
		
		int i, j, startOffset, endOffset;
		float min = Float.MAX_VALUE, max = Float.MIN_VALUE, v;
		
		startOffset = feature * this._width * this._height;
		endOffset = (feature + 1) * (this._width * this._height);
		
		for(i = startOffset; i < endOffset; i++) {
			v = this._data[i];
			
			if(v < min) min = v;
			if(v > max) max = v;
		}
		
		Color c;
		
		for(i = 0; i < this._width; i++) {
			for(j = 0; j < this._height; j++) {
				v = (this._data[getOffset(i, j, feature)] - min) / (max - min);
				c = new Color(v, v, v);
				img.setRGB(i, j, c.getRGB());
			}
		}
		
		return img;
	}
}