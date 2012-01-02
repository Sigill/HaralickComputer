package glcm;

public class HaralickImage {
	int[] dimensions;
	private FloatArray[] image;
	
	public HaralickImage(int... dimensions) throws IllegalArgumentException {
		this.dimensions = dimensions;
		
		image = new FloatArray[HaralickComputer.numberOfFeatures];
		for(int i = 0; i < HaralickComputer.numberOfFeatures; ++i) {
			this.image[i] = new FloatArray(dimensions);
			this.image[i].allocate();
		}
	}
	
	public void set(float value, int feature, int... coordinates) {
		image[feature].set(value, coordinates);
	}
	
	public FloatArray getFeature(int feature) {
		return this.image[feature];
	}
}
