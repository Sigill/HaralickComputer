package HaralickComputer.views;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import HaralickComputer.controllers.TextureFeaturesComputer;
import HaralickComputer.core.DoubleImagePanel;
import HaralickComputer.core.TextureFeatures;
import javax.swing.JLabel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JSpinner;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SpinnerNumberModel;
import javax.swing.JButton;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class Display extends JFrame implements ActionListener, ChangeListener {
	private static final long serialVersionUID = 4923476327038677535L;
	
	DoubleImagePanel imagesPanel;
	JMenuItem menuItemOpen, 
		menuItemDisplayEnergy, menuItemDisplayEntropy, 
		menuItemDisplayCorrelation, menuItemDisplayInverseDifferenceMoment, 
		menuItemDisplayInertia, menuItemDisplayClusterShade, 
		menuItemDisplayClusterProminence, menuItemDisplayHaralickCorrelation,
		menuSwitchDisplay;
	
	JFileChooser fc;
	TextureFeaturesComputer tfc = new TextureFeaturesComputer();
	private JLabel lblNumberOfGrayLevels;
	private JSpinner spinner_numberOfGrayLevels;
	private JButton btnValidateParameters;
	private JSpinner spinner_radiusOfWindow;
	
	public Display() {
		super("HaralickComputer");
		setMinimumSize(new Dimension(500, 400));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container contentPanel = getContentPane();
		
		fc = new JFileChooser();
		
		setJMenuBar(buildMenuBar());
		
		imagesPanel = new DoubleImagePanel();
		contentPanel.setLayout(new BorderLayout());
		contentPanel.add(imagesPanel, BorderLayout.CENTER);
		
		contentPanel.add(buildSidebar(), BorderLayout.EAST);

		pack();
		setVisible(true);
	}
	
	private JPanel buildSidebar() {
		JPanel sidebar = new JPanel();
		
		lblNumberOfGrayLevels = new JLabel("Number of gray levels");
		
		spinner_numberOfGrayLevels = new JSpinner(new SpinnerNumberModel(16, 0, 255, 1));
		spinner_numberOfGrayLevels.addChangeListener(this);
		
		btnValidateParameters = new JButton("Validate parameters");
		btnValidateParameters.addActionListener(this);
		
		JLabel lblSizeOfWindow = new JLabel("Radius of window");
		
		spinner_radiusOfWindow = new JSpinner(new SpinnerNumberModel(2, 1, null, 1));
		
		GroupLayout gl_sidebar = new GroupLayout(sidebar);
		gl_sidebar.setHorizontalGroup(
			gl_sidebar.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_sidebar.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_sidebar.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNumberOfGrayLevels)
						.addComponent(btnValidateParameters, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(lblSizeOfWindow)
						.addGroup(gl_sidebar.createParallelGroup(Alignment.TRAILING, false)
							.addComponent(spinner_radiusOfWindow, Alignment.LEADING)
							.addComponent(spinner_numberOfGrayLevels, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)))
					.addContainerGap())
		);
		gl_sidebar.setVerticalGroup(
			gl_sidebar.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_sidebar.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblNumberOfGrayLevels)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(spinner_numberOfGrayLevels, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(lblSizeOfWindow)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(spinner_radiusOfWindow, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED, 202, Short.MAX_VALUE)
					.addComponent(btnValidateParameters)
					.addContainerGap())
		);
		sidebar.setLayout(gl_sidebar);
		
		return sidebar;
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
		Object source = e.getSource();
		
		if(source == menuItemOpen) {
			if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				try {
					imagesPanel.setLeftImage(null);
					imagesPanel.setRightImage(null);
					tfc.setImageSource(ImageIO.read(fc.getSelectedFile()));
					tfc.compute();
					imagesPanel.setLeftImage(tfc.getSourceImage());
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		} else if(source == menuItemDisplayEnergy) {
			imagesPanel.setRightImage(tfc.getHaralickImage(TextureFeatures.Energy));
		} else if(source == menuItemDisplayEntropy) {
			imagesPanel.setRightImage(tfc.getHaralickImage(TextureFeatures.Entropy));
		} else if(source == menuItemDisplayCorrelation) {
			imagesPanel.setRightImage(tfc.getHaralickImage(TextureFeatures.Correlation));
		} else if(source == menuItemDisplayInverseDifferenceMoment) {
			imagesPanel.setRightImage(tfc.getHaralickImage(TextureFeatures.InverseDifferenceMoment));
		} else if(source == menuItemDisplayInertia) {
			imagesPanel.setRightImage(tfc.getHaralickImage(TextureFeatures.Inertia));
		} else if(source == menuItemDisplayClusterShade) {
			imagesPanel.setRightImage(tfc.getHaralickImage(TextureFeatures.ClusterShade));
		} else if(source == menuItemDisplayClusterProminence) {
			imagesPanel.setRightImage(tfc.getHaralickImage(TextureFeatures.ClusterProminence));
		} else if(source == menuItemDisplayHaralickCorrelation) {
			imagesPanel.setRightImage(tfc.getHaralickImage(TextureFeatures.HaralickCorrelation));
		} else if(source == menuSwitchDisplay) {
			imagesPanel.changeOrientation();
		} else if (source == btnValidateParameters) {
			tfc.setNumberOfGrayLevels((Integer)spinner_numberOfGrayLevels.getValue());
			tfc.setSizeOfWindow((Integer)spinner_radiusOfWindow.getValue());
			tfc.compute();
		}
		update(getGraphics());
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		//Object source = e.getSource();
		
		
	}
}
