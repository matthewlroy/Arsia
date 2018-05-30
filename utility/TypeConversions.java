package utility;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

/**
 * TODO: Javadoc
 * 
 * @author Matthew L. Roy
 *
 */
public class TypeConversions {
	/**
	 * TODO: Javadoc
	 * 
	 * @param data
	 * @return
	 */
	public static FloatBuffer floatArrayToFloatBuffer(float[] data) {
		// Create empty float buffer, letting buffer know length of data
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);

		// Copy data over to the buffer, as buffer is in a write-to state
		buffer.put(data);

		// Tells the buffer that we are finished writing to it, now in a read-from state
		buffer.flip();

		return buffer;
	}
}
