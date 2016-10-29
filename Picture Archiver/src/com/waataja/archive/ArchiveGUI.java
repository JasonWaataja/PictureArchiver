package com.waataja.archive;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.text.DefaultCaret;

public class ArchiveGUI implements TextOutput, ActionListener {
	
	public static final int TEXT_OUTPUT_WINDOW_WIDTH_CHARACTERS = 80;
	public static final int TEXT_OUTPUT_WINDOW_HEIGHT_CHARACTERS = 20;
	public static final int MAIN_FRAME_WIDTH = 640;
	public static final int MAIN_FRAME_HEIGHT = 480;
	public static final int BUTTON_BORDER = 10;
	
	private JFrame textOutputFrame;
	private JTextArea textOutputArea;
	private JScrollPane textOutputScroller;
	
	private JFrame mainFrame;
	
	private boolean textOutputFrameVisible;
	
	private PictureArchiver archiver;
	private boolean hasArchiver;
	
	private JButton selectPicturesFolderButton;
	private JButton selectNewDirectoryButton;
	private JButton archiveButton;
	private JButton showExtraInfoButton;
	private JButton hideExtraInfoButton;
	
	private boolean hasRootDirectory;
	private boolean hasNewDirectory;
	
	public boolean isHasRootDirectory() {
		return hasRootDirectory;
	}

	public boolean isHasNewDirectory() {
		return hasNewDirectory;
	}

	public JFrame getMainFrame() {
		return mainFrame;
	}

	public void setMainFrame(JFrame mainFrame) {
		this.mainFrame = mainFrame;
	}

	public boolean isTextOutputFrameVisible() {
		return textOutputFrameVisible;
	}
	
	public void setTextOutputFrameVisible(boolean textOutputFrameVisible) {
		this.textOutputFrameVisible = textOutputFrameVisible;
		if (textOutputFrame != null) {
			textOutputFrame.setVisible(textOutputFrameVisible);
		}
	}

	public PictureArchiver getArchiver() {
		return archiver;
	}

	public void setArchiver(PictureArchiver archiver) {
		this.archiver = archiver;
	}

	public boolean isHasArchiver() {
		return hasArchiver;
	}
	
	public void setDefaults() {
		setTextOutputFrameVisible(false);
		hasRootDirectory = false;
		hasNewDirectory = false;
	}
	
	private void createTextOutputFrame() {
		textOutputFrame = new JFrame();
		textOutputFrame.setTitle("Output Window");
		textOutputFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		textOutputArea = new JTextArea(TEXT_OUTPUT_WINDOW_HEIGHT_CHARACTERS, TEXT_OUTPUT_WINDOW_WIDTH_CHARACTERS);
		textOutputArea.setEditable(false);
		textOutputScroller = new JScrollPane(textOutputArea);
		textOutputScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		textOutputScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		DefaultCaret caret = (DefaultCaret)textOutputArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		textOutputFrame.getContentPane().add(BorderLayout.CENTER, textOutputScroller);
		textOutputFrame.pack();
		textOutputFrame.setVisible(isTextOutputFrameVisible());
	}
	
	private void createButtons() {
		selectPicturesFolderButton = new JButton("select pictures folder");
		selectPicturesFolderButton.addActionListener(this);
		selectNewDirectoryButton = new JButton("select folder to copy to");
		selectNewDirectoryButton.addActionListener(this);
		archiveButton = new JButton("copy pictures\n(not ready)");
		archiveButton.addActionListener(this);
		showExtraInfoButton = new JButton("show extra info");
		showExtraInfoButton.addActionListener(this);
		hideExtraInfoButton = new JButton("hide extra info");
		hideExtraInfoButton.addActionListener(this);
	}
	
	private void initializeComponents() {
		mainFrame = new JFrame();
		mainFrame.setTitle("Picture Archiver");
		mainFrame.setSize(MAIN_FRAME_WIDTH, MAIN_FRAME_HEIGHT);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		createButtons();
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(selectPicturesFolderButton);
		buttonPanel.add(selectNewDirectoryButton);
		buttonPanel.add(archiveButton);
		JPanel showInfoButtonPanel = new JPanel();
		showInfoButtonPanel.add(showExtraInfoButton);
		showInfoButtonPanel.add(hideExtraInfoButton);	
		
		GridLayout windowContentPaneManager = new GridLayout(2, 1);
		GridLayout topButtonsManager = new GridLayout(1, 3);
		GridLayout bottomButtonsManager = new GridLayout(1, 2);
		buttonPanel.setLayout(topButtonsManager);
		showInfoButtonPanel.setLayout(bottomButtonsManager);
		mainFrame.getContentPane().setLayout(windowContentPaneManager);
		
		Border panelBorder = BorderFactory.createBevelBorder(BevelBorder.RAISED);
		buttonPanel.setBorder(panelBorder);
		showInfoButtonPanel.setBorder(panelBorder);
		
		mainFrame.getContentPane().add(buttonPanel);
		mainFrame.getContentPane().add(showInfoButtonPanel);
		mainFrame.setVisible(true);
	}
	
	/*private void sizeComponents() {
		int topButtonWidth = mainFrame.getWidth() / 3 - BUTTON_BORDER;
		int topButtonHeight = mainFrame.getHeight() * 3 / 4 - BUTTON_BORDER;
		int bottomButtonWidth = mainFrame.getWidth() / 2 - BUTTON_BORDER;
		int bottomButtonHeight = mainFrame.getHeight() / 4 - BUTTON_BORDER;
		
		selectPicturesFolderButton.setPreferredSize(new Dimension(topButtonWidth, topButtonHeight));
		selectNewDirectoryButton.setPreferredSize(new Dimension(topButtonWidth, topButtonHeight));
		archiveButton.setPreferredSize(new Dimension(topButtonWidth, topButtonHeight));
		showExtraInfoButton.setPreferredSize(new Dimension(bottomButtonWidth, bottomButtonHeight));
		hideExtraInfoButton.setPreferredSize(new Dimension(bottomButtonWidth, bottomButtonHeight));
	}*/

	public ArchiveGUI(PictureArchiver archiver) {
		setDefaults();
		setArchiver(archiver);
		hasArchiver = (archiver != null);
		createTextOutputFrame();
		initializeComponents();
	}
	
	public ArchiveGUI() {
		this(null);
	}
	
	public boolean getYesNoInput(String prompt) {
		int input = JOptionPane.showConfirmDialog(null,
				prompt, "select yes or no", JOptionPane.YES_NO_OPTION);
		if (input == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public void basicNotify(String message) {
		println(message);
		JOptionPane.showMessageDialog(null, message, "heads up", JOptionPane.PLAIN_MESSAGE);
	}
	
	public int getIntInput(String prompt) {
		try {
			String input = JOptionPane.showInputDialog(prompt);
			int inputAsInt = Integer.parseInt(input);
			if (inputAsInt < 0) {
				basicNotify("You have to enter a valid number that can be used, try again.");
				return getIntInput(prompt);
			}
			return inputAsInt;
		} catch (NumberFormatException e) {
			basicNotify("That's not a a whole number, try again");
			return getIntInput(prompt);
		}
	}
	
	public File getDirectoryInput() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int state = chooser.showOpenDialog(null);
		if (state == JFileChooser.APPROVE_OPTION) {
			File selectedFile = chooser.getSelectedFile();
			if (selectedFile.isDirectory()) {
				return selectedFile;
			} else {
				basicNotify("That's not a folder, try again.");
				return getDirectoryInput();
			}
		} else {
			return null;
		}
	}

	@Override
	public void println(String output) {
		if (textOutputArea != null) {
			textOutputArea.append(output + "\n");
		}
	}

	@Override
	public void println(int output) {
		println(Integer.toString(output));
	}

	@Override
	public void println(long output) {
		println(Long.toString(output));
	}

	@Override
	public void println(boolean output) {
		println(Boolean.toString(output));
	}

	@Override
	public void println(float output) {
		println(Float.toString(output));
	}

	@Override
	public void println(double output) {
		println(Double.toString(output));
	}

	@Override
	public void println(Object output) {
		println(output.toString());
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource() == selectPicturesFolderButton) {
			File rootDirectory = getDirectoryInput();
			if (rootDirectory != null) {
				archiver.setRootDirectory(rootDirectory);
				hasRootDirectory = true;
			}
		}
		if (arg0.getSource() == selectNewDirectoryButton) {
			File newDirectory = getDirectoryInput();
			if (newDirectory != null) {
				archiver.setNewDirectory(newDirectory);
				hasNewDirectory = true;
			}
		}
		if (arg0.getSource() == archiveButton) {
			if (!hasRootDirectory) {
				basicNotify("You don't have a directory selected to copy from.");
			} else if (!hasNewDirectory) {
				basicNotify("You don't have a directory selected to copy to.");
			} else {
				long sizeBytes = PictureArchiver.getSizeOfDirectory(archiver.getRootDirectory());
				float sizeKillobytes = sizeBytes / 1024.0f;
				float sizeMegs = sizeKillobytes / 1024.0f;
				float sizeGigs = sizeMegs / 1024.0f;
				String fileSizeMessage = sizeBytes + " bytes will be coppied";
				if (sizeGigs > 1) {
					fileSizeMessage += ", that's " + sizeGigs + " gigabytes";
				} else if (sizeMegs > 1) {
					fileSizeMessage += ", that's " + sizeMegs + " megabytes";
				} else if (sizeKillobytes > 1) {
					fileSizeMessage += ", that's " + sizeKillobytes + " killobytes";
				}
				fileSizeMessage += ". Would you like to continue?";
				boolean shouldContinue = getYesNoInput(fileSizeMessage);
				if (shouldContinue) {
					archiver.setImagesOnly(getYesNoInput("Would you like to copy images only? If you select yes, files that aren't .png or .jpg won't be coppied."));
					boolean shouldShowInfo = getYesNoInput("Would you like to view extra info while copying? A window will appear to show information while copying.");
					archiver.setPrintInfo(shouldShowInfo);
					setTextOutputFrameVisible(shouldShowInfo);
					archiver.setIncludeDuplicates(getYesNoInput("Would you like to copy files with duplicate names? If you select yes, files with the same name will be included but the old and new files will be labled copies. If you select no, files with duplicate names will not be copied."));
					archiver.setFoldersPerYear(getIntInput("How many folders would you like per year?"));
					archiver.archive();
					basicNotify("done archiving");		
				} else {
					basicNotify("didn't copy");
				}
			}
		}
		if (arg0.getSource() == showExtraInfoButton) {
			setTextOutputFrameVisible(true);
		}
		if (arg0.getSource() == hideExtraInfoButton) {
			setTextOutputFrameVisible(false);
		}
	}
}
