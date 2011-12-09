package HaralickComputer.views;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import HaralickComputer.controllers.TextureFeaturesComputer;
import HaralickComputer.core.DoubleImagePanel;
import HaralickComputer.core.TextureFeatures;

public class Display extends JFrame implements ActionListener, ComponentListener {
	private static final long serialVersionUID = 4923476327038677535L;
	
	DoubleImagePanel panel;
	JMenuItem menuItemOpen, 
		menuItemDisplayEnergy, menuItemDisplayEntropy, 
		menuItemDisplayCorrelation, menuItemDisplayInverseDifferenceMoment, 
		menuItemDisplayInertia, menuItemDisplayClusterShade, 
		menuItemDisplayClusterProminence, menuItemDisplayHaralickCorrelation,
		menuSwitchDisplay;
	
	JFileChooser fc;
	TextureFeaturesComputer tfc = new TextureFeaturesComputer();
	
	public Display() {
		super("HaralickComputer");
		setMinimumSize(new Dimension(300, 200));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addComponentListener(this);
		
		fc = new JFileChooser();
		
		setJMenuBar(buildMenuBar());
		
		panel = new DoubleImagePanel();
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
		
		menuDisplay.addSeparator();
		
		menuSwitchDisplay = new JMenuItem("Switch display");
		menuDisplay.add(menuSwitchDisplay);
		menuSwitchDisplay.addActionListener(this);
		
		return menubar;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JMenuItem source = (JMenuItem)e.getSource();
		
		if(source == menuItemOpen) {
			if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				try {
					panel.setLeftImage(null);
					panel.setRightImage(null);
					tfc.setImageSource(ImageIO.read(fc.getSelectedFile()));
					tfc.compute();
					panel.setLeftImage(tfc.getSourceImage());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		} else if(source == menuItemDisplayEnergy) {
			panel.setRightImage(tfc.getHaralickImage(TextureFeatures.Energy));
		} else if(source == menuItemDisplayEntropy) {
			panel.setRightImage(tfc.getHaralickImage(TextureFeatures.Entropy));
		} else if(source == menuItemDisplayCorrelation) {
			panel.setRightImage(tfc.getHaralickImage(TextureFeatures.Correlation));
		} else if(source == menuItemDisplayInverseDifferenceMoment) {
			panel.setRightImage(tfc.getHaralickImage(TextureFeatures.InverseDifferenceMoment));
		} else if(source == menuItemDisplayInertia) {
			panel.setRightImage(tfc.getHaralickImage(TextureFeatures.Inertia));
		} else if(source == menuItemDisplayClusterShade) {
			panel.setRightImage(tfc.getHaralickImage(TextureFeatures.ClusterShade));
		} else if(source == menuItemDisplayClusterProminence) {
			panel.setRightImage(tfc.getHaralickImage(TextureFeatures.ClusterProminence));
		} else if(source == menuItemDisplayHaralickCorrelation) {
			panel.setRightImage(tfc.getHaralickImage(TextureFeatures.HaralickCorrelation));
		} else if(source == menuSwitchDisplay) {
			panel.changeDirection();
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
