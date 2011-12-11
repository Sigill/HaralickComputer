package HaralickComputer.views;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import HaralickComputer.controllers.TextureFeaturesComputer;
import HaralickComputer.core.DoubleImagePanel;
import HaralickComputer.core.GLCM;
import HaralickComputer.core.GLCM_Widget;
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

public class Display extends JFrame implements ActionListener, ChangeListener, Observer {
	private static final long serialVersionUID = 4923476327038677535L;
	
	DoubleImagePanel imagesPanel;
	JMenuItem menuItemOpen, 
		menuItemDisplayEnergy, menuItemDisplayEntropy, 
		menuItemDisplayCorrelation, menuItemDisplayInverseDifferenceMoment, 
		menuItemDisplayInertia, menuItemDisplayClusterShade, 
		menuItemDisplayClusterProminence, menuItemDisplayHaralickCorrelation,
		menuSwitchDisplay;
	
	private GLCM live_GLCM;
	
	JFileChooser fc;
	TextureFeaturesComputer tfc = new TextureFeaturesComputer();
	private JLabel lblNumberOfGrayLevels;
	private JSpinner spinner_numberOfGrayLevels;
	private JButton btnValidateParameters;
	private JSpinner spinner_radiusOfWindow;
	private GLCM_Widget glcm_widget;
	private DoubleImagePanel.MouseObservable imagesPanel_mouse_observable;
	
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
		
		this.imagesPanel_mouse_observable = this.imagesPanel.getObservable();
		this.imagesPanel_mouse_observable.addObserver(this);
		
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
		
		glcm_widget = new GLCM_Widget(300);
		
		GroupLayout gl_sidebar = new GroupLayout(sidebar);
		gl_sidebar.setHorizontalGroup(
			gl_sidebar.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_sidebar.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_sidebar.createParallelGroup(Alignment.LEADING)
						.addComponent(glcm_widget, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(lblNumberOfGrayLevels)
						.addComponent(btnValidateParameters, GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
						.addComponent(spinner_numberOfGrayLevels, GroupLayout.PREFERRED_SIZE, 98, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_sidebar.createParallelGroup(Alignment.TRAILING, false)
							.addComponent(spinner_radiusOfWindow, Alignment.LEADING)
							.addComponent(lblSizeOfWindow, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
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
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(glcm_widget, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
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
			this.tfc.setNumberOfGrayLevels((Integer)spinner_numberOfGrayLevels.getValue());
			this.tfc.setSizeOfWindow((Integer)spinner_radiusOfWindow.getValue());
			this.tfc.compute();
			this.live_GLCM = new GLCM(this.tfc.getNumberOfGraylevels());
			this.live_GLCM.normalize();
		}
		update(getGraphics());
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		//Object source = e.getSource();
		
		
	}

	@Override
	public void update(Observable o, Object arg1) {
		if(o == this.imagesPanel_mouse_observable) {
			Point pos = this.imagesPanel.getMousePositionOnImage();
			
			if(!(pos.x >= 0 && pos.y >= 0 && pos.x < this.tfc.getImageWidth() && pos.y < this.tfc.getImageHeight())) {
				return;
			}
			
			if((this.live_GLCM == null) || (this.live_GLCM.getSize() != this.tfc.getNumberOfGraylevels()))
					this.live_GLCM = new GLCM(this.tfc.getNumberOfGraylevels());
			
			this.live_GLCM.reset();
			
			this.tfc.computeForPixel(this.live_GLCM, pos.x, pos.y, this.tfc.getImageWidth(), this.tfc.getImageWidth());
			this.live_GLCM.normalize();
			
			this.glcm_widget.setGLCM(this.live_GLCM);
			this.glcm_widget.repaint();
		}
		
	}
}
