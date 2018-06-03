package renderEngine;

import static main.Arsia.ARSIA_DEBUG;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import utility.TypeConversions;

/**
 * This class deals with loading 3D models into memory. This class uses native
 * calls to OpenGL to create a VAO, initialize it with an Id to be referenced,
 * and then add different types of VBOs and attributes.
 * 
 * @author Matthew L. Roy
 * @see {@link renderEngine.RawModel}
 *
 */
public class ModelLoader {

	/* Setting up lists for memory management, to be properly disposed */
	private List<Integer> vaoList = new ArrayList<Integer>(), vboList = new ArrayList<Integer>();

	/**
	 * Uses native calls to OpenGL to create a VAO, initialize it with an Id to be
	 * referenced, and then adds the different types of VBOs and attributes. The Id
	 * and number of vertexes are then passed to the {@link renderEngine.RawModel}
	 * constructor.
	 * 
	 * @param positions
	 *            the vertices, (X, Y, Z) coordinates, that this model will be
	 *            rendered at on the display
	 * @return the model object containing the Vao Id and vertex count
	 * @see {@link renderEngine.RawModel#RawModel(int, int)}
	 */
	public RawModel loadToVAO(float[] positions) {
		RenderInputWindow.linkOpenGLContext(); // CRITICAL

		/* Divide length by 3 to account for (X, Y, Z) vertex positions */
		int vertexCount = positions.length / 3;
		RawModel model = null;

		if (ARSIA_DEBUG) {
			System.out.println("Creating VAO...");
		}

		int vaoId = createVAO();

		if (ARSIA_DEBUG) {
			System.out.println("Created VAO with Id: " + vaoId);
		}

		/* First list will start at VAO attribute list 0 */
		storeDataInAttributeList(0, positions);

		/* Required per OpenGL to unbind once previously bound */
		unbindVAO();

		model = new RawModel(vaoId, vertexCount);
		return model;
	}

	/**
	 * Frees up resources as it relates to the VAO and VBO lists. Call this method
	 * when {@link renderEngine.RenderInputWindow#isClosed()} returns true.
	 */
	public void cleanUp() {
		for (int vaoId : vaoList) {
			GL30.glDeleteVertexArrays(vaoId);
		}

		for (int vboId : vboList) {
			GL15.glDeleteBuffers(vboId);
		}
	}

	private int createVAO() {
		/* Creates an empty VAO and returns the Id of that VAO */
		int vaoId = GL30.glGenVertexArrays();
		vaoList.add(vaoId);

		/*
		 * Binding means to "activate" this specific VAO by binding it to the OpenGL
		 * pipeline, using the VAO Id from the above call. This then allows the VAO to
		 * be manipulated further (like editing VBOs and adding attribute lists)
		 */
		GL30.glBindVertexArray(vaoId);

		return vaoId;
	}

	private void storeDataInAttributeList(int attributeNumber, float[] data) {
		/* Creates an empty VBO and returns the Id of that VBO */
		int vboId = GL15.glGenBuffers();
		vboList.add(vboId);

		/* Bind newly created buffer to pipeline so we may manipulate it */
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);

		/*
		 * Store the data into the VBO, we need to make sure the data is stored as a
		 * buffer, as opposed to an array, so a type conversion is needed. Also,
		 * specifying usage as a static draw, so the data will not be changed nor edited
		 */
		FloatBuffer buffer = TypeConversions.floatArrayToFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);

		/*
		 * This puts the VBO into one of the VAO's attribute lists: (VAO index, length
		 * of each vertex (X, Y, Z), type of data, is data normalized?, distance between
		 * each vertex, offset)
		 */
		GL20.glVertexAttribPointer(attributeNumber, 3, GL11.GL_FLOAT, false, 0, 0);

		/* Unbinds similar to VAO (unbinds with 0 passed in) */
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	private void unbindVAO() {
		/* Passing zero to this method unbinds the VAO, per OpenGL */
		GL30.glBindVertexArray(0);
	}
}
