package glcm;

import glcm.Bitmap3D;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class LocalHaralickOperator {
	private ShortArray data;
	private GLCMOperator glcmOp;
	private int[] radius;
	private ShortArray.NeighbourhoodIterator neighbourhoodIterator;
	private HaralickImage haralickImage;
	private HaralickComputer haralickComputer;
	
	public LocalHaralickOperator(ShortArray data) {
		this.data = data;
		this.glcmOp = new GLCMOperator(this.data);
		this.haralickImage = new HaralickImage(this.data.dimensions);
	}
	
	public void setRadius(int... radius) {
		if(radius.length == 1) {
			this.radius = new int[this.data.numberOfDimensions];
			Arrays.fill(this.radius, radius[0]);
		} else {
			this.radius = radius.clone();
		}
		
		this.neighbourhoodIterator = this.data.new NeighbourhoodIterator(radius);
	}
	
	public void setOffset(boolean symmetric, int... offset) {
		this.glcmOp.setOffset(symmetric, offset);
	}
	
	public void setNumberOfGraylevels(int n) {
		this.glcmOp.setSize(n);
		this.haralickComputer = new HaralickComputer(n);
	}
	
	public void compute(ShortArray.Iterator it) {
		GLCM glcm = null;
		
		while(it.isNotAtTheEnd()) {
			this.neighbourhoodIterator.setCenterPixel(it.getCoordinates());
			
			this.glcmOp.resetGLCM();
			this.glcmOp.compute(this.neighbourhoodIterator);
			glcm = this.glcmOp.getGLCM();
			glcm.normalize();
			this.haralickComputer.compute(glcm);
			for(int f = 0; f < HaralickComputer.numberOfFeatures; ++f) {
				this.haralickImage.set(this.haralickComputer.getFeature(f), f, it.getCoordinates());
			}
			
			it.move();
		}
	}
	
	public FloatArray getFeature(int feature) {
		return this.haralickImage.getFeature(feature);
	}
	
	public static void main(String[] args) {
		try {
			Bitmap3D img = Bitmap3D.loadImage2D(new File("C:\\Users\\Cyrille\\Pictures\\WatershedOriginalImage-R.bmp"));
			Bitmap3D img16 = new Bitmap3D(img);
			img16.posterize(16);
			
			Bitmap3D.ImageIterator it = img16.new ImageIterator();
			it.start();
			
			LocalHaralickOperator haraOp = new LocalHaralickOperator(img16);
			haraOp.setNumberOfGraylevels(16);
			haraOp.setRadius(2, 2, 1);
			haraOp.setOffset(true, 1, 0, 0);
			haraOp.compute(it);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
