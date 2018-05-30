package main;

import static java.lang.System.nanoTime;

import org.lwjgl.opengl.GL;

import renderEngine.ModelLoader;
import renderEngine.RawModel;
import renderEngine.RenderInputWindow;

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

		RenderInputWindow riw = new RenderInputWindow(WINDOW_WIDTH, WINDOW_HEIGHT, WINDOW_NAME);
		riw.init();

		if (Arsia.DEBUG) {
			System.out.println("Updating display window...");
		}

		GL.createCapabilities();
		ModelLoader loader = new ModelLoader();
		float[] vertices = { -0.5f, 0.5f, 0f, -0.5f, -0.5f, 0f, 0.5f, -0.5f, 0f, 0.5f, -0.5f, 0f, 0.5f, 0.5f, 0f, -0.5f,
				0.5f, 0f };
		RawModel model = loader.loadToVAO(vertices);

		// Main game loop will follow this process:
		// 1. Compute the current time and deltas
		// 2. Handle input
		// 3. Tick (update) game logic
		// 4. Prepare and render via OpenGL pipeline
		while (!riw.isClosed()) {
			// 1. Compute the current time and deltas
			if (!startGame) {
				startGame = true;
				previousTimeNs = nanoTime();
			}
			currentTimeNs = nanoTime();
			delta += (currentTimeNs - previousTimeNs) / nsPerIteration;
			previousTimeNs = currentTimeNs;

			// 2. Handle input
			riw.handleInput();

			// 3. Tick (update) game logic
			if (delta >= 1) {
				tick();
				delta--;
			}

			// 4. Prepare and render via OpenGL pipeline
			riw.prepare();
			riw.render(model);
		}

		if (Arsia.DEBUG) {
			System.out.println("Terminating display window...");
		}

		loader.cleanUp();
		riw.terminate();
	}

	private void tick() {
		currentTick++;

		if (Arsia.DEBUG) {
			System.out.println(currentTick / 20 + " sec:\tcurrentTick: " + currentTick);
		}
	}

	public static void main(String[] args) {
		try {
			new Arsia().run();
		} catch (Exception e) {
			System.err.println("Game has crashed! :(");
			e.printStackTrace();
		}
	}
}
