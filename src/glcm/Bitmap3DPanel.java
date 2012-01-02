package glcm;

import glcm.Bitmap3D;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class Bitmap3DPanel extends JPanel {
	private static final long serialVersionUID = 6155778863809510401L;
	private JSlider sliderSliceChooser;
	private JPanel panelImage;
	private BufferedImage[] slices = null;
	
	public Bitmap3DPanel() {
		setLayout(new BorderLayout(0, 0));
		
		panelImage = new JPanel();
		add(panelImage, BorderLayout.CENTER);
		
		sliderSliceChooser = new JSlider();
		sliderSliceChooser.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider sl = (JSlider) e.getSource();
				System.out.println(sl.getValue());
				repaint();
			}
		});
		add(sliderSliceChooser, BorderLayout.SOUTH);
		this.sliderSliceChooser.setMaximum(0);
	}
	
	public void setImage3D(Bitmap3D image) {
		this.slices = null;
		sliderSliceChooser.setMaximum(image.dimensions[2] - 1);
		
		this.slices = new BufferedImage[image.dimensions[2]];
		for(int i = 0; i < this.slices.length; ++i) {
			this.slices[i] = new BufferedImage(image.dimensions[0], image.dimensions[1], BufferedImage.TYPE_INT_RGB);
		}
		
		Bitmap3D.ImageIterator it = image.new ImageIterator();
		short v;
		int[] coordinates;
		Color c;
		while(it.isNotAtTheEnd()) {
			v = it.get();
			coordinates = it.getCoordinates();
			c = new Color(v, v, v);
			slices[coordinates[2]].setRGB(coordinates[0], coordinates[1], c.getRGB());
			it.move();
		}
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if(slices == null)
			return;
		int s = sliderSliceChooser.getValue();
		g.drawImage(slices[s], 0, 0, slices[s].getWidth(), slices[s].getHeight(), null);
	}
}
