package renderEngine;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import main.Arsia;
import utility.TypeConversions;

/**
 * TODO: Javadoc
 * 
 * @author Matthew L. Roy
 *
 */

/*
 * This class deals with loading 3D models into memory. Specifically, this class
 * stores positional data about the model in a VAO
 */

public class ModelLoader {

	// Setting up lists for memory management, so that they may be properly cleaned
	private List<Integer> vaoList = new ArrayList<Integer>();
	private List<Integer> vboList = new ArrayList<Integer>();

	/**
	 * TODO: Javadoc
	 * 
	 * @param positions
	 * @return
	 */
	public RawModel loadToVAO(float[] positions) {
		if (Arsia.DEBUG) {
			System.out.println("Creating VAO...");
		}

		int vaoId = createVAO();

		if (Arsia.DEBUG) {
			System.out.println("Created VAO with Id: " + vaoId);
		}

		// First list, so this is the VBO at VAO attribute list 0
		storeDataInAttributeList(0, positions);

		// Required per OpenGL to unbind once previously used/bound
		unbindVAO();

		// Divide length by 3 to account for (X, Y, Z) vertex positions
		return new RawModel(vaoId, positions.length / 3);
	}

	/**
	 * TODO: Javadoc
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
		// Creates an empty VAO and returns the Id of that VAO
		int vaoId = GL30.glGenVertexArrays();
		vaoList.add(vaoId);

		// Basically, "activates" this specific VAO by binding it to the OpenGL
		// pipeline, by the VAO Id from the above call
		GL30.glBindVertexArray(vaoId);

		return vaoId;
	}

	private void storeDataInAttributeList(int attributeNumber, float[] data) {
		// Create empty VBO and return the VBO Id newly created
		int vboId = GL15.glGenBuffers();
		vboList.add(vboId);

		// Bind newly created buffer to pipeline so we may manipulate it
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);

		// Store the data into the VBO, we need to make sure the data is stored as a
		// buffer, as opposed to an array, so a type conversion is needed. Also,
		// specifying usage as a static draw, so the data will not be changed nor edited
		FloatBuffer buffer = TypeConversions.floatArrayToFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);

		// This puts the VBO into one of the VAO's attribute lists (VAO index, length of
		// each vertex (x,y,z), type of data, is data normalized?, distance between each
		// vertex, offset)
		GL20.glVertexAttribPointer(attributeNumber, 3, GL11.GL_FLOAT, false, 0, 0);

		// Unbind similarly to VAO, unbinds with 0 passed in
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	private void unbindVAO() {
		GL30.glBindVertexArray(0); // Passing zero to this method (see above in createVAO()) unbinds the VAO
	}
}
