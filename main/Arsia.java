package main;

import static java.lang.System.nanoTime;

import renderEngine.RenderWindow;

public class Arsia {

	public static final boolean DEBUG = true;

	private final int WINDOW_WIDTH = 1280, WINDOW_HEIGHT = 720;
	private int currentTick = -1;

	private final String WINDOW_NAME = "Arsia";

	private long previousTimeNs = 0, currentTimeNs = 0;

	private final double ticksPerSecond = 20D;
	private double nsPerIteration = 1000000000D / ticksPerSecond, delta = 0;

	private boolean startGame = false;

	private void run() {
		if (Arsia.DEBUG) {
			System.out.println("Creating & initializing display window...");
		}

		RenderWindow rw = new RenderWindow(WINDOW_WIDTH, WINDOW_HEIGHT, WINDOW_NAME);

		if (Arsia.DEBUG) {
			System.out.println("Updating display window...");
		}

		while (!rw.isClosed()) {
			if (!startGame) {
				startGame = true;
				previousTimeNs = nanoTime();
			}

			currentTimeNs = nanoTime();
			delta += (currentTimeNs - previousTimeNs) / nsPerIteration;
			previousTimeNs = currentTimeNs;
			if (delta >= 1) {
				tick();
				delta--;
			}

			rw.update();
		}

		if (Arsia.DEBUG) {
			System.out.println("Terminating display window...");
		}

		rw.terminate();
	}

	private void tick() {
		currentTick++;

		if (Arsia.DEBUG) {
			System.out.println(currentTick / 20 + " sec:\tcurrentTick: " + currentTick);
		}
	}

	public static void main(String[] args) {
		new Arsia().run();
	}
}
