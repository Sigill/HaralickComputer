package glcm;

public class HaralickComputer {
	public final static int 
		AngularSecondMoment = 0, 
		Entropy = 1, 
		AutoCorrelation = 2,
		Correlation = 3,
		SumOfSquaresVariance = 4,
		SumAverage = 5,
		InverseDifferenceMoment = 6, 
		Contrast = 7,
		ClusterShade = 8, 
		ClusterProminence = 9,
		HaralickCorrelationITK = 10;

	public final static int numberOfFeatures = 11;
	
	private int glcmSize;
	
	private float _marginalMean, _marginalDevSquared;
	
	private float[] 
		_mu_i, _mu_j, _s_i, _s_j,
		_p_x, _p_y;
	
	private float _mu_x, _mu_y, _s_x, _s_y;
	
	public float _angularSecondMoment, _contrast, _autoCorrelation, _correlation, 
		_sumOfSquaresVariance, _sumAverage, _entropy,
		_inverseDifferenceMoment, _clusterShade,
		_clusterProminence, _haralickCorrelationITK;
	
	public HaralickComputer(int glcmSize) {
		this.glcmSize = glcmSize;
		this._mu_i = new float[this.glcmSize];
		this._mu_j = new float[this.glcmSize];
		this._s_i = new float[this.glcmSize];
		this._s_j = new float[this.glcmSize];
		
		this._p_x = new float[this.glcmSize];
		this._p_y = new float[this.glcmSize];
	}
	
	private void computeMeansAndVariance(GLCM glcm) {
		float[] marginalSums = new float[this.glcmSize];
		int i, j, k;
		float f;
		
		for(i = 0 ; i < this.glcmSize; i++) { marginalSums[i] = 0; }
		
		for(i = 0 ; i < this.glcmSize; i++) {
			for(j = 0 ; j < this.glcmSize; j++) {
				marginalSums[i] += glcm.glcm_norm[j * this.glcmSize + i];
			}
		}
		
		
		float M_k_minus_1, S_k_minus_1, x_k, M_k, S_k;
		
		this._marginalMean = 0;
		this._marginalDevSquared = 0;
		
		for(i = 0 ; i < this.glcmSize; i++) {
		    k = i + 1;
		    M_k_minus_1 = this._marginalMean;
		    S_k_minus_1 = this._marginalDevSquared;
		    x_k = marginalSums[i];
		        
		    M_k = M_k_minus_1 + (x_k - M_k_minus_1) / k;
		    S_k = S_k_minus_1 + (x_k - M_k_minus_1) * (x_k - M_k);
		        
		   this._marginalMean = M_k;
		   this._marginalDevSquared = S_k;
		}
		
		this._marginalDevSquared = this._marginalDevSquared / this.glcmSize;
		
		float mu_i;
		for(i = 0; i < this.glcmSize; ++i) {
			mu_i = 0;
			for(j = 0; j < this.glcmSize; ++j) {
				mu_i += i * glcm.glcm_norm[j * this.glcmSize + i];
			}
			this._mu_i[i] = mu_i;
		}
		
		float mu_j;
		for(j = 0; j < this.glcmSize; ++j) {
			mu_j = 0;
			for(i = 0; i < this.glcmSize; ++i) {
				mu_j += j * glcm.glcm_norm[j * this.glcmSize + i];
			}
			this._mu_j[j] = mu_j;
		}
		
		float s_i;
		for(i = 0; i < this.glcmSize; ++i) {
			s_i = 0;
			mu_i = this._mu_i[i];
			for(j = 0; j < this.glcmSize; ++j) {
				s_i += (i - mu_i) * (i - mu_i) * glcm.glcm_norm[j * this.glcmSize + i];
			}
			this._s_i[i] = (float) Math.sqrt(s_i);
		}
		
		float s_j;
		for(j = 0; j < this.glcmSize; ++j) {
			s_j = 0;
			mu_j = this._mu_j[j];
			for(i = 0; i < this.glcmSize; ++i) {
				s_j += (j - mu_j) * (j - mu_j) * glcm.glcm_norm[j * this.glcmSize + i];
			}
			this._s_j[j] = (float) Math.sqrt(s_j);
		}
		
		
		// HARALICK BASED MEANS & STD
		float p_x;
		for(i = 0; i < this.glcmSize; ++i) {
			p_x = 0;
			for(j = 0; j < this.glcmSize; ++j) {
				p_x += glcm.glcm_norm[j * this.glcmSize + i];
			}
			this._p_x[i] = p_x;
		}
		
		float p_y;
		for(j = 0; j < this.glcmSize; ++j) {
			p_y = 0;
			for(i = 0; i < this.glcmSize; ++i) {
				p_y += glcm.glcm_norm[j * this.glcmSize + i];
			}
			this._p_y[j] = p_y;
		}
		
		this._mu_x = 0;
		this._mu_y = 0;
		for(i = 0; i < this.glcmSize; ++i) {
			for(j = 0; j < this.glcmSize; ++j) {
				f = glcm.glcm_norm[j * this.glcmSize + i];
				this._mu_x += i * f;
				this._mu_y += j * f;
			}
		}
		
		this._s_x = 0;
		this._s_y = 0;
		for(i = 0; i < this.glcmSize; ++i) {
			for(j = 0; j < this.glcmSize; ++j) {
				f = glcm.glcm_norm[j * this.glcmSize + i];
				this._s_x += Math.sqrt((i - this._mu_x) * (i - this._mu_x) * f);
				this._s_y += Math.sqrt((j - this._mu_y) * (j - this._mu_y) * f);
			}
		}
		// END OF HARALICK BASED MEANS & STD
	}
	
	public void compute(GLCM glcm) {
		float f;
		int i, j;
		glcm.normalize();
		computeMeansAndVariance(glcm);
		double log2 = Math.log(2);
		
		_angularSecondMoment = 0; _entropy = 0; _autoCorrelation = 0; _correlation = 0; 
		_sumOfSquaresVariance = 0; _inverseDifferenceMoment = 0; _sumAverage = 0; 
		_contrast = 0; _clusterShade = 0; _clusterProminence = 0; _haralickCorrelationITK = 0;
		

		for(i = 0 ; i < this.glcmSize; i++) {
			for(j = 0 ; j < this.glcmSize; j++) {
				f = glcm.glcm_norm[j * this.glcmSize + i];
				
				if(f == 0) continue;
				
			    this._angularSecondMoment += f * f;
			    this._contrast += (i - j) * (i - j) * f;
			    this._autoCorrelation += i * j * f;
			    
			    this._sumOfSquaresVariance += (i - this._mu_x) * (i - this._mu_x) * f; 
			    this._inverseDifferenceMoment += f / (1.0 + (i - j) * (i - j) );
			    this._sumAverage += (i + j) * f;
			    
			    this._entropy -= (f > 0.0001) ? f * Math.log(f) / log2 : 0;
			    
			    this._clusterShade += Math.pow((i - this._mu_x) + (j - this._mu_y), 3) * f;
			    this._clusterProminence += Math.pow((i - this._mu_x) + (j - this._mu_y), 4) * f;
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
	
	public float getFeature(int feature) {
		switch(feature) {
			case AngularSecondMoment:
				return this._angularSecondMoment;
			case Entropy:
				return this._entropy;
			case AutoCorrelation:
				return this._autoCorrelation;
			case Correlation:
				return this._correlation;
			case SumOfSquaresVariance:
				return this._sumOfSquaresVariance;
			case InverseDifferenceMoment:
				return this._inverseDifferenceMoment;
			case SumAverage:
				return this._sumAverage;
			case Contrast:
				return this._contrast;
			case ClusterShade:
				return this._clusterShade;
			case ClusterProminence:
				return this._clusterProminence;
			case HaralickCorrelationITK:
				return this._haralickCorrelationITK;
			default:
				return 0;
		}
	}
}
