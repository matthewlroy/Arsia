package renderEngine;

import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import java.nio.IntBuffer;

import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import main.Arsia;

/**
 * This class functions as the main display for Arsia. It generates a render
 * window that also handles user input. This is achieved by using the underlying
 * GLFW library in tandem with the OpenGL pipeline. To properly use this class,
 * follow the general order of operations:
 * 
 * <ol>
 * <li>Construct the display, specifying width, height, and window title
 * name</li>
 * <li>Make a call to the initializing method</li>
 * <li>Query the state of the window for the main game loop</li>
 * <li>In the main game loop, while the state of the window is open:
 * <ol type="a">
 * <li>Handle user input</li>
 * <li>Make a call to prepare the rendering engine</li>
 * <li>Finally, render the specified RawModel(s)</li>
 * </ol>
 * </li>
 * <li>Clean up resources once the window is closed</li>
 * </ol>
 * 
 * @author Matthew L. Roy
 * 
 */
public class RenderInputWindow {

	private int windowWidth = 1280, windowHeight = 720, frameCounter = -1;

	private String windowName = "Window";

	private long window;

	/**
	 * This constructor just sets up the passed variables for the initializing
	 * method. Make a call immediately to {@link #init()} once the constructor has
	 * been called.
	 * 
	 * @param windowWidth
	 *            the width, in pixels, of this display window
	 * @param windowHeight
	 *            the height, in pixels, of this display window
	 * @param windowName
	 *            the name in the title bar of this display window
	 */
	public RenderInputWindow(int windowWidth, int windowHeight, String windowName) {
		this.windowWidth = windowWidth;
		this.windowHeight = windowHeight;
		this.windowName = windowName;
	}

	/**
	 * This method initializes the backend hooks for the GLFW display, as well as
	 * sets up the input handler (by using callback methods). This method should be
	 * called immediately after {@link #RenderInputWindow(int, int, String)}
	 * construction.
	 */
	public void init() {
		/* The default implementation will print the error message in System.err */
		GLFWErrorCallback.createPrint(System.err).set();

		/* Needs to be called for any of the following GLFW methods to work */
		if (!GLFW.glfwInit()) {
			throw new IllegalStateException("Unable to initialize GLFW");
		}

		/* Optional as the current window hints are already the default */
		GLFW.glfwDefaultWindowHints();

		/*
		 * Window is later visible via glfwShowWindow(). This allows the loading of the
		 * window to complete before any rendering is shown and to avoid artifacts
		 */
		GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);

		/* Let the window be focused for any following user input, but not resizable */
		GLFW.glfwWindowHint(GLFW.GLFW_FOCUSED, GLFW.GLFW_TRUE);
		GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GLFW.GLFW_FALSE);

		window = GLFW.glfwCreateWindow(windowWidth, windowHeight, windowName, MemoryUtil.NULL, MemoryUtil.NULL);
		if (window == MemoryUtil.NULL) {
			throw new RuntimeException("Failed to create the GLFW window");
		}

		setupInput();

		/*
		 * Get the thread stack and push a new frame. The stack frame is popped
		 * automatically via built-in Java
		 */
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1); // Analogous to int*
			IntBuffer pHeight = stack.mallocInt(1); // Analogous to int*

			/* Gets the resolution of the primary monitor */
			GLFW.glfwGetWindowSize(window, pWidth, pHeight);
			GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());

			/* Center the window */
			GLFW.glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2,
					(vidmode.height() - pHeight.get(0)) / 2);
		}

		GLFW.glfwMakeContextCurrent(window);
		GLFW.glfwSwapInterval(1); // Enables v-sync
		GLFW.glfwShowWindow(window);
	}

	/**
	 * Performs a low-level call to the GLFW pipeline to handle events.
	 * Specifically, input is handled in this call.
	 */
	public void handleInput() {
		/* Will poll ALL events, but currently just INPUT */
		GLFW.glfwPollEvents();
	}

	/**
	 * This method prepares the GLFW API for rendering. This method is critical for
	 * LWJGL's interpretation with GLFW's OpenGL context, or any context that is
	 * managed externally. LWJGL detects the context that is current in the current
	 * thread, creates the GLCapabilities instance and makes the OpenGL bindings
	 * available for use. Also sets the clear color to the desired RGB
	 * configuration. Make a call to {@link #render(RawModel)} after calling
	 * prepare.
	 */
	public void prepare() {
		frameCounter++;

		if (Arsia.DEBUG) {
			System.out.println("Frame: " + frameCounter);
		}

		createCapabilities(); // CRITICAL
		glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
		glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

	/**
	 * Must be called immediately after {@link #prepare()}. This method renders the
	 * model to the display with the correct position. Uses the model's Vertex Array
	 * Object (VAO) and then uses the Vertex Buffer Object (VBO) at VAO attribute
	 * position 0, which coincides with vertex positions.
	 * 
	 * @param model
	 *            the specified model to be rendered to the display
	 */
	public void render(RawModel model) {
		/*
		 * VAO needs to be bound, per its Id, prior to rendering. Also, VBO at VAO
		 * attribute 0 is positions, per convention of these classes.
		 */
		glBindVertexArray(model.getVaoId());
		glEnableVertexAttribArray(0);
		glDrawArrays(GL_TRIANGLES, 0, model.getVertexCount());
		glDisableVertexAttribArray(0);
		glBindVertexArray(0);
		glfwSwapBuffers(window); // CRITICAL
	}

	/**
	 * Frees up resources as it relates to the GLFW callback methods and display
	 * window, while clearing the error stream for GLFW. Call this method when
	 * {@link #isClosed()} returns true.
	 */
	public void cleanUp() {
		Callbacks.glfwFreeCallbacks(window);
		GLFW.glfwDestroyWindow(window);
		GLFW.glfwTerminate();
		GLFW.glfwSetErrorCallback(null).free();
	}

	/**
	 * Returns the state of the GLFW window, specifically if it is closed or open.
	 * Make sure to call {@link #cleanUp()} after this method returns true,
	 * indicating that the display has been closed.
	 * 
	 * @return the current state of the GLFW window, whether it is closed or not
	 */
	public boolean isClosed() {
		return GLFW.glfwWindowShouldClose(window);
	}

	private void setupInput() {
		/* This callback is detected in the rendering loop */
		GLFW.glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_RELEASE)
				GLFW.glfwSetWindowShouldClose(window, true);
		});
	}
}
