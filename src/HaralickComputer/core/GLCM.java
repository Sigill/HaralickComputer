package HaralickComputer.core;

import java.util.Arrays;

public class GLCM {
	private int _size;
	private int[] glcm;
	private float[] glcm_norm;
	
	private float _pixelMean, _pixelVariance, _marginalMean, _marginalDevSquared;
	
	private float[] 
		_mu_i, _mu_j, _s_i, _s_j,
		_p_x, _p_y;
	
	private float _mu_x, _mu_y, _s_x, _s_y;
	
	private float _angularSecondMoment, _contrast, _autoCorrelation, _correlation, _haralickCorrelation, _entropy,
		_inverseDifferenceMoment, _clusterShade,
		_clusterProminence, _haralickCorrelationITK;
	
	public enum TextureFeatureName { 
		AngularSecondMoment, 
		Contrast, 
		AutoCorrelation,
		Correlation, 
		Correlation2, 
		Entropy,
		InverseDifferenceMoment, 
		ClusterShade, 
		ClusterProminence,
		HaralickCorrelation
	};
	
	public GLCM(int size) {
		this._size = size;
		
		this.glcm = new int[this._size * this._size];
		this.glcm_norm = new float[this._size * this._size];
		
		this._mu_i = new float[this._size];
		this._mu_j = new float[this._size];
		this._s_i = new float[this._size];
		this._s_j = new float[this._size];
		
		this._p_x = new float[this._size];
		this._p_y = new float[this._size];
		
		reset();
	}
	
	public int getSize() {
		return this._size;
	}
	
	public void reset() {
		Arrays.fill(this.glcm, 0);
		//for(int i = 0; i < this._size * this._size; i++) {
		//	this._data[i] = 0;
		//}
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
		return this.glcm[l1 * this._size + l2];
	}
	
	public void set(int l1, int l2, int v) throws IndexOutOfBoundsException {
		if(!validIndexes(l1, l2)) {
			throw new IndexOutOfBoundsException("Indexes outside of matrix.");
		}
		this.glcm[l1 * this._size + l2] = v;
	}
	
	public void inc(int l1, int l2) throws IndexOutOfBoundsException {
		if(!validIndexes(l1, l2)) {
			throw new IndexOutOfBoundsException("Indexes outside of matrix.");
		}
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
	
	public void compute() {
		float f;
		int i, j;
		this.normalize();
		this.computeMeansAndVariance();
		double log2 = Math.log(2);
		
		_angularSecondMoment = 0; _entropy = 0; _autoCorrelation = 0; _correlation = 0; 
		_haralickCorrelation = 0; _inverseDifferenceMoment = 0; _contrast = 0; _clusterShade = 0;
		_clusterProminence = 0; _haralickCorrelationITK = 0;

		for(i = 0 ; i < this._size; i++) {
			for(j = 0 ; j < this._size; j++) {
				f = this.glcm_norm[j * this._size + i];
				
				if(f == 0) continue;
				
			    this._angularSecondMoment += f * f;
			    this._contrast += (i - j) * (i - j) * f;
			    this._autoCorrelation += i * j * f;
			    
			    this._entropy -= (f > 0.0001) ? f * Math.log(f) / log2 : 0;
			    
			    this._inverseDifferenceMoment += f / (1.0 + (i - j) * (i - j) );
			    
			    this._clusterShade += Math.pow((i - this._pixelMean) + (j - this._pixelMean), 3) * f;
			    this._clusterProminence += Math.pow((i - this._pixelMean) + (j - this._pixelMean), 4) * f;
			}
		}
		
		if((this._s_x > 0.0) && (this._s_y > 0.0))
			this._correlation = (float) ( (this._autoCorrelation - this._mu_x * this._mu_y) / (this._s_x * this._s_y) );
		else
			this._correlation = 0;
		
		if(this._marginalDevSquared > 0.0)
			this._haralickCorrelationITK = (this._autoCorrelation - this._marginalMean * this._marginalMean) / this._marginalDevSquared;
		else
			this._haralickCorrelationITK = 0;
	}
	
	public float getMax() {
		float max = 0;
		
		for(int i = 0; i < this._size * this._size; i++) {
			if(this.glcm[i] > max) max = this.glcm[i];
		}
		
		return max;
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
				f = this.glcm_norm[j * this._size + i];
				this._pixelMean += i * f;
				marginalSums[i] += f;
				
				this._pixelVariance += (i - j) * (i - j) * f;
			}
		}
		
		for(i = 0 ; i < this._size; i++) {
			for(j = 0 ; j < this._size; j++) {
				f = this.glcm_norm[j * this._size + i];
				this._pixelVariance += (i - this._pixelMean) * (i - this._pixelMean) * f;
			}
		}
		
		float M_k_minus_1, S_k_minus_1, x_k, M_k, S_k;
		
		this._marginalMean = 0;
		this._marginalDevSquared = 0;
		
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
		
		float mu_i;
		for(i = 0; i < this._size; ++i) {
			mu_i = 0;
			for(j = 0; j < this._size; ++j) {
				mu_i += i * this.glcm_norm[j * this._size + i];
			}
			this._mu_i[i] = mu_i;
		}
		
		float mu_j;
		for(j = 0; j < this._size; ++j) {
			mu_j = 0;
			for(i = 0; i < this._size; ++i) {
				mu_j += j * this.glcm_norm[j * this._size + i];
			}
			this._mu_j[j] = mu_j;
		}
		
		float s_i;
		for(i = 0; i < this._size; ++i) {
			s_i = 0;
			mu_i = this._mu_i[i];
			for(j = 0; j < this._size; ++j) {
				s_i += (i - mu_i) * (i - mu_i) * this.glcm_norm[j * this._size + i];
			}
			this._s_i[i] = (float) Math.sqrt(s_i);
		}
		
		float s_j;
		for(j = 0; j < this._size; ++j) {
			s_j = 0;
			mu_j = this._mu_j[j];
			for(i = 0; i < this._size; ++i) {
				s_j += (j - mu_j) * (j - mu_j) * this.glcm_norm[j * this._size + i];
			}
			this._s_j[j] = (float) Math.sqrt(s_j);
		}
		
		
		// HARALICK BASED MEANS & STD
		float p_x;
		for(i = 0; i < this._size; ++i) {
			p_x = 0;
			for(j = 0; j < this._size; ++j) {
				p_x += this.glcm_norm[j * this._size + i];
			}
			this._p_x[i] = p_x;
		}
		
		float p_y;
		for(j = 0; j < this._size; ++j) {
			p_y = 0;
			for(i = 0; i < this._size; ++i) {
				p_y += this.glcm_norm[j * this._size + i];
			}
			this._p_y[j] = p_y;
		}
		
		this._mu_x = 0;
		this._mu_y = 0;
		for(i = 0; i < this._size; ++i) {
			for(j = 0; j < this._size; ++j) {
				f = this.glcm_norm[j * this._size + i];
				this._mu_x += i * f;
				this._mu_y += j * f;
			}
		}
		
		this._s_x = 0;
		this._s_y = 0;
		for(i = 0; i < this._size; ++i) {
			for(j = 0; j < this._size; ++j) {
				f = this.glcm_norm[j * this._size + i];
				this._s_x += Math.sqrt((i - this._mu_x) * (i - this._mu_x) * f);
				this._s_y += Math.sqrt((j - this._mu_y) * (j - this._mu_y) * f);
			}
		}
		// END OF HARALICK BASED MEANS & STD
	}
	
	public float getFeature(int feature) {
		switch(feature) {
		case TextureFeatures.AngularSecondMoment:
			return this._angularSecondMoment;
		case TextureFeatures.Entropy:
			return this._entropy;
		case TextureFeatures.AutoCorrelation:
			return this._autoCorrelation;
		case TextureFeatures.Correlation:
			return this._correlation;
		case TextureFeatures.InverseDifferenceMoment:
			return this._inverseDifferenceMoment;
		case TextureFeatures.Contrast:
			return this._contrast;
		case TextureFeatures.ClusterShade:
			return this._clusterShade;
		case TextureFeatures.ClusterProminence:
			return this._clusterProminence;
		case TextureFeatures.HaralickCorrelationITK:
			return this._haralickCorrelationITK;
		default:
			return 0;
		}
	}
}
