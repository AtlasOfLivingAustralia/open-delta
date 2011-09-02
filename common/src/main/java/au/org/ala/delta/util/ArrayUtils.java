package au.org.ala.delta.util;

import java.util.List;

/**
 * Utility methods for working with Arrays.
 */
public class ArrayUtils {

	/**
	 * Returns a new byte array containing a copy of the data from the source array
	 * with the data between <code>start</code> and <code>end</code> removed.
	 * @param source the array to remove data from.  May not be null.
	 * @param start the index of the first data element to remove.
	 * @param end the index of the last data element to remove.
	 * @return a new byte array containing the source data with the identified range removed.
	 */
	public static byte[] deleteRange(byte[] source, int start, int end) {
		byte[] newData = new byte[source.length - (end - start)];
		System.arraycopy(source, 0, newData, 0, start);
		System.arraycopy(source, end, newData, start, source.length - end);
		
		return newData;
	}
	
	
	public static byte[] insert(byte[] source, int where, byte[] toInsert) {
		
		
		byte[] newData = new byte[source.length + toInsert.length];
		System.arraycopy(source, 0, newData, 0, where);
		System.arraycopy(toInsert, 0, newData, where, toInsert.length);
		System.arraycopy(source, where, newData, where+toInsert.length, source.length-where);
		
		return newData;

	}
	
	public static <T> void resize(List<T> list, int newSize, T newValue) {
		if (list.size() <= newSize) {
			for (int i=0; i<newSize-list.size(); i++) {
				list.add(newValue);
			}
		}
		else {
			for (int i=list.size()-1; i>newSize; i--) {
				list.remove(i);
			}
		}
	}
	
	/**
	 * Does a case insensitive comparison of each of the elements of the
	 * supplied arrays to determine if they are equal.
	 * @param array1 the first array to compare.  May not be null.
	 * @param array2 the second array to compare. May not be null.
	 * @return true if the arrays are the same length and 
	 * String.equalsIgnoreCase returns true for each of the elements.
	 */
	public static boolean equalsIgnoreCase(String[] array1, String[] array2) {
		if (array1 == null || array2 == null) {
			throw new IllegalArgumentException("Null arrays not supported");
		}
		if (array1.length != array2.length) {
			return false;
		}
		for (int i=0; i<array1.length; i++) {
			if (!array1[i].equalsIgnoreCase(array2[i])) {
				return false;
			}
		}
		return true;
	}

}
