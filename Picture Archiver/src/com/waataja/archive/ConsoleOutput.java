package com.waataja.archive;

public class ConsoleOutput implements TextOutput {

	@Override
	public void println(String output) {
		System.out.println(output);
	}

	@Override
	public void println(int output) {
		System.out.println(output);
	}

	@Override
	public void println(long output) {
		System.out.println(output);
	}

	@Override
	public void println(boolean output) {
		System.out.println(output);
	}

	@Override
	public void println(float output) {
		System.out.println(output);
	}

	@Override
	public void println(double output) {
		System.out.println(output);
	}

	@Override
	public void println(Object output) {
		System.out.println(output);
	}

}
