package au.org.ala.delta.dist;

import au.org.ala.delta.model.DeltaDataSet;

/**
 * Does the work of calculating the distance matrix that is the main output
 * of the DIST program.
 */
public class DistanceMatrixCalculator {

	private DistContext _context;
	private DeltaDataSet _dataSet;
	
	public DistanceMatrixCalculator(DistContext context) {
		_context = context;
		_dataSet = _context.getDataSet();
	}
	
	
	public DistanceMatrix calculateDistanceMatrix() {
		DistanceMatrix matrix = new DistanceMatrix(_dataSet.getMaximumNumberOfItems());
		
		return matrix;
		
	}
}
