package glcm;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Bitmap3D extends ShortArray {
	public Bitmap3D(int... dimensions) throws IllegalArgumentException {
		super(dimensions);
	}
	
	public Bitmap3D(Bitmap3D other) throws IllegalArgumentException {
		super(other);
	}
	
	public static Bitmap3D loadImage2D(File image) throws IOException {
		BufferedImage bi;
		bi = ImageIO.read(image);
		
		int width = bi.getWidth(), height = bi.getHeight();
		
		Bitmap3D bitmap = new Bitmap3D(width, height, 1);
		bitmap.allocate();
		
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
		ColorConvertOp op = new ColorConvertOp(cs, null);  
		BufferedImage gimg = op.filter(bi, null);
		
		int i, j;
		for(j = 0; j < height; j++) {
			for(i = 0; i < width; i++) {
				bitmap.set((short)(gimg.getRGB(i, j) & 0xff), i, j, 0);
			}
		}
		
		return bitmap;
	}
	
	public static Bitmap3D loadImage3D(File folder) throws IOException {
		File[] slices = folder.listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".bmp");
			}
		});
		
		BufferedImage bi;
		try {
			bi = ImageIO.read(slices[0]);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		int width = bi.getWidth(), height = bi.getHeight(), depth = slices.length;
		Bitmap3D bitmap = new Bitmap3D(width, height, depth);
		bitmap.allocate();
		
		int sliceNumber = 0;
		for(File slice : slices) {
			bi = ImageIO.read(slice);
			if(bi.getWidth() != width || bi.getHeight() != height) {
				System.err.println("Slices differs in size");
				return null;
			}
			
			ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
			ColorConvertOp op = new ColorConvertOp(cs, null);  
			BufferedImage gimg = op.filter(bi, null);
			
			int i, j;
			for(j = 0; j < height; j++) {
				for(i = 0; i < width; i++) {
					bitmap.set((short)(gimg.getRGB(i, j) & 0xff), i, j, sliceNumber);
				}
			}	
			
			++sliceNumber;
		}
		
		return bitmap;
	}
	
	public void posterize(int level) {
		float numberofcolors = level;
		float numberofareas = 256 / numberofcolors;

		ImageIterator it = new ImageIterator();
		while(it.isNotAtTheEnd()) {
			it.set((short) Math.rint(Math.floor(it.get() / numberofareas)));
			it.move();
		}
	}
	
	public Bitmap3D(FloatArray fa) {
		super(fa.dimensions);
		allocate();
		
		float min = Float.MAX_VALUE, max = Float.MIN_VALUE, v;
		
		for(int i = 0; i < this.offsets[this.numberOfDimensions]; ++i) {
			v = fa.data[i];
			if(v < min) min = v;
			if(v > max) max = v;
		}
		for(int i = 0; i < this.offsets[this.numberOfDimensions]; ++i) {
			this.data[i] = (short) ((fa.data[i] - min) / (max - min) * 255);
		}
	}
	
	public static void main(String[] args) {
		//Bitmap3D img = Bitmap3D.loadImage3D(new File("C:\\Users\\Cyrille\\Pictures\\stack"));
		try {
			Bitmap3D img = Bitmap3D.loadImage2D(new File("C:\\Users\\Cyrille\\Pictures\\WatershedOriginalImage-R.bmp"));
			Bitmap3D img16 = new Bitmap3D(img);
			img16.posterize(16);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
