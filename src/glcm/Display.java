package glcm;

import glcm.DoubleBitmap3DPanel.Orientation;
import glcm.Bitmap3D;

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
	
	DoubleBitmap3DPanel imagesPanel;
	JMenuItem menuitemOpen2D, 
		menuItemDisplayAngularSecondMoment, menuItemDisplayEntropy, 
		menuItemDisplayAutoCorrelation, menuItemDisplayCorrelation,
		menuItemDisplaySumOfSquaresVariance, menuItemDisplayInverseDifferenceMoment,
		menuItemDisplaySumAverage,
		menuItemDisplayContrast, menuItemDisplayClusterShade, 
		menuItemDisplayClusterProminence, menuItemDisplayHaralickCorrelation,
		menuItemSwitchDisplay,
		menuItemExportCSV,
		menuitemOpen3D;
		
	JFileChooser fc2D, fc_export, fc3D;
	private JLabel lblNumberOfGrayLevels, lblCoocurrenceMatrix, lblXoffset, lblYoffset;
	private JButton btnValidateParameters;
	private GLCM_Widget glcm_widget;
	private DoubleBitmap3DPanel.MouseObservable imagesPanel_mouse_observable;
	private JSpinner spinner_numberOfGrayLevels, spinner_radiusOfWindow, spinner_y_offset, spinner_x_offset;
	private JCheckBox checkboxSymmetricOffset;
	
	private Controller controller = new Controller();
		
	public Display() {
		super("Haralick 3D Computer");
		setMinimumSize(new Dimension(800, 600));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container contentPanel = getContentPane();
		
		fc2D = new JFileChooser();
		fc2D.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc3D = new JFileChooser();
		fc3D.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc_export = new JFileChooser();
		
		setJMenuBar(buildMenuBar());
		
		contentPanel.setLayout(new BorderLayout());
		imagesPanel = new DoubleBitmap3DPanel();
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
		
		spinner_numberOfGrayLevels = new JSpinner(new SpinnerNumberModel(Controller.DEFAULT_NUMBER_OF_GRAYLEVELS, 0, 255, 1));
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
		
		spinner_radiusOfWindow = new JSpinner(new SpinnerNumberModel(Controller.DEFAULT_WINDOW_RADIUS, 1, null, 1));
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
		spinner_x_offset.setModel(new SpinnerNumberModel(Controller.DEFAULT_X_OFFSET, null, null, 1));
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
		spinner_y_offset.setModel(new SpinnerNumberModel(Controller.DEFAULT_Y_OFFSET, null, null, 1));
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
	
	private JMenuItem addItemDisplay(String text, JMenu menu) {
		JMenuItem item = new JMenuItem(text);
		item.addActionListener(this);
		menu.add(item);
		return item;
	}
	
	private JMenuBar buildMenuBar() {
		JMenuBar menubar = new JMenuBar();
		
		JMenu menuFile = new JMenu("File");
		menubar.add(menuFile);
		
		menuitemOpen2D = new JMenuItem("Open 2D image");
		menuitemOpen2D.addActionListener(this);
		menuFile.add(menuitemOpen2D);
		
		menuitemOpen3D = new JMenuItem("Open 3D image");
		menuitemOpen3D.addActionListener(this);
		menuFile.add(menuitemOpen3D);
		
		JMenu menuDisplay = new JMenu("Display");
		menubar.add(menuDisplay);
		
		menuItemDisplayAngularSecondMoment = addItemDisplay("Display angular second moment", menuDisplay);
		menuItemDisplayEntropy = addItemDisplay("Display entropy", menuDisplay);
		menuItemDisplayAutoCorrelation = addItemDisplay("Display auto-correlation", menuDisplay);
		menuItemDisplayCorrelation = addItemDisplay("Display correlation", menuDisplay);
		menuItemDisplaySumOfSquaresVariance = addItemDisplay("Display sum of squares variance", menuDisplay);
		menuItemDisplaySumAverage = addItemDisplay("Display sum average", menuDisplay);
		menuItemDisplayInverseDifferenceMoment = addItemDisplay("Display inverse difference moment", menuDisplay); 
		menuItemDisplayContrast = addItemDisplay("Display contrast", menuDisplay);
		menuItemDisplayClusterShade = addItemDisplay("Display cluster shade", menuDisplay); 
		menuItemDisplayClusterProminence = addItemDisplay("Display cluster prominence", menuDisplay);
		menuItemDisplayHaralickCorrelation = addItemDisplay("Display haralick correlation", menuDisplay);
		
		menuDisplay.addSeparator();
		
		menuItemSwitchDisplay = addItemDisplay("Switch display", menuDisplay);
		
		JMenu menuExport = new JMenu("Export");
		menubar.add(menuExport);
		
		menuItemExportCSV = addItemDisplay("Export CSV", menuExport);
		
		return menubar;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		
		if(source == menuitemOpen2D) {
			if(fc2D.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				try {
					this.controller.imageSource = Bitmap3D.loadImage2D(fc2D.getSelectedFile());
					this.controller.process();
					imagesPanel.clear();
					
					imagesPanel.setLeftImage(this.controller.imageSource);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		} else if(source == menuitemOpen3D) {
			if(fc3D.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				try {
					this.controller.imageSource = Bitmap3D.loadImage3D(fc3D.getSelectedFile());
					this.controller.process();
					imagesPanel.clear();
					
					imagesPanel.setLeftImage(this.controller.imageSource);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		} else if(source == menuItemDisplayAngularSecondMoment) {
			imagesPanel.setRightImage(this.controller.haralickImages[HaralickComputer.AngularSecondMoment]);
		} else if(source == menuItemDisplayEntropy) {
			imagesPanel.setRightImage(this.controller.haralickImages[HaralickComputer.Entropy]);
		} else if(source == menuItemDisplayAutoCorrelation) {
			imagesPanel.setRightImage(this.controller.haralickImages[HaralickComputer.AutoCorrelation]);
		} else if(source == menuItemDisplayCorrelation) {
			imagesPanel.setRightImage(this.controller.haralickImages[HaralickComputer.Correlation]);
		} else if(source == menuItemDisplaySumOfSquaresVariance) {
			imagesPanel.setRightImage(this.controller.haralickImages[HaralickComputer.SumOfSquaresVariance]);
		} else if(source == menuItemDisplayInverseDifferenceMoment) {
			imagesPanel.setRightImage(this.controller.haralickImages[HaralickComputer.InverseDifferenceMoment]);
		} else if(source == menuItemDisplaySumAverage) {
			imagesPanel.setRightImage(this.controller.haralickImages[HaralickComputer.SumAverage]);
		} else if(source == menuItemDisplayContrast) {
			imagesPanel.setRightImage(this.controller.haralickImages[HaralickComputer.Contrast]);
		} else if(source == menuItemDisplayClusterShade) {
			imagesPanel.setRightImage(this.controller.haralickImages[HaralickComputer.ClusterShade]);
		} else if(source == menuItemDisplayClusterProminence) {
			imagesPanel.setRightImage(this.controller.haralickImages[HaralickComputer.ClusterProminence]);
		} else if(source == menuItemDisplayHaralickCorrelation) {
			imagesPanel.setRightImage(this.controller.haralickImages[HaralickComputer.HaralickCorrelationITK]);
		} else if(source == menuItemSwitchDisplay) {
			imagesPanel.changeOrientation();
		} else if (source == btnValidateParameters) {
			this.controller.numberOfGraylevels = (Integer)this.spinner_numberOfGrayLevels.getValue();
			this.controller.windowRadius = (Integer)this.spinner_radiusOfWindow.getValue();
			this.controller.symmetricOffset = this.checkboxSymmetricOffset.isSelected();
			this.controller.xOffset = (Integer)this.spinner_x_offset.getValue();
			this.controller.yOffset = (Integer)this.spinner_y_offset.getValue();
			this.controller.process();
		} else if (source == menuItemExportCSV) {
			if(fc_export.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
				this.controller.exportCSV(fc_export.getSelectedFile());
			}
		}
		
		update(getGraphics());
	}

	@Override
	public void stateChanged(ChangeEvent e) {}

	@Override
	public void update(Observable o, Object arg1) {
		
		if(o == this.imagesPanel_mouse_observable) {
			Point pos = this.imagesPanel.getMousePositionOnImage();
			
			if(!(
					pos.x >= 0 && 
					pos.y >= 0 && 
					pos.x < this.controller.imageSource.dimensions[0] && 
					pos.y < this.controller.imageSource.dimensions[1])) {
				return;
			}
			
			this.controller.nit.setCenterPixel(pos.x, pos.y, this.imagesPanel.getSliceNumber());
			this.controller.glcmOp.resetGLCM();
			this.controller.glcmOp.compute(this.controller.nit);
			
			this.glcm_widget.setGLCM(this.controller.glcmOp.getGLCM());
			this.glcm_widget.repaint();
		}
	}
	
	public static void main(String[] args) {
		Display viewer = new Display();
	}
}
