package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board;

import org.joml.Vector2f;
import org.joml.Vector2i;

import java.util.List;

public class UniformBoard extends Board{
    /*
     * U(a,b)
     * P(X=k) = 1 / (b-a)
     * */

    // Parameter a - the lower bound of the uniform distribution
    private float lowerBound;

    // Parameter b - the lower bound of the uniform distribution
    private float upperBound;

    // The number of buckets that the ouput is divided into
    private int steps;

    /**
     * Construct a 10 levels board that shows the Uniform Distribution for U(0,1).
     */
    public UniformBoard() { this(0, 1, 10); }

    /**
     * Construct a board that shows the Uniform Distribution for U(a,b).
     * @param a : float - The lower bound of the distribution.
     * @param b : float - The upper bound of the distribution.
     * @param s : int - How many buckets this board has.
     */
    public UniformBoard(float a, float b, int s) {
        super(s-1); // isoGridWidth = s-1
        this.lowerBound = a;
        this.upperBound = b;
        this.steps = s;

        // Set peg probability values to create uniform distribution
        fixAllPegs();

        // Set bucket labels (a<=k<a_1, ..., a_n-1<=k<b)
        fixAllBucketTags();
    }

    /**
     * Reset all of the peg values so that they follow the uniform distribution.
     */
    private void fixAllPegs() {
        float denominator = steps;
        for (Peg peg : getPegs()) {
            Vector2i gp = peg.getGridPos();
            if(gp.y == 0) {
                peg.setProbability(1f-(1f/denominator));
                denominator--;
            }
            else { peg.setProbability(0f); }
        }
    }

    /**
     * Check whether the user has edited any of the buckets.
     * @return Whether the bucket layout is default or not.
     */
    private boolean bucketsEdited() {
        boolean edited = false;
        for (Bucket b : getBuckets()) {
            if (b.getWidth() > 1) {
                edited = true;
            }
        }
        return edited;
    }

    /**
     * Reset all of the bucket tags when we increase the number of buckets
     */
    private void fixAllBucketTags() {
        if (bucketsEdited()) { return; }
        int i = 0;
        float stepInc = (upperBound-lowerBound) / steps;
        for (Bucket bucket : getBuckets()) {
            bucket.setTag((lowerBound+i*stepInc) + "<=k<" + (lowerBound+(i+1)*stepInc));
            i++;
        }
    }

    /**
     * Update the lower bound of the distribution.
     * @param lowerBound : float - The lower bound of the uniform distribution.
     */
    public void setLowerBound(float lowerBound) {
        this.lowerBound = lowerBound;
        fixAllBucketTags();
    }

    /**
     * Update the upper bound of the distribution.
     * @param upperBound : float - The upper bound of the uniform distribution.
     */
    public void setUpperBound(float upperBound) {
        this.upperBound = upperBound;
        fixAllBucketTags();
    }

    /**
     * Update the number of steps between the lower and upper bounds.
     * @param steps : int - How many outputs we want for the distribution.
     */
    public void setSteps(int steps) {
        if (steps >= 0) {
            int rowDifference = steps-this.steps;
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
        else { System.err.println(steps + " is not a valid number of steps in the Uniform distribution."); }
    }

    /**
     * Add a new row of pegs when editing the board. This method should be called from the UI "plus" button.
     */
    @Override
    public void addRow() {
        super.addRow();
        steps++;
        // Correct the probability values of the pegs
        fixAllPegs();
        // Add tag to the new bucket
        fixAllBucketTags();
    }

    /**
     * Remove a row of pegs when editing the board. This method should be called from the UI "minus" button.
     */
    @Override
    public void removeRow() {
        super.removeRow();
        steps--;
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
            float stepInc = (upperBound-lowerBound) / steps;
            newB.setTag((lowerBound+sc*stepInc) + "<=k<" + (lowerBound+(sc+1)*stepInc));
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
            newB.setTag("k=" + (newB.getStartColumn()+1));
            float stepInc = (upperBound-lowerBound) / steps;
            newB.setTag((lowerBound+sc*stepInc) + "<=k<" + (lowerBound+(sc+1)*stepInc));
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
     * Getter for the lower bound of the distribution.
     * @return The lower bound.
     */
    public float getLowerBound() {
        return lowerBound;
    }

    /**
     * Getter for the upper bound of the distribution.
     * @return The upper bound.
     */
    public float getUpperBound() {
        return lowerBound;
    }

    /**
     * Getter for the steps (number of buckets) in the distribution.
     * @return The number of steps.
     */
    public int getSteps() {
        return steps;
    }

    @Override
    public String toString() {
        return "Uniform board of width " + super.isoGridWidth;
    }
}
