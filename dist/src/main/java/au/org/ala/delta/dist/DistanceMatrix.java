package au.org.ala.delta.dist;

public class DistanceMatrix {

	private double[][] _matrix;
	
	public DistanceMatrix(int numItems) {
		_matrix = new double[numItems-1][numItems-1];
	}
	
	public void set(int item1, int item2, double value) {
		_matrix[item1][item2] = value;
	}
	
	public double get(int item1, int item2) {
		return _matrix[item1][item2];
	}
}
