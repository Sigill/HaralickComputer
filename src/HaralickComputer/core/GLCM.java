package HaralickComputer.core;

public class GLCM {
	private int _size;
	private int[] _data;
	private float[] _dataNormalized;
	
	private float _pixelMean, _pixelVariance, _marginalMean, _marginalDevSquared;
	
	private float _energy, _entropy, _correlation, 
		_inverseDifferenceMoment, _inertia, _clusterShade,
		_clusterProminence, _haralickCorrelation;
	
	public enum TextureFeatureName { Energy, Entropy, Correlation,
		InverseDifferenceMoment, Inertia, ClusterShade, ClusterProminence,
		HaralickCorrelation };

	
	public GLCM(int size) {
		this._size = size;
		
		this._data = new int[this._size * this._size];
		this._dataNormalized = new float[this._size * this._size];
		
		reset();
	}
	
	public int getSize() {
		return this._size;
	}
	
	public void reset() {
		for(int i = 0; i < this._size * this._size; i++) {
			this._data[i] = 0;
		}
	}
	
	private boolean validIndexes(int l1, int l2) {
		if((l1 < 0) || (l2 < 0) || (l1 >= this._size) || (l2 >= this._size)) {
			return false;
		}
		return true;
	}
	
	public int get(int l1, int l2) throws IndexOutOfBoundsException {
		if(!validIndexes(l1, l2)) {
			throw new IndexOutOfBoundsException("Indexes outside of matrix.");
		}
		return this._data[l1 * this._size + l2];
	}
	
	public void set(int l1, int l2, int v) throws IndexOutOfBoundsException {
		if(!validIndexes(l1, l2)) {
			throw new IndexOutOfBoundsException("Indexes outside of matrix.");
		}
		this._data[l1 * this._size + l2] = v;
	}
	
	public void inc(int l1, int l2) throws IndexOutOfBoundsException {
		if(!validIndexes(l1, l2)) {
			throw new IndexOutOfBoundsException("Indexes outside of matrix.");
		}
		this._data[l1 * this._size + l2] += 1;
	}
	
	private int count() {
		int v = 0;
		for(int i : this._data) {
			v += i;
		}
		return v;
	}
	
	public void normalize() {
		float c = this.count();
		for(int i = 0; i < this._size * this._size; i++) {
			this._dataNormalized[i] = this._data[i] / c;
		}
	}
	
	public void compute() {
		float f;
		int i, j;
		this.normalize();
		this.computeMeansAndVariance();
		double log2 = Math.log(2);
		
		_energy = 0; _entropy = 0; _correlation = 0; 
		_inverseDifferenceMoment = 0; _inertia = 0; _clusterShade = 0;
		_clusterProminence = 0; _haralickCorrelation = 0;
		
		float pixelVarianceSquared = this._pixelVariance * this._pixelVariance;

		for(i = 0 ; i < this._size; i++) {
			for(j = 0 ; j < this._size; j++) {
				f = this._dataNormalized[j * this._size + i];
				
				if(f == 0) continue;
				
			    this._energy += f * f;
			    this._entropy -= (f > 0.0001) ? f * Math.log(f) / log2 : 0;
			    this._correlation += ( (i - this._pixelMean) * (j - this._pixelMean) * f) / pixelVarianceSquared;
			    this._inverseDifferenceMoment += f / (1.0 + (i - j) * (i - j) );
			    this._inertia += (i - j) * (i - j) * f;
			    this._clusterShade += Math.pow((i - this._pixelMean) + (j - this._pixelMean), 3) * f;
			    this._clusterProminence += Math.pow((i - this._pixelMean) + (j - this._pixelMean), 4) * f;
			    this._haralickCorrelation += i * j * f;
			}
		}
		
		this._haralickCorrelation = (this._haralickCorrelation - this._marginalMean * this._marginalMean) / this._marginalDevSquared;
	}
	
	public float getMax() {
		float max = 0;
		
		for(int i = 0; i < this._size * this._size; i++) {
			if(this._data[i] > max) max = this._data[i];
		}
		
		return max;
	}
	
	private float computeDissimilarity() {
		float v = 0;
		int i, j;
		
		for(i = 0 ; i < this._size; i++) {
			for(j = 0 ; j < this._size; j++) {
				v += Math.abs(i - j) * this._dataNormalized[j * this._size + i];
			}
		}
		
		return v;
	}
	
	private float computeHomogeneity() {
		float v = 0;
		int i, j;
		
		for(i = 0 ; i < this._size; i++) {
			for(j = 0 ; j < this._size; j++) {
				v += this._dataNormalized[j * this._size + i] / (1 + (i - j) * (i - j));
			}
		}
		
		return v;
	}
	
	/**
	 * Compute the Angular Second Moment of a normalized GLCM.
	 * 1st aralick textural feature.
	 * 
	 * @return The Angular Second Moment of this GLCM.
	 */
	private float computeAngularSecondMoment() {
		float v = 0, d;
		int i, j;
		
		for(i = 0 ; i < this._size; i++) {
			for(j = 0 ; j < this._size; j++) {
				d = this._dataNormalized[j * this._size + i];
				v += d * d;
			}
		}
		
		return v;
	}
	
	/**
	 * Compute the Contrast of a normalized GLCM.
	 * 2nd Haralick textural feature.
	 * 
	 * @return The Angular Second Moment of this GLCM.
	 */
	private float computeContrast() {
		float v = 0;
		int i, j;
		
		for(i = 0 ; i < this._size; i++) {
			for(j = 0 ; j < this._size; j++) {
				v += (i - j) * (i - j) * this._dataNormalized[j * this._size + i];
			}
		}
		
		return v;
	}
	
	private void computeMeansAndVariance() {
		float[] marginalSums = new float[this._size];
		int i, j, k;
		float f;
		this._pixelMean = 0;
		this._pixelVariance = 0;
		
		for(i = 0 ; i < this._size; i++) { marginalSums[i] = 0; }
		
		for(i = 0 ; i < this._size; i++) {
			for(j = 0 ; j < this._size; j++) {
				f = this._dataNormalized[j * this._size + i];
				this._pixelMean += i * f;
				marginalSums[i] += f;
				
				this._pixelVariance += (i - j) * (i - j) * f;
			}
		}
		
		float M_k_minus_1, S_k_minus_1, x_k, M_k, S_k;
		
		for(i = 0 ; i < this._size; i++) {
		    k = i + 1;
		    M_k_minus_1 = this._marginalMean;
		    S_k_minus_1 = this._marginalDevSquared;
		    x_k = marginalSums[i];
		        
		    M_k = M_k_minus_1 + (x_k - M_k_minus_1) / k;
		    S_k = S_k_minus_1 + (x_k - M_k_minus_1) * (x_k - M_k);
		        
		   this._marginalMean = M_k;
		   this._marginalDevSquared = S_k;
		}
		
		this._marginalDevSquared = this._marginalDevSquared / this._size;		
	}
	
	public float get(int feature) {
		switch(feature) {
		case TextureFeatures.Energy:
			return this._energy;
		case TextureFeatures.Entropy:
			return this._entropy;
		case TextureFeatures.Correlation:
			return this._correlation;
		case TextureFeatures.InverseDifferenceMoment:
			return this._inverseDifferenceMoment;
		case TextureFeatures.Inertia:
			return this._inertia;
		case TextureFeatures.ClusterShade:
			return this._clusterShade;
		case TextureFeatures.ClusterProminence:
			return this._clusterProminence;
		case TextureFeatures.HaralickCorrelation:
			return this._haralickCorrelation;
		default:
			return 0;
		}
	}
}
