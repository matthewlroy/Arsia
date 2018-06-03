package renderEngine;

/**
 * This class represents a 3D model stored in memory, as determined by it's
 * Vertex Array Object (VAO). Every dataset stored in the indices of the
 * attribute list is a Vertex Buffer Object (VBO). Every VAO is given an Id, and
 * generally represents the 3D object as a set of triangles. Per conventions of
 * Arsia and these classes, these are functions for the VBOs at their VAO
 * attribute lists indices:
 * 
 * <ul>
 * <li>[0 | vertex positions]</li>
 * <li>[1 | vertex colors]</li>
 * <li>[2 | normal vectors]</li>
 * <li>[3 | texture coordinates]</li>
 * <li>[4 | ....]</li>
 * <li>[15| ....]</li>
 * </ul>
 * 
 * @author Matthew L. Roy
 * @see {@link renderEngine.ModelLoader#loadToVAO(float[])}
 *
 */
public class RawModel {

	private int vaoId = 0, vertexCount = 0;

	/**
	 * Construct a logical representation of a 3D model in memory; this constructor
	 * merely acts a pointer to important fields of that model.
	 * 
	 * @param vaoId
	 *            the Vertex Array Object ID that was given by the OpenGL pipeline
	 * @param vertexCount
	 *            the amount of vertexes for this model. For example, (X, Y, Z)
	 *            coordinates are 3 different float values, but will only represent
	 *            1 vertex
	 */
	public RawModel(int vaoId, int vertexCount) {
		this.vaoId = vaoId;
		this.vertexCount = vertexCount;
	}

	/**
	 * Returns the integer Id value for this model's VAO, as determined during the
	 * creation of the model by the OpenGL pipeline.
	 * 
	 * @return the Vertex Array Object ID that was given by the OpenGL pipeline
	 */
	public int getVaoId() {
		return vaoId;
	}

	/**
	 * Returns the count of vertexes to be used by the OpenGL render methods.
	 * 
	 * @return the amount of vertexes to be rendered from this VAO
	 * @see {@link renderEngine.RenderInputWindow#render(RawModel)}
	 */
	public int getVertexCount() {
		return vertexCount;
	}
}
