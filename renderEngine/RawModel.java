package renderEngine;

/* 
 * This class represents a 3D model stored in memory, as determined by it's Vertex Array Object (VAO) Id
 * VAO Example:
 * [0 | vertex positions]
 * [1 | vertex colors]
 * [2 | normal vectors]
 * [3 | texture coordinates]
 * [4 | ....]
 * [15| ....]
 * Every data set in the above indices of the attribute list is a Vertex Buffer Object (VBO) 
 * Every VAO is given an Id, and generally represents the 3D object as a set of triangles
 */
public class RawModel {

	private int vaoId, vertexCount;

	public RawModel(int vaoId, int vertexCount) {
		this.vaoId = vaoId;
		this.vertexCount = vertexCount;
	}

	public int getVaoId() {
		return vaoId;
	}

	public int getVertexCount() {
		return vertexCount;
	}
}
