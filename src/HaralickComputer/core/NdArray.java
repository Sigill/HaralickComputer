package HaralickComputer.core;

public class NdArray {
	private int _size;
	private int[] _dimensions;
	private int[] _data;
	private int[] _offsets;
	
	public NdArray(int... dimensions) /* throws IndexOutOfBoundsException */ {
		this._size = dimensions.length;
		/*
		if(this._size == 0) {
			throw new IndexOutOfBoundsException("The dimension of a NdArray must be greater than 0.");
		}
		
		if(!validateSize(dimensions)) {
			throw new IndexOutOfBoundsException("The dimensions of the NdArray not legals.");
		}
		*/
		
		_dimensions = dimensions;
		
		computeOffsets();
		
		_data = new int[_offsets[this._size]];
	}
	/*
	private boolean validDimension(int length) {
		return length == this._size;
	}
	
	private boolean validateArray(int min, int... values) {
		for(int d : values) {
			if(d < min) {
				return false;
			}
		}
		return true;
	}
	
	private boolean validateSize(int... dimensions) {
		return validateArray(1, dimensions);
	}
	
	private boolean validateCoordinates(int... coordinates) {
		return validateArray(0, coordinates);
	}
	*/
	
	private void computeOffsets() {
		_offsets = new int[this._size + 1];
		_offsets[0] = 1;
		
		for(int i = 0; i < this._size; i++) {
			_offsets[i+1] = _offsets[i] * _dimensions[i];
		}
	}
	
	private int getOffset(int... position) {
		int offset = 0;
		for(int i = 0; i < position.length; i++) {
			offset += position[i] * _offsets[i];
		}
		
		return offset;
	}
	
	public int get(int... coordinates) /* throws IndexOutOfBoundsException */ {
		/*
		if(!( validDimension(coordinates.length) && validateCoordinates(coordinates) )) {
			throw new IndexOutOfBoundsException("Invalid coordinates.");
		}
		*/
		
		return _data[getOffset(coordinates)];
	}
	
	public void set(int value, int... coordinates) /* throws IndexOutOfBoundsException */ {
		/*
		if(!( validDimension(coordinates.length) && validateCoordinates(coordinates) )) {
			throw new IndexOutOfBoundsException("Invalid coordinates.");
		}
		*/
		
		_data[getOffset(coordinates)] = value;
	}
	
	public NdArray extractRegion(int... coordinates) /* throws IndexOutOfBoundsException */ {
		/*
		if((coordinates.length % 2 != 0)) {
			throw new IndexOutOfBoundsException("Invalid dimensions.");
		}
		
		if(!validDimension(coordinates.length << 1)) {
			throw new IndexOutOfBoundsException("Invalid dimensions.");
		}
		*/
		
		int[] begin = new int[this._size];
		int[] end = new int[this._size];
		
		for(int i = 0; i < this._size; i++) {
			begin[i] = coordinates[i];
			end[i] = coordinates[this._size + i];
		}
		
		/*
		if(validateCoordinates(begin)) {
			throw new IndexOutOfBoundsException("Invalid begin coordinates.");
		}
		if(validateCoordinates(end)) {
			throw new IndexOutOfBoundsException("Invalid begin coordinates.");
		}
		*/
		
		NdArray roi = new NdArray();
		
		return roi;
	}
}
