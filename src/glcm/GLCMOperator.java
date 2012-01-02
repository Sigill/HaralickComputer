package glcm;

public class GLCMOperator {
	private ShortArray data;
	private GLCM glcm;
	private int size;
	private int[] offset;
	private boolean symmetricOffset;
	private int[] firstItemCoordinates, secondItemCoordinates;
	
	public GLCMOperator(ShortArray data) {
		this.data = data;
		this.secondItemCoordinates = new int[data.numberOfDimensions];
	}
	
	public void setSize(int size) {
		this.size = size;
		this.glcm = new GLCM(this.size);
	}
	
	public void setOffset(boolean symmetric, int... offset) {
		this.symmetricOffset = symmetric;
		this.offset = offset.clone();
	}
	
	public void resetGLCM() {
		this.glcm.reset();
	}
	
	public GLCM getGLCM() {
		return this.glcm;
	}
	
	public void compute(ShortArray.Iterator it) {
		short v1, v2;
		while(it.isNotAtTheEnd()) {
			this.firstItemCoordinates = it.getCoordinates().clone();
			Utils.addOffset(this.firstItemCoordinates, this.offset, this.secondItemCoordinates);
			if(this.data.validateCoordinates(this.secondItemCoordinates)) {
				v1 = this.data.get(this.firstItemCoordinates);
				v2 = this.data.get(this.secondItemCoordinates);
				
				this.glcm.inc(v1, v2);
				if(this.symmetricOffset) this.glcm.inc(v2, v1);
			}
			
			it.move();
		}
	}
}
