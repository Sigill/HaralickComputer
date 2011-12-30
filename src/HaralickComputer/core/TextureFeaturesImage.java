package HaralickComputer.core;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

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
		set(x, y, TextureFeatures.AngularSecondMoment, glcm.getFeature(TextureFeatures.AngularSecondMoment));
		set(x, y, TextureFeatures.Entropy, glcm.getFeature(TextureFeatures.Entropy));
		set(x, y, TextureFeatures.AutoCorrelation, glcm.getFeature(TextureFeatures.AutoCorrelation));
		set(x, y, TextureFeatures.Correlation, glcm.getFeature(TextureFeatures.Correlation));
		set(x, y, TextureFeatures.SumOfSquaresVariance, glcm.getFeature(TextureFeatures.SumOfSquaresVariance));
		set(x, y, TextureFeatures.InverseDifferenceMoment, glcm.getFeature(TextureFeatures.InverseDifferenceMoment));
		set(x, y, TextureFeatures.SumAverage, glcm.getFeature(TextureFeatures.SumAverage));
		set(x, y, TextureFeatures.Contrast, glcm.getFeature(TextureFeatures.Contrast));
		set(x, y, TextureFeatures.ClusterShade, glcm.getFeature(TextureFeatures.ClusterShade));
		set(x, y, TextureFeatures.ClusterProminence, glcm.getFeature(TextureFeatures.ClusterProminence));
		set(x, y, TextureFeatures.HaralickCorrelationITK, glcm.getFeature(TextureFeatures.HaralickCorrelationITK));
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
	
	public void exportCSV(File file) {
		int i, j, f, startOffset, endOffset;
		float min[] = new float[TextureFeatures._nbFeatures];
		float max[] = new float[TextureFeatures._nbFeatures];
		float v, min_f, max_f;
		NumberFormat formatter = new DecimalFormat("###.#####");
		DecimalFormatSymbols frenchSymbols = new DecimalFormatSymbols(Locale.FRENCH);
		frenchSymbols.setDecimalSeparator('.');
		((DecimalFormat)formatter).setDecimalFormatSymbols(frenchSymbols);
		ArrayList<String> values = new ArrayList<String>(TextureFeatures._nbFeatures);
		
		for(f = 0; f < TextureFeatures._nbFeatures; ++f) {
			min_f = Float.MAX_VALUE;
			max_f = Float.MIN_VALUE;
			startOffset = f * this._width * this._height;
			endOffset = (f + 1) * (this._width * this._height);
			for(i = startOffset; i < endOffset; i++) {
				v = this._data[i];
				
				if(v < min_f) min_f = v;
				if(v > max_f) max_f = v;
			}
			min[f] = min_f;
			max[f] = max_f;
		}
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write("Id; Features\n");
			for(j = 0; j < this._height; ++j) {
				for(i = 0; i < this._width; ++i) {
					values.clear();
					for(f = 0; f < TextureFeatures._nbFeatures; ++f) {
						values.add(formatter.format((this._data[getOffset(i, j, f)] - min[f]) / (max[f] - min[f])));
					}
					writer.write((j * this._width + i) + "; (" + Utils.combine(values, ", ") + ")\n");
					//writer.write((j * this._width + i) + "; " + Utils.combine(values, "; ") + "\n");
				}
			}
			
			writer.close();
		} catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		}
	}
}
