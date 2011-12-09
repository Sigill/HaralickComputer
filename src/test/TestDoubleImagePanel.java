package test;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import HaralickComputer.core.DoubleImagePanel;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;

public class TestDoubleImagePanel extends JFrame {
	private static final long serialVersionUID = -7702774643567852360L;
	public DoubleImagePanel panel;

	public TestDoubleImagePanel() {
		panel = new DoubleImagePanel();
		
		getContentPane().add(panel, BorderLayout.CENTER);
		
		try {
			panel.setLeftImage(ImageIO.read(new File("Textures/tex1.bmp")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		TestDoubleImagePanel w = new TestDoubleImagePanel();
		
		w.setVisible(true);
	}

}
