package glcm;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

public class GLCM_Widget extends JPanel {
	private static final long serialVersionUID = 6080122179815105369L;
	
	private GLCM glcm;
	
	public GLCM_Widget(int size) {
		super();
		setSize(size, size);
		setPreferredSize(new Dimension(size, size));
	}

	public void paintComponent(Graphics g) {
		if(this.glcm == null)
			return;
		
		Graphics2D g2 = (Graphics2D) g;
		
		int glcm_size = glcm.getSize();
		
		int size = getWidth();
		float max = this.glcm.getMax();
		float rect_width = size / (float)glcm_size;
		int graylevel;
		
		g.clearRect(0, 0, this.getWidth(), this.getHeight());
		
		for(int i = 0; i < this.glcm.getSize(); ++i) {
			for(int j = 0; j < this.glcm.getSize(); ++j) {
				graylevel = (int)(255 * this.glcm.get(i, j) / max);
				g.setColor(new Color(graylevel, graylevel, graylevel));
				//g.fillRect((int)Math.round(i * rect_width), (int)Math.round(j * rect_width), (int)Math.round(rect_width), (int)Math.round(rect_width));
				g2.setColor(new Color(graylevel, graylevel, graylevel));
				g2.fill(new Rectangle2D.Float(i * rect_width, j * rect_width,
						rect_width,
						rect_width));
			}
		}
		
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, size-1, size-1);
	}
	
	public void setGLCM(GLCM glcm) {
		this.glcm = glcm;
	}
}
