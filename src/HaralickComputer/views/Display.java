package HaralickComputer.views;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import HaralickComputer.controllers.TextureFeaturesComputer;
import HaralickComputer.core.TextureFeatures;

public class Display extends JFrame implements ActionListener, ComponentListener {
	private static final long serialVersionUID = 4923476327038677535L;
	
	JPanel panel;
	BufferedImage leftImage, rightImage;
	JMenuItem menuItemOpen, 
		menuItemDisplayEnergy, menuItemDisplayEntropy, 
		menuItemDisplayCorrelation, menuItemDisplayInverseDifferenceMoment, 
		menuItemDisplayInertia, menuItemDisplayClusterShade, 
		menuItemDisplayClusterProminence, menuItemDisplayHaralickCorrelation;
	
	JFileChooser fc;
	TextureFeaturesComputer tfc = new TextureFeaturesComputer();
	
	public Display() {
		super("HaralickDisplay");
		setMinimumSize(new Dimension(300, 200));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addComponentListener(this);
		
		leftImage = null;
		rightImage = null;
		fc = new JFileChooser();
		
		setJMenuBar(buildMenuBar());
		
		panel = new JPanel() {
			private static final long serialVersionUID = 7680070290567701814L;

			public void paintComponent(Graphics g) {
				if(leftImage != null) {
					Dimension d = getSize();
					int imageWidth, imageHeight, xOffset, yOffset;
					float containerRatio = (float) (d.height/(float)(d.width / 2.0));
					float imageRatio = leftImage.getHeight()/(float)leftImage.getWidth();
					float scale;
				
					if(containerRatio > imageRatio) {
						scale = (float) ((d.width/2.0) / (float)leftImage.getWidth());
					} else {
						scale = d.height / (float)leftImage.getHeight();
					}
					imageWidth = (int)(leftImage.getWidth() * scale);
					imageHeight = (int)(leftImage.getHeight() * scale);
					
					xOffset = (int) ((d.width - 2 * imageWidth) / 3.0);
					yOffset = (d.height - imageHeight) / 2;
					
					g.drawImage(leftImage, xOffset, yOffset, imageWidth, imageHeight, null);
					
					if(rightImage != null) {
						g.drawImage(rightImage, 2 * xOffset + imageWidth, yOffset, imageWidth, imageHeight, null);
					}
				}
			}
		};
		setContentPane(panel);

		pack();
		setVisible(true);
	}
	
	private JMenuBar buildMenuBar() {
		JMenuBar menubar = new JMenuBar();
		
		JMenu menuFile = new JMenu("File");
		menubar.add(menuFile);
		
		menuItemOpen = new JMenuItem("Open");
		menuItemOpen.addActionListener(this);
		
		menuFile.add(menuItemOpen);
		
		JMenu menuDisplay = new JMenu("Display");
		menubar.add(menuDisplay);
		
		menuItemDisplayEnergy = new JMenuItem("Display energy");
		menuItemDisplayEntropy = new JMenuItem("Display entropy"); 
		menuItemDisplayCorrelation = new JMenuItem("Display correlation");
		menuItemDisplayInverseDifferenceMoment = new JMenuItem("Display inverse difference moment"); 
		menuItemDisplayInertia = new JMenuItem("Display inertia");
		menuItemDisplayClusterShade = new JMenuItem("Display cluster shade"); 
		menuItemDisplayClusterProminence = new JMenuItem("Display cluster prominence");
		menuItemDisplayHaralickCorrelation = new JMenuItem("Display haralick correlation");
		
		menuDisplay.add(menuItemDisplayEnergy);
		menuDisplay.add(menuItemDisplayEntropy);
		menuDisplay.add(menuItemDisplayCorrelation);
		menuDisplay.add(menuItemDisplayInverseDifferenceMoment);
		menuDisplay.add(menuItemDisplayInertia);
		menuDisplay.add(menuItemDisplayClusterShade);
		menuDisplay.add(menuItemDisplayClusterProminence);
		menuDisplay.add(menuItemDisplayHaralickCorrelation);
		
		menuItemDisplayEnergy.addActionListener(this);
		menuItemDisplayEntropy.addActionListener(this);
		menuItemDisplayCorrelation.addActionListener(this);
		menuItemDisplayInverseDifferenceMoment.addActionListener(this);
		menuItemDisplayInertia.addActionListener(this);
		menuItemDisplayClusterShade.addActionListener(this);
		menuItemDisplayClusterProminence.addActionListener(this);
		menuItemDisplayHaralickCorrelation.addActionListener(this);
		
		return menubar;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JMenuItem source = (JMenuItem)e.getSource();
		
		if(source == menuItemOpen) {
			if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				try {
					leftImage = null;
					rightImage = null;
					tfc.setImageSource(ImageIO.read(fc.getSelectedFile()));
					tfc.compute();
					leftImage = tfc.getSourceImage();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		} else if(source == menuItemDisplayEnergy) {
			rightImage = tfc.getHaralickImage(TextureFeatures.Energy);
		} else if(source == menuItemDisplayEntropy) {
			rightImage = tfc.getHaralickImage(TextureFeatures.Entropy);
		} else if(source == menuItemDisplayCorrelation) {
			rightImage = tfc.getHaralickImage(TextureFeatures.Correlation);
		} else if(source == menuItemDisplayInverseDifferenceMoment) {
			rightImage = tfc.getHaralickImage(TextureFeatures.InverseDifferenceMoment);
		} else if(source == menuItemDisplayInertia) {
			rightImage = tfc.getHaralickImage(TextureFeatures.Inertia);
		} else if(source == menuItemDisplayClusterShade) {
			rightImage = tfc.getHaralickImage(TextureFeatures.ClusterShade);
		} else if(source == menuItemDisplayClusterProminence) {
			rightImage = tfc.getHaralickImage(TextureFeatures.ClusterProminence);
		} else if(source == menuItemDisplayHaralickCorrelation) {
			rightImage = tfc.getHaralickImage(TextureFeatures.HaralickCorrelation);
		}
		update(getGraphics());
	}

	@Override
	public void componentHidden(ComponentEvent e) { }

	@Override
	public void componentMoved(ComponentEvent e) { }

	@Override
	public void componentResized(ComponentEvent e) { }

	@Override
	public void componentShown(ComponentEvent e) { }
}
