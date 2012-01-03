package glcm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Controller {
	public final static int DEFAULT_X_OFFSET = 1;
	public final static int DEFAULT_Y_OFFSET = 0;
	public final static int DEFAULT_NUMBER_OF_GRAYLEVELS = 16;
	public final static int DEFAULT_WINDOW_RADIUS = 2;
	public final static boolean DEFAULT_SYMMETRIC_OFFSET = false;
	
	public Bitmap3D imageSource = null, imagePosterized = null;
	public LocalHaralickOperator haraOp;
	public Bitmap3D[] haralickImages;
	public Bitmap3D.NeighbourhoodIterator nit;
	public GLCMOperator glcmOp;
	
	public int 
		numberOfGraylevels = DEFAULT_NUMBER_OF_GRAYLEVELS,
		windowRadius = DEFAULT_WINDOW_RADIUS, 
		xOffset = DEFAULT_X_OFFSET, 
		yOffset = DEFAULT_Y_OFFSET;
	public boolean symmetricOffset = DEFAULT_SYMMETRIC_OFFSET;
	
	public void process() {
		this.imagePosterized = new Bitmap3D(this.imageSource);
		this.imagePosterized.posterize(this.numberOfGraylevels);
		
		this.glcmOp = new GLCMOperator(this.imagePosterized);
		this.glcmOp.setSize(this.numberOfGraylevels);
		this.glcmOp.setOffset(
				this.symmetricOffset, 
				this.xOffset, 
				this.yOffset, 
				0);
		
		Bitmap3D.ImageIterator it = this.imagePosterized.new ImageIterator();
		it.start();
		
		haraOp = new LocalHaralickOperator(this.imagePosterized);
		haraOp.setNumberOfGraylevels(this.numberOfGraylevels);
		haraOp.setRadius(this.windowRadius, this.windowRadius, 1);
		haraOp.setOffset(this.symmetricOffset, this.xOffset, this.yOffset, 0);
		haraOp.compute(it);
		
		this.haralickImages = new Bitmap3D[HaralickComputer.numberOfFeatures];
		for(int i = 0; i < HaralickComputer.numberOfFeatures; ++i) {
			this.haralickImages[i] = new Bitmap3D(haraOp.getFeature(i));
		}
		
		nit = this.imageSource.new NeighbourhoodIterator(this.windowRadius);
	}
	
	public void exportCSV(File file) {
		int imageSize = this.haralickImages[0].offsets[this.haralickImages[0].numberOfDimensions];
		int i;
		NumberFormat formatter = new DecimalFormat("###.#####");
		DecimalFormatSymbols frenchSymbols = new DecimalFormatSymbols(Locale.FRENCH);
		frenchSymbols.setDecimalSeparator('.');
		((DecimalFormat)formatter).setDecimalFormatSymbols(frenchSymbols);
		ArrayList<String> values = new ArrayList<String>(HaralickComputer.numberOfFeatures);
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write("Id; Features\n");
			
			for(int pix = 0; pix < imageSize; ++pix) {
				values.clear();
				for(i = 0; i < HaralickComputer.numberOfFeatures; ++i) {
					values.add(formatter.format(this.haralickImages[i].data[pix] / 255.0));
				}
				writer.write(pix + "; (" + Utils.combine(values, ", ") + ")\n");
			}
			
			writer.close();
		} catch (IOException x) {
		    System.err.format("IOException: %s%n", x);
		}
	}
}
