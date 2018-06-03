package main;

import static java.lang.System.nanoTime;

import org.lwjgl.opengl.GL;

import renderEngine.ModelLoader;
import renderEngine.RawModel;
import renderEngine.RenderInputWindow;

/**
 * This is the main entry point class for the entire Arsia project. This class
 * contains the timing system, queries to the render window, and all of the game
 * logic. The main game loop follows the process of:
 * 
 * <ol>
 * <li>Query the state of the GLFW window to see if it is still open</li>
 * <li>If so, compute the current time and deltas to time-step the game logic:
 * <ol type="a">
 * <li>At the beginning of the loop, grab the current time in nanoseconds</li>
 * <li>Subtract this value from the time of the end of the previous loop</li>
 * <li>Divide that newly subtracted value by 1E10^9 (or nanoseconds)</li></li>
 * <li>The tick system (detailed below) is determined by dividing that value
 * again by the number of ticks desired per second and incrementing a delta</li>
 * </ol>
 * </li>
 * <li>Handle input and events from the user</li>
 * <li>Tick and update game logic (20 ticks per second or 1t/0.05s)</li>
 * <li>Render using steps from: {@link renderEngine.RenderInputWindow}</li>
 * <li>Repeat steps 1-5 until the GLFW has returned a window closed query</li>
 * <li>Perform cleanup of any resources used and terminate the game</li>
 * </ol>
 * 
 * @author Matthew L. Roy
 *
 */
public class Arsia {

	/**
	 * Prints debug and trace statements to standard output when true
	 */
	public static final boolean ARSIA_DEBUG = true;

	private final int WINDOW_WIDTH = 1280, WINDOW_HEIGHT = 720;
	private int currentTick = -1;

	private final String WINDOW_NAME = "Arsia";

	private long previousTimeNs = 0, currentTimeNs = 0;

	private final double TICKS_PER_SECOND = 20D;
	private double nsPerIteration = 1000000000D / TICKS_PER_SECOND, delta = 0;

	private boolean startGame = false;

	/**
	 * The main entry point method for the entire Arsia project. Runs the Arsia
	 * start game method, and attempts to catch any exception and print the stack
	 * trace on errors. TODO: Properly handle exceptions.
	 * 
	 * @param args
	 *            command line arguments specified for JVM execution
	 */
	public static void main(String[] args) {
		try {
			new Arsia().run();
		} catch (Exception e) {
			System.err.println("Game has crashed! :(");
			e.printStackTrace();
		}
	}

	private void run() {
		if (ARSIA_DEBUG) {
			System.out.println("Creating and initializing display window...");
		}

		RenderInputWindow riw = new RenderInputWindow(WINDOW_WIDTH, WINDOW_HEIGHT, WINDOW_NAME);
		riw.init();

		if (ARSIA_DEBUG) {
			System.out.println("Creating a quad via the ModelLoader...");
		}

		// FIXME: Naming conventions & placement of these lines of code
		GL.createCapabilities(); // FIXME: Correct place for this?
		ModelLoader loader = new ModelLoader();
		float[] vertices = { -0.5f, 0.5f, 0f, -0.5f, -0.5f, 0f, 0.5f, -0.5f, 0f, 0.5f, -0.5f, 0f, 0.5f, 0.5f, 0f, -0.5f,
				0.5f, 0f };
		RawModel model = loader.loadToVAO(vertices);

		if (ARSIA_DEBUG) {
			System.out.println("Updating display window...");
		}

		/* Begin main game loop, each step is detailed in class comments above */
		while (!riw.isClosed()) {

			/* 1. Compute the current time and deltas */
			if (!startGame) {
				startGame = true;
				previousTimeNs = nanoTime();
			}

			currentTimeNs = nanoTime();
			delta += (currentTimeNs - previousTimeNs) / nsPerIteration;
			previousTimeNs = currentTimeNs;

			/* 2. Handle input and events from the user */
			riw.handleInput();

			/* 3. Tick and update game logic (20 ticks per second or 1t/0.05s) */
			if (delta >= 1) {
				tick();
				delta--;
			}

			/* 4. Render using steps from: {@link renderEngine.RenderInputWindow} */
			riw.prepare();
			riw.render(model);
		}

		if (ARSIA_DEBUG) {
			System.out.println("Performing clean up of resources...");
		}

		loader.cleanUp();
		riw.cleanUp();
	}

	private void tick() {
		currentTick++;

		if (ARSIA_DEBUG) {
			System.out.println(currentTick / 20 + " sec:\tcurrentTick: " + currentTick);
		}
	}
}
