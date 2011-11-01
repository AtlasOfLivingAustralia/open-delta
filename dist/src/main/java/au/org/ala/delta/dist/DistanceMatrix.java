package au.org.ala.delta.dist;

public class DistanceMatrix {

	private double[][] _matrix;
	
	public DistanceMatrix(int numItems) {
		_matrix = new double[numItems][numItems];
	}
	
	public void set(int item1, int item2, double value) {
		_matrix[item1-1][item2-1] = value;
	}
	
	public double get(int item1, int item2) {
		return _matrix[item1-1][item2-1];
	}
}
