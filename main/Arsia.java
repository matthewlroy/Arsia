package main;

import renderEngine.RenderWindow;

public class Arsia {

	private static final int WINDOW_WIDTH = 1280, WINDOW_HEIGHT = 720;

	private static final String WINDOW_NAME = "Arsia";

	public static void main(String[] args) {
		System.out.println("Creating & initializing display window...");
		RenderWindow dw = new RenderWindow(WINDOW_WIDTH, WINDOW_HEIGHT, WINDOW_NAME);

		System.out.println("Updating display window...");
		while (!dw.isClosed()) {
			dw.update();
		}

		System.out.println("Terminating display window...");
		dw.terminate();
	}
}
