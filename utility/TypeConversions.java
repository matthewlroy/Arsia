package utility;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

/**
 * Deals with conversions of non-primitive data types such as arrays to buffers.
 * 
 * @author Matthew L. Roy
 *
 */
public class TypeConversions {
	/**
	 * Takes in a float array and returns the respective float buffer.
	 * 
	 * @param data
	 *            the data to be transferred into the buffer
	 * @return the newly created buffer from the passed in data
	 */
	public static FloatBuffer floatArrayToFloatBuffer(float[] data) {

		/* Create empty float buffer, letting buffer know length of data */
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);

		/* Copy data over to the buffer, as buffer is in a write-to state */
		buffer.put(data);

		/* Tells buffer that we are finished writing to it, now in a read-from state */
		buffer.flip();

		return buffer;
	}

	/**
	 * Takes in an int array and returns the respective int buffer.
	 * 
	 * @param data
	 *            the data to be transferred into the buffer
	 * @return the newly created buffer from the passed in data
	 */
	public static IntBuffer intArrayToIntBuffer(int[] data) {
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
}
