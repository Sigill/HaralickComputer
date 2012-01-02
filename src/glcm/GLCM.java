package glcm;

import java.util.Arrays;

public class GLCM {
	private int _size;
	public int[] glcm;
	public float[] glcm_norm;
	
	public GLCM(int size) {
		this._size = size;
		
		this.glcm = new int[this._size * this._size];
		this.glcm_norm = new float[this._size * this._size];
		
		reset();
	}
	
	public int getSize() {
		return this._size;
	}
	
	public void reset() {
		Arrays.fill(this.glcm, 0);
	}
	
	private boolean validIndexes(int l1, int l2) {
		if((l1 < 0) || (l2 < 0) || (l1 >= this._size) || (l2 >= this._size)) {
			return false;
		}
		return true;
	}
	
	public int get(int l1, int l2) {
		return this.glcm[l1 * this._size + l2];
	}
	
	public void set(int l1, int l2, int v) {
		this.glcm[l1 * this._size + l2] = v;
	}
	
	public void inc(int l1, int l2) {
		this.glcm[l1 * this._size + l2] += 1;
	}
	
	private int count() {
		int v = 0;
		for(int i : this.glcm) {
			v += i;
		}
		return v;
	}
	
	public void normalize() {
		float c = this.count();
		for(int i = 0; i < this._size * this._size; i++) {
			this.glcm_norm[i] = this.glcm[i] / c;
		}
	}
	
	public float getMax() {
		float max = 0;
		
		for(int i = 0; i < this._size * this._size; i++) {
			if(this.glcm[i] > max) max = this.glcm[i];
		}
		
		return max;
	}
}
