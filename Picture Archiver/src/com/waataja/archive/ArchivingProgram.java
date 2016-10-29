package com.waataja.archive;

public class ArchivingProgram {
	
	public static void main(String[] args) {
		new ArchivingProgram().start();
	}
	
	public void start() {
		PictureArchiver archiver = new PictureArchiver();
		ArchiveGUI gui = new ArchiveGUI(archiver);
		archiver.setConsole(gui);
	}
}
