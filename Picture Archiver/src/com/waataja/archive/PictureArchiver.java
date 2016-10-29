package com.waataja.archive;

import java.io.*;
import java.util.*;
import java.nio.channels.FileChannel;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class PictureArchiver {
	private File rootDirectory;
	private File newDirectory;
	
	private boolean includeDuplicates;
	private boolean printInfo;
	private boolean imagesOnly;
	
	private TextOutput console;
	
	private int foldersPerYear;
	
	public TextOutput getConsole() {
		return console;
	}

	public void setConsole(TextOutput console) {
		this.console = console;
	}

	public int getFoldersPerYear() {
		return foldersPerYear;
	}

	public void setFoldersPerYear(int foldersPerYear) {
		this.foldersPerYear = foldersPerYear;
	}
	
	public void setDefaults() {
		setFoldersPerYear(2);
		setIncludeDuplicates(false);
		setPrintInfo(true);
		setImagesOnly(false);
		console = new ConsoleOutput();
	}

	public PictureArchiver(File rootDirectory, File newDirectory) {
		setDefaults();
		
		setRootDirectory(rootDirectory);
		setNewDirectory(newDirectory);
	}
	
	public PictureArchiver() {
		this(null, null);
	}

	public File getRootDirectory() {
		return rootDirectory;
	}

	public void setRootDirectory(File rootDirectory) {
		this.rootDirectory = rootDirectory;
	}

	public File getNewDirectory() {
		return newDirectory;
	}

	public void setNewDirectory(File newDirectory) {
		this.newDirectory = newDirectory;
	}

	public boolean isIncludeDuplicates() {
		return includeDuplicates;
	}

	public void setIncludeDuplicates(boolean includeDuplicates) {
		this.includeDuplicates = includeDuplicates;
	}

	public boolean isPrintInfo() {
		return printInfo;
	}

	public void setPrintInfo(boolean printInfo) {
		this.printInfo = printInfo;
	}

	public boolean isImagesOnly() {
		return imagesOnly;
	}

	public void setImagesOnly(boolean imagesOnly) {
		this.imagesOnly = imagesOnly;
	}
	
	public static final int START_YEAR = 1970;
	
	public static float yearLastModified(File file) {
		long milliseconds = file.lastModified();
		long seconds = milliseconds / 1000;
		double minutes = seconds / 60;
		double hours = minutes / 60;
		double days = hours / 24;
		float years = (float) days / 365.0f + START_YEAR;
		return years;
	}
	
	public static ArrayList<File> getAllFilesInDirectory(File file) {
		ArrayList<File> allFiles = new ArrayList<File>();
		File[] fileList = file.listFiles();
		if (fileList != null) {
			for (File subFile : fileList) {
				if (subFile.isDirectory()) {
					allFiles.addAll(getAllFilesInDirectory(subFile));
				}
				if (subFile.isFile()) {
					allFiles.add(subFile);
				}
			}
		}
		
		return allFiles;
	}
	
	public static long getSizeOfDirectory(File directory) {
		if (directory != null && directory.exists() && directory.isDirectory()) {
			ArrayList<File> allFiles = PictureArchiver.getAllFilesInDirectory(directory);
			long totalSize = 0;
			for (File file : allFiles) {
				totalSize += file.length();
			}
			return totalSize;
		} else {
			return 0;
		}
	}
	
	public void archive() throws RuntimeException {
		if (rootDirectory.exists()) {
			if (rootDirectory.isDirectory()) {
				if (!newDirectory.exists()) {
					newDirectory.mkdirs();
				}
				if (newDirectory.isDirectory()) {
					println("passed all error checking for directories.");
					ArrayList<File> allFiles = getAllFilesInDirectory(rootDirectory);
					if (allFiles.size() > 0) {
						int firstYear = (int) yearLastModified(allFiles.get(0));
						int lastYear = (int) yearLastModified(allFiles.get(0));
						for (File file : allFiles) {
							int yearLastModified = (int) yearLastModified(file);
							if (yearLastModified < firstYear) {
								firstYear = yearLastModified;
							}
							if (yearLastModified > lastYear) {
								lastYear = yearLastModified;
							}
						}
						println("Creating directories for years" + firstYear + " through " + lastYear);
						for (int year = firstYear; year <= lastYear; year++) {
							File yearDir = new File(newDirectory.getPath() + "/" + year);
							println("making directory for year " + year);
							yearDir.mkdirs();
							for (int i = 1; i <= this.foldersPerYear; i++) {
								File subYearDir = new File(yearDir.getPath() + "/" + i + "of" + this.foldersPerYear);
								println("making sub-year folder " + i + " for year " + year);
								subYearDir.mkdir();
							}
						}
						if (!includeDuplicates) {
							println("removing duplicastes.");
							removeDuplicateFileNames(allFiles);
						} else {
							renameDuplicateFileNames(allFiles);
							allFiles = getAllFilesInDirectory(rootDirectory);
						}
						println("copying files");
						for (File file : allFiles) {
							String extension = FilenameUtils.getExtension(file.getName());
							println("Checking if " + file.getAbsolutePath() + " is an image");
							if (imagesOnly && !extension.equals("png") && !extension.equals("jpg")) {
								println("The file " + file.getAbsolutePath() + " is not an image and the option to only use images is selected.");
							} else {
								println("copying file " + file.getAbsolutePath());
								println("getting correct folder for " + file.getAbsolutePath());
								float year = yearLastModified(file);
								int subYear = ((int) ((year % 1) * foldersPerYear)) + 1;
								println("generating path for new file");
								File newFile = new File(newDirectory.getPath() + "/" + ((int) year) + "/" + subYear + "of" + this.foldersPerYear + "/" + file.getName());
								try {
									println("checking if " + file.getAbsolutePath() + " already exists");
									if (!newFile.exists()) {
										println("the file didnt' exist, creating file " + newFile.getAbsolutePath());
										newFile.createNewFile();
									}
									//copyFile(file, newFile);
									println("finally copying file " + file.getAbsolutePath() + " to " + newFile.getAbsolutePath());
									FileUtils.copyFile(file, newFile);
									println("done copying file " + file.getAbsolutePath());
								} catch (Exception e) {
									println("an exception occured when copying file " + file.getAbsolutePath() + " to " + newFile.getAbsolutePath());
								}
							}
						}
					}
				} else {
					println("There are no files in the directory.");
					throw new RuntimeException();
				}
			} else {
				println("The directory you selected is not a folder.");
				throw new RuntimeException();
			}
		} else {
			println("The directory you selected does not exist.");
			throw new RuntimeException();
		}
	}
	
	public static void copyFile(File source, File destination) throws IOException {
		FileChannel sourceChannel = null;
		FileChannel destinationChannel = null;
		try {
			sourceChannel = new FileInputStream(source).getChannel();
			destinationChannel = new FileInputStream(destination).getChannel();
			destinationChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
			
		} catch (java.nio.channels.NonWritableChannelException e) {
			
		}
		finally {
			if (sourceChannel != null) {
				sourceChannel.close();
			}
			if (destinationChannel != null) {
				destinationChannel.close();
			}
		}
	}
	
	public static void removeDuplicateFileNames(ArrayList<File> fileList) {
		for (int i = 0; i < fileList.size(); i++) {
			String fileName = fileList.get(i).getName();
			for (int j = i + 1; j < fileList.size(); j++) {
				if (fileList.get(j).getName().equals(fileName)) {
					fileList.remove(j);
				}
			}
		}
	}
	
	public static void renameDuplicateFileNames(ArrayList<File> fileList) {
		for (int i = 0; i < fileList.size(); i++) {
			String fileName = fileList.get(i).getName();
			int duplicates = 0;
			for (int j = i + 1; j < fileList.size(); j++) {
				if (fileList.get(j).getName().equals(fileName)) {
					duplicates++;
					String duplicateName = "(copy " + duplicates + ")" + fileName;
					fileList.get(j).renameTo(new File(fileList.get(j).getParentFile().getAbsolutePath() + "/" + duplicateName));
				}
			}
		}
	}
	
	private void println(String output) {
		if (printInfo) {
			console.println(output);
		}
	}
}
