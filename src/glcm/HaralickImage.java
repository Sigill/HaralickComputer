package glcm;

public class HaralickImage {
	int[] dimensions;
	private FloatArray[] image;
	
	public HaralickImage(int... dimensions) throws IllegalArgumentException {
		this.dimensions = dimensions;
		
		image = new FloatArray[HaralickComputer.numberOfFeatures];
		for(int f = 0; f < HaralickComputer.numberOfFeatures; ++f) {
			this.image[f] = new FloatArray(dimensions);
			this.image[f].allocate();
		}
	}
	
	public void set(float value, int feature, int... coordinates) {
		image[feature].set(value, coordinates);
	}
	
	public FloatArray getFeature(int feature) {
		return this.image[feature];
	}
}
