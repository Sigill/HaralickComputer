package glcm;

public class Utils {
	public static void addOffset(int[] origin, int[] offset, int[] target) {
		for(int i = 0; i < target.length; ++i) {
			target[i] = origin[i] + offset[i];
		}
	}
}
