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
import javax.swing.JDialog;
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
import HaralickComputer.core.DoubleImagePanel.Orientation;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.JButton;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.border.EmptyBorder;
import javax.swing.JCheckBox;

public class Display extends JFrame implements ActionListener, ChangeListener, Observer {
	private static final long serialVersionUID = 4923476327038677535L;
	
	DoubleImagePanel imagesPanel;
	JMenuItem menuItemOpen, 
		menuItemDisplayAngularSecondMoment, menuItemDisplayEntropy, 
		menuItemDisplayAutoCorrelation, menuItemDisplayCorrelation, menuItemDisplayInverseDifferenceMoment, 
		menuItemDisplayContrast, menuItemDisplayClusterShade, 
		menuItemDisplayClusterProminence, menuItemDisplayHaralickCorrelation,
		menuItemSwitchDisplay,
		menuItemExportCSV;
	
	private GLCM live_GLCM;
	
	JFileChooser fc, fc_export;
	TextureFeaturesComputer tfc = new TextureFeaturesComputer();
	private JLabel lblNumberOfGrayLevels, lblCoocurrenceMatrix, lblXoffset, lblYoffset;
	private JButton btnValidateParameters;
	private GLCM_Widget glcm_widget;
	private DoubleImagePanel.MouseObservable imagesPanel_mouse_observable;
	private JSpinner spinner_numberOfGrayLevels, spinner_radiusOfWindow, spinner_y_offset, spinner_x_offset;
	private JCheckBox checkboxSymmetricOffset;
	
	public Display() {
		super("HaralickComputer");
		setMinimumSize(new Dimension(800, 600));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container contentPanel = getContentPane();
		
		fc = new JFileChooser();
		fc_export = new JFileChooser();
		
		setJMenuBar(buildMenuBar());
		
		contentPanel.setLayout(new BorderLayout());
		imagesPanel = new DoubleImagePanel();
		imagesPanel.setOrientation(Orientation.Vertical);
		contentPanel.add(imagesPanel, BorderLayout.CENTER);
		
		this.imagesPanel_mouse_observable = this.imagesPanel.getObservable();
		this.imagesPanel_mouse_observable.addObserver(this);
		
		contentPanel.add(buildSidebar(), BorderLayout.EAST);

		pack();
		setVisible(true);
	}
	
	private JPanel buildSidebar() {
		JPanel sidebar = new JPanel();
		sidebar.setBorder(new EmptyBorder(0, 5, 0, 5));
		GridBagLayout gbl_sidebar = new GridBagLayout();
		gbl_sidebar.columnWidths = new int[]{300, 0};
		gbl_sidebar.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_sidebar.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_sidebar.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		sidebar.setLayout(gbl_sidebar);
		
		spinner_numberOfGrayLevels = new JSpinner(new SpinnerNumberModel(TextureFeaturesComputer.DEFAULT_NUMBER_OF_GRAYLEVELS, 0, 255, 1));
		spinner_numberOfGrayLevels.addChangeListener(this);
		
		lblCoocurrenceMatrix = new JLabel("Coocurrence matrix");
		GridBagConstraints gbc_lblCoocurrenceMatrix = new GridBagConstraints();
		gbc_lblCoocurrenceMatrix.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblCoocurrenceMatrix.insets = new Insets(0, 0, 5, 0);
		gbc_lblCoocurrenceMatrix.gridx = 0;
		gbc_lblCoocurrenceMatrix.gridy = 0;
		sidebar.add(lblCoocurrenceMatrix, gbc_lblCoocurrenceMatrix);
		
		glcm_widget = new GLCM_Widget(300);
		GridBagConstraints gbc_glcm_widget = new GridBagConstraints();
		gbc_glcm_widget.anchor = GridBagConstraints.NORTH;
		gbc_glcm_widget.fill = GridBagConstraints.HORIZONTAL;
		gbc_glcm_widget.insets = new Insets(0, 0, 5, 0);
		gbc_glcm_widget.gridx = 0;
		gbc_glcm_widget.gridy = 1;
		sidebar.add(glcm_widget, gbc_glcm_widget);
		
		lblNumberOfGrayLevels = new JLabel("Number of gray levels");
		GridBagConstraints gbc_lblNumberOfGrayLevels = new GridBagConstraints();
		gbc_lblNumberOfGrayLevels.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblNumberOfGrayLevels.insets = new Insets(0, 0, 5, 0);
		gbc_lblNumberOfGrayLevels.gridx = 0;
		gbc_lblNumberOfGrayLevels.gridy = 2;
		sidebar.add(lblNumberOfGrayLevels, gbc_lblNumberOfGrayLevels);
		GridBagConstraints gbc_spinner_numberOfGrayLevels = new GridBagConstraints();
		gbc_spinner_numberOfGrayLevels.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinner_numberOfGrayLevels.anchor = GridBagConstraints.NORTH;
		gbc_spinner_numberOfGrayLevels.insets = new Insets(0, 0, 5, 0);
		gbc_spinner_numberOfGrayLevels.gridx = 0;
		gbc_spinner_numberOfGrayLevels.gridy = 3;
		sidebar.add(spinner_numberOfGrayLevels, gbc_spinner_numberOfGrayLevels);
		
		JLabel lblSizeOfWindow = new JLabel("Radius of window");
		GridBagConstraints gbc_lblSizeOfWindow = new GridBagConstraints();
		gbc_lblSizeOfWindow.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblSizeOfWindow.insets = new Insets(0, 0, 5, 0);
		gbc_lblSizeOfWindow.gridx = 0;
		gbc_lblSizeOfWindow.gridy = 4;
		sidebar.add(lblSizeOfWindow, gbc_lblSizeOfWindow);
		
		spinner_radiusOfWindow = new JSpinner(new SpinnerNumberModel(TextureFeaturesComputer.DEFAULT_WINDOW_RADIUS, 1, null, 1));
		GridBagConstraints gbc_spinner_radiusOfWindow = new GridBagConstraints();
		gbc_spinner_radiusOfWindow.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinner_radiusOfWindow.insets = new Insets(0, 0, 5, 0);
		gbc_spinner_radiusOfWindow.gridx = 0;
		gbc_spinner_radiusOfWindow.gridy = 5;
		sidebar.add(spinner_radiusOfWindow, gbc_spinner_radiusOfWindow);
		
		btnValidateParameters = new JButton("Validate parameters");
		btnValidateParameters.addActionListener(this);
		
		lblXoffset = new JLabel("X-offset");
		GridBagConstraints gbc_lblXoffset = new GridBagConstraints();
		gbc_lblXoffset.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblXoffset.insets = new Insets(0, 0, 5, 0);
		gbc_lblXoffset.gridx = 0;
		gbc_lblXoffset.gridy = 6;
		sidebar.add(lblXoffset, gbc_lblXoffset);
		
		spinner_x_offset = new JSpinner();
		spinner_x_offset.setModel(new SpinnerNumberModel(TextureFeaturesComputer.DEFAULT_X_OFFSET, null, null, 1));
		GridBagConstraints gbc_spinner_x_offset = new GridBagConstraints();
		gbc_spinner_x_offset.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinner_x_offset.insets = new Insets(0, 0, 5, 0);
		gbc_spinner_x_offset.gridx = 0;
		gbc_spinner_x_offset.gridy = 7;
		sidebar.add(spinner_x_offset, gbc_spinner_x_offset);
		
		lblYoffset = new JLabel("Y-offset");
		GridBagConstraints gbc_lblYoffset = new GridBagConstraints();
		gbc_lblYoffset.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblYoffset.insets = new Insets(0, 0, 5, 0);
		gbc_lblYoffset.gridx = 0;
		gbc_lblYoffset.gridy = 8;
		sidebar.add(lblYoffset, gbc_lblYoffset);
		
		spinner_y_offset = new JSpinner();
		spinner_y_offset.setModel(new SpinnerNumberModel(TextureFeaturesComputer.DEFAULT_Y_OFFSET, null, null, 1));
		GridBagConstraints gbc_spinner_y_offset = new GridBagConstraints();
		gbc_spinner_y_offset.fill = GridBagConstraints.HORIZONTAL;
		gbc_spinner_y_offset.insets = new Insets(0, 0, 5, 0);
		gbc_spinner_y_offset.gridx = 0;
		gbc_spinner_y_offset.gridy = 9;
		sidebar.add(spinner_y_offset, gbc_spinner_y_offset);
		
		checkboxSymmetricOffset = new JCheckBox("Symmetric offset");
		GridBagConstraints gbc_checkboxSymmetricOffset = new GridBagConstraints();
		gbc_checkboxSymmetricOffset.fill = GridBagConstraints.HORIZONTAL;
		gbc_checkboxSymmetricOffset.insets = new Insets(0, 0, 5, 0);
		gbc_checkboxSymmetricOffset.gridx = 0;
		gbc_checkboxSymmetricOffset.gridy = 10;
		sidebar.add(checkboxSymmetricOffset, gbc_checkboxSymmetricOffset);
		GridBagConstraints gbc_btnValidateParameters = new GridBagConstraints();
		gbc_btnValidateParameters.anchor = GridBagConstraints.NORTH;
		gbc_btnValidateParameters.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnValidateParameters.gridx = 0;
		gbc_btnValidateParameters.gridy = 11;
		sidebar.add(btnValidateParameters, gbc_btnValidateParameters);
		
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
		
		menuItemDisplayAngularSecondMoment = new JMenuItem("Display angular second moment");
		menuItemDisplayEntropy = new JMenuItem("Display entropy");
		menuItemDisplayAutoCorrelation = new JMenuItem("Display auto-correlation");
		menuItemDisplayCorrelation = new JMenuItem("Display correlation");
		menuItemDisplayInverseDifferenceMoment = new JMenuItem("Display inverse difference moment"); 
		menuItemDisplayContrast = new JMenuItem("Display contrast");
		menuItemDisplayClusterShade = new JMenuItem("Display cluster shade"); 
		menuItemDisplayClusterProminence = new JMenuItem("Display cluster prominence");
		menuItemDisplayHaralickCorrelation = new JMenuItem("Display haralick correlation");
		
		menuDisplay.add(menuItemDisplayAngularSecondMoment);
		menuDisplay.add(menuItemDisplayEntropy);
		menuDisplay.add(menuItemDisplayAutoCorrelation);
		menuDisplay.add(menuItemDisplayCorrelation);
		menuDisplay.add(menuItemDisplayInverseDifferenceMoment);
		menuDisplay.add(menuItemDisplayContrast);
		menuDisplay.add(menuItemDisplayClusterShade);
		menuDisplay.add(menuItemDisplayClusterProminence);
		menuDisplay.add(menuItemDisplayHaralickCorrelation);
		
		menuItemDisplayAngularSecondMoment.addActionListener(this);
		menuItemDisplayEntropy.addActionListener(this);
		menuItemDisplayAutoCorrelation.addActionListener(this);
		menuItemDisplayCorrelation.addActionListener(this);
		menuItemDisplayInverseDifferenceMoment.addActionListener(this);
		menuItemDisplayContrast.addActionListener(this);
		menuItemDisplayClusterShade.addActionListener(this);
		menuItemDisplayClusterProminence.addActionListener(this);
		menuItemDisplayHaralickCorrelation.addActionListener(this);
		
		menuDisplay.addSeparator();
		
		menuItemSwitchDisplay = new JMenuItem("Switch display");
		menuDisplay.add(menuItemSwitchDisplay);
		menuItemSwitchDisplay.addActionListener(this);
		
		JMenu menuExport = new JMenu("Export");
		menubar.add(menuExport);
		
		menuItemExportCSV = new JMenuItem("Export CSV");
		menuExport.add(menuItemExportCSV);
		menuItemExportCSV.addActionListener(this);
		
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
		} else if(source == menuItemDisplayAngularSecondMoment) {
			imagesPanel.setRightImage(tfc.getHaralickImage(TextureFeatures.AngularSecondMoment));
		} else if(source == menuItemDisplayEntropy) {
			imagesPanel.setRightImage(tfc.getHaralickImage(TextureFeatures.Entropy));
		} else if(source == menuItemDisplayAutoCorrelation) {
			imagesPanel.setRightImage(tfc.getHaralickImage(TextureFeatures.AutoCorrelation));
		} else if(source == menuItemDisplayCorrelation) {
			imagesPanel.setRightImage(tfc.getHaralickImage(TextureFeatures.Correlation));
		} else if(source == menuItemDisplayInverseDifferenceMoment) {
			imagesPanel.setRightImage(tfc.getHaralickImage(TextureFeatures.InverseDifferenceMoment));
		} else if(source == menuItemDisplayContrast) {
			imagesPanel.setRightImage(tfc.getHaralickImage(TextureFeatures.Contrast));
		} else if(source == menuItemDisplayClusterShade) {
			imagesPanel.setRightImage(tfc.getHaralickImage(TextureFeatures.ClusterShade));
		} else if(source == menuItemDisplayClusterProminence) {
			imagesPanel.setRightImage(tfc.getHaralickImage(TextureFeatures.ClusterProminence));
		} else if(source == menuItemDisplayHaralickCorrelation) {
			imagesPanel.setRightImage(tfc.getHaralickImage(TextureFeatures.HaralickCorrelationITK));
		} else if(source == menuItemSwitchDisplay) {
			imagesPanel.changeOrientation();
		} else if (source == btnValidateParameters) {
			this.tfc.setNumberOfGrayLevels((Integer)spinner_numberOfGrayLevels.getValue());
			this.tfc.setSizeOfWindow((Integer)spinner_radiusOfWindow.getValue());
			this.tfc.setxOffset((Integer)spinner_x_offset.getValue());
			this.tfc.setyOffset((Integer)spinner_y_offset.getValue());
			this.tfc.setSymmetricOffset(checkboxSymmetricOffset.isSelected());
			this.tfc.compute();
			this.live_GLCM = new GLCM(this.tfc.getNumberOfGraylevels());
			this.live_GLCM.normalize();
		} else if (source == menuItemExportCSV) {
			if(fc_export.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				this.tfc.exportCSV(fc_export.getSelectedFile()); 
			}
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
			
			this.tfc.computeForPixel(this.live_GLCM, pos.x, pos.y, this.tfc.getImageWidth(), this.tfc.getImageHeight());
			this.live_GLCM.normalize();
			
			this.glcm_widget.setGLCM(this.live_GLCM);
			this.glcm_widget.repaint();
		}
		
	}
}
