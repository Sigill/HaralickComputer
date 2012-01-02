package glcm;

import javax.swing.JFrame;

import java.awt.Dimension;

import javax.swing.JFileChooser;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.BorderLayout;
import java.io.IOException;

public class Bitmap3D_Viewer extends JFrame {
	private static final long serialVersionUID = -6343120761600990522L;
	private JMenuBar menuBar;
	private JMenu menuFile;
	private JMenuItem menuItemOpen2D;
	private JMenuItem menuItemOpen3D;
	
	private JFileChooser fc2D, fc3D;
	private Bitmap3D bitmap3D;
	private Bitmap3DPanel image3DPanel;
	
	public Bitmap3D_Viewer() {
		super("Image 3D Viewer");
		setPreferredSize(new Dimension(400, 320));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		fc2D = new JFileChooser();
		fc2D.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc3D = new JFileChooser();
		fc3D.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		menuFile = new JMenu("File");
		menuBar.add(menuFile);
		menuItemOpen2D = new JMenuItem("Open 2D image");
		menuItemOpen2D.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(fc2D.showOpenDialog(Bitmap3D_Viewer.this) == JFileChooser.APPROVE_OPTION) {
					bitmap3D = null;
					try {
						bitmap3D = Bitmap3D.loadImage2D(fc2D.getSelectedFile());
						image3DPanel.setImage3D(bitmap3D);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		menuFile.add(menuItemOpen2D);
		menuItemOpen3D = new JMenuItem("Open 3D image");
		menuItemOpen3D.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(fc3D.showOpenDialog(Bitmap3D_Viewer.this) == JFileChooser.APPROVE_OPTION) {
					bitmap3D = null;
					try {
						bitmap3D = Bitmap3D.loadImage3D(fc3D.getSelectedFile());
						image3DPanel.setImage3D(bitmap3D);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		menuFile.add(menuItemOpen3D);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		image3DPanel = new Bitmap3DPanel();
		getContentPane().add(image3DPanel, BorderLayout.CENTER);
	}
	
	public static void main(String[] args) {
		Bitmap3D_Viewer viewer = new Bitmap3D_Viewer();
		viewer.pack();
		viewer.setVisible(true);
	}
}
