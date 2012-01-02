package glcm;

import java.util.Arrays;

public class FloatArray {
	protected int numberOfDimensions;
	protected int[] dimensions;
	protected int[] offsets;
	protected float[] data;
	
	public int getNumberOfDimensions() {
		return numberOfDimensions;
	}
	
	public int[] getDimensions() {
		return dimensions;
	}
	
	public FloatArray(int... dimensions) throws IllegalArgumentException {
		this.data = null;
		this.numberOfDimensions = dimensions.length;
		
		if(this.numberOfDimensions == 0) {
			throw new IllegalArgumentException("The dimension of a FloatArray must be greater than 0.");
		}
		
		if(!validateSize(dimensions)) {
			throw new IllegalArgumentException("The dimensions of the FloatArray are invalid.");
		}
		
		this.dimensions = dimensions;
		
		computeOffsets();
	}
	
	public FloatArray(FloatArray other) {
		this(other.dimensions);
		this.data = other.data.clone();
	}
	
	public void allocate() {
		this.data = new float[offsets[this.numberOfDimensions]];
	}
	
	protected boolean validDimension(int length) {
		return length == this.numberOfDimensions;
	}
	
	protected boolean validateArray(int min, int... values) {
		for(int d : values) {
			if(d < min) {
				return false;
			}
		}
		return true;
	}
	
	protected boolean validateSize(int... dimensions) {
		return validateArray(1, dimensions);
	}
	
	public boolean validateCoordinates(int... coordinates) {
		int c;
		for(int i = 0; i < this.numberOfDimensions; ++i) {
			c = coordinates[i];
			if(c < 0) return false;
			if(c >= this.dimensions[i]) return false;
		}
		return true;
	}
	
	
	protected void computeOffsets() {
		offsets = new int[this.numberOfDimensions + 1];
		offsets[0] = 1;
		
		for(int i = 0; i < this.numberOfDimensions; i++) {
			offsets[i+1] = offsets[i] * dimensions[i];
		}
	}
	
	protected int getOffset(int... position) {
		int offset = 0;
		for(int i = 0; i < position.length; i++) {
			offset += position[i] * offsets[i];
		}
		
		return offset;
	}
	
	public float get(int... coordinates) /* throws IndexOutOfBoundsException */ {
		/*
		if(!( validDimension(coordinates.length) && validateCoordinates(coordinates) )) {
			throw new IndexOutOfBoundsException("Invalid coordinates.");
		}
		*/
		
		return this.data[getOffset(coordinates)];
	}
	
	public void set(float value, int... coordinates) /* throws IndexOutOfBoundsException */ {
		/*
		if(!( validDimension(coordinates.length) && validateCoordinates(coordinates) )) {
			throw new IndexOutOfBoundsException("Invalid coordinates.");
		}
		*/
		
		this.data[getOffset(coordinates)] = value;
	}
	
	public FloatArray extractRegion(int... coordinates) /* throws IndexOutOfBoundsException */ {
		/*
		if((coordinates.length % 2 != 0)) {
			throw new IndexOutOfBoundsException("Invalid dimensions.");
		}
		
		if(!validDimension(coordinates.length << 1)) {
			throw new IndexOutOfBoundsException("Invalid dimensions.");
		}
		*/
		
		int[] begin = new int[this.numberOfDimensions];
		int[] end = new int[this.numberOfDimensions];
		
		for(int i = 0; i < this.numberOfDimensions; i++) {
			begin[i] = coordinates[i];
			end[i] = coordinates[this.numberOfDimensions + i];
		}
		
		/*
		if(validateCoordinates(begin)) {
			throw new IndexOutOfBoundsException("Invalid begin coordinates.");
		}
		if(validateCoordinates(end)) {
			throw new IndexOutOfBoundsException("Invalid begin coordinates.");
		}
		*/
		
		FloatArray roi = new FloatArray();
		
		return roi;
	}
	/*
	public FloatArray clone() {
		return new FloatArray(this);
	}
	*/
	
	public interface Iterator {
		public boolean isNotAtTheEnd();
		public float get();
		public void set(float v);
		public void move();
		public int[] getCoordinates();
	}
	
	public class ImageIterator implements Iterator {
		private int[] coordinates = new int[FloatArray.this.numberOfDimensions];
		private boolean trueIfNotAtTheEnd;
		
		public ImageIterator() {
			start();
		}
		
		public void start() {
			Arrays.fill(this.coordinates, 0);
			this.trueIfNotAtTheEnd = true;
		}
		
		public boolean isNotAtTheEnd() {
			return this.trueIfNotAtTheEnd;
		}
		
		public float get() {
			return FloatArray.this.get(this.coordinates);
		}
		
		public void set(float v) {
			FloatArray.this.set(v, this.coordinates);
		}
		
		public int[] getCoordinates() { return this.coordinates; };
		
		public void move() {
			for(int i = 0; i < FloatArray.this.numberOfDimensions; ++i) {
				if(this.coordinates[i] < FloatArray.this.dimensions[i] - 1) { 
					this.coordinates[i] += 1;
					break;
				} else {
					if(i == FloatArray.this.numberOfDimensions - 1) {
						this.trueIfNotAtTheEnd = false;
					} else {
						this.coordinates[i] = 0;
					}
				}
			}
		}
	}
	
	public class NeighbourhoodIterator implements Iterator {
		private int[] radius;
		private int[] centerPixel;
		private int[] minCoordinates = new int[FloatArray.this.numberOfDimensions];
		private int[] maxCoordinates = new int[FloatArray.this.numberOfDimensions];
		private int[] coordinates;
		private boolean trueIfNotAtTheEnd;
		
		public NeighbourhoodIterator(int... radius) {
			if(radius.length == 1) {
				this.radius = new int[FloatArray.this.numberOfDimensions];
				Arrays.fill(this.radius, radius[0]);
			} else {
				this.radius = radius.clone();
			}
		}
		
		public void setCenterPixel(int... coordinates) {
			int l;
			this.centerPixel = coordinates.clone();
			
			for(int i = 0; i < FloatArray.this.numberOfDimensions; ++i) {
				l = this.centerPixel[i] - this.radius[i];
				if(l < 0) l = 0; 
					
				this.minCoordinates[i] = l;
				
				l = this.centerPixel[i] +  this.radius[i];
				if(l >= FloatArray.this.dimensions[i]) l = FloatArray.this.dimensions[i] - 1; 
					
				this.maxCoordinates[i] = l;
			}
			
			this.coordinates = this.minCoordinates.clone();
			this.trueIfNotAtTheEnd = true;
		}
		
		public boolean isNotAtTheEnd() {
			return this.trueIfNotAtTheEnd;
		}
		
		public float get() {
			return FloatArray.this.get(this.coordinates);
		}
		
		public void set(float v) {
			FloatArray.this.set(v, this.coordinates);
		}
		
		public int[] getCoordinates() { return this.coordinates; };
		
		public void move() {
			for(int i = 0; i < FloatArray.this.numberOfDimensions; ++i) {
				if(this.coordinates[i] < this.maxCoordinates[i]) { 
					this.coordinates[i] += 1;
					break;
				} else {
					if(i == FloatArray.this.numberOfDimensions - 1) {
						this.trueIfNotAtTheEnd = false;
					} else {
						this.coordinates[i] = this.minCoordinates[i];
					}
				}
			}
		}
	}
	
	public static void main(String[] args) {
		int S = 3;
		long start, end;
		float v;
		FloatArray a = new FloatArray(S, S, S);
		a.allocate();
		
		for(int k = 0; k < S; ++k) {
			for(int j = 0; j < S; ++j) {
				for(int i = 0; i < S; ++i) {
					a.set((float)a.getOffset(i, j, k), i, j, k);
				}
			}
		}
		
		NeighbourhoodIterator it = a.new NeighbourhoodIterator(1);
		for(int k = 0; k < S; ++k) {
			for(int j = 0; j < S; ++j) {
				for(int i = 0; i < S; ++i) {
					it.setCenterPixel(i, j, k);
					while(it.isNotAtTheEnd()) {
						v = it.get();
						//System.out.println(v);
						it.move();
					}
				}
			}
		}
		
		start = System.currentTimeMillis();
		ImageIterator it2 = a.new ImageIterator();
		while(it2.isNotAtTheEnd()) {
			v = it2.get();
			//System.out.println(v);
			it2.move();
		}
		end = System.currentTimeMillis();

		System.out.println("Execution time was " + (end - start) + " ms.");
	}
}
