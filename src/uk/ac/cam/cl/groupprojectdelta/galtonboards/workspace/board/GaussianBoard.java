package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board;

import org.joml.Vector2f;

import java.util.List;

public class GaussianBoard extends Board {
    /*
     * N(u,o^2) - don't know how to model the standard deviation o^2 apart from changing no. of buckets
     * */

    // Parameter u - the mean of this normal distribution
    private float meanValue;

    // How many column outputs this has - kinda corresponds to o^2
    private int numColumns;

    /**
     * Construct a board that shows the Gaussian Distribution for N(u,o^2).
     * @param u : float - The mean value for the sake of labelling buckets.
     * @param n : int - How many buckets we want to split the output into.
     */
    public GaussianBoard(float u, int n) {
        super(n-1); // isoGridWidth = n-1
        this.meanValue = u;
        this.numColumns = n;

        // Set bucket labels
        fixAllBucketTags();
    }

    /**
     * Reset all of the peg values so that they follow the Gaussian distribution.
     */
    private void fixAllPegs() {
        for (Peg peg : getPegs()) {
            peg.setProbability(0.5f);
        }
    }

    /**
     * Reset all of the bucket tags when we increase the number of buckets
     */
    private void fixAllBucketTags() {
        // TODO - This will break if bucket sizes have been edited, create check for bucket editing
        // TODO - Figure out default bucket labels for Gaussian distribution
    }

    /**
     * Update the mean value of the Guassian distribution (this updates the bucket labels).
     * @param meanValue : float - The new mean value for the distribution.
     */
    public void setMeanValue(float meanValue) {
        this.meanValue = meanValue;
        fixAllBucketTags();
    }

    /**
     * Update the number of output columns the balls can fall into (more columns = larger standard deviation).
     * @param numColumns : int - How many outputs we want for the distribution.
     */
    public void setNumColumns(int numColumns) {
        if (numColumns >= 0) {
            int rowDifference = numColumns-this.numColumns;
            if (rowDifference > 0) {
                // Keep adding rows until this.steps = steps
                for (int i = 0; i < rowDifference; i++) {
                    addRow();
                }
            }
            else if (rowDifference < 0) {
                // Keep removing rows until this.steps = steps
                for (int i = 0; i < -rowDifference; i++) {
                    removeRow();
                }
            }
        }
        else { System.err.println(numColumns + " is not a valid number of columns in the Gaussian distribution."); }
    }

    /**
     * Add a new row of pegs when editing the board. This method should be called from the UI "plus" button.
     */
    @Override
    public void addRow() {
        super.addRow();
        numColumns++;
        // Add tag to the new bucket and correct previous tag if still default
        Bucket newB = getBucket(getIsoGridWidth());
        fixAllBucketTags();
    }

    /**
     * Remove a row of pegs when editing the board. This method should be called from the UI "minus" button.
     */
    @Override
    public void removeRow() {
        super.removeRow();
        numColumns--;
        fixAllBucketTags();
    }

    /**
     * Signal that the boundaries of a bucket have been moved and get new neighbouring column positions.
     * @param leftEdge : boolean - Has the bucket's left or right edge been extended left?
     * @return The new neighbouring column positions of the edge being moved.
     */
    @Override
    public Vector2f edgeExtendedLeft(boolean leftEdge) {
        Vector2f newEdge = super.edgeExtendedLeft(leftEdge);
        // If it was the right edge, then set default tag for new bucket
        if(!leftEdge) {
            List<Bucket> buckets = getBuckets();
            Bucket b = getBeingEdited();
            int index = buckets.indexOf(b) + 1;
            Bucket newB = buckets.get(index);
            int sc = newB.getStartColumn();
            // TODO - Update label once I know what I'm doing for labels
        }
        return newEdge;
    }

    /**
     * Signal that the boundaries of a bucket have been moved and get new neighbouring column positions.
     * @param leftEdge : boolean - Has the bucket's left or right edge been extended right?
     * @return The new neighbouring column positions of the edge being moved.
     */
    @Override
    public Vector2f edgeExtendedRight(boolean leftEdge) {
        Vector2f newEdge = super.edgeExtendedRight(leftEdge);
        // If it was the left edge, then set default tag for new bucket
        if(leftEdge) {
            List<Bucket> buckets = getBuckets();
            Bucket b = getBeingEdited();
            int index = buckets.indexOf(b) - 1;
            Bucket newB = buckets.get(index);
            int sc = newB.getStartColumn();
            // TODO - Update label once I know what I'm doing for labels
        }
        return newEdge;
    }

    /**
     * Reset this board to its default given its parameters.
     */
    @Override
    public void reset() {
        super.reset();
        fixAllPegs();
        fixAllBucketTags();
    }

    /**
     * Getter for the mean value of the distribution.
     * @return The mean value.
     */
    public float getMeanValue() {
        return meanValue;
    }

    /**
     * Getter for the number of columns the output is being divided into.
     * @return The number of columns for output.
     */
    public int getNumColumns() {
        return numColumns;
    }
}
