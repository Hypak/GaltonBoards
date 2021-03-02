package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board;

import org.joml.Vector2f;
import org.joml.Vector2i;

import java.util.List;

public class GeometricBoard extends Board {
    /*
    * Geo(p)
    * P(X=k) = (1-p)^(k-1) * p
    * */

    // Parameter p - what's the probability of a trial succeeding
    private float probPerTrial;

    // How many unique k's (buckets) there are before the final X>k bucket
    private int numUniqueTrials;

    /**
     * Construct a board that shows the Geometric Distribution for Geo(p).
     * @param p : float - The probability of a trial succeeding.
     * @param k : int - How many unique X=k's we can test for with buckets.
     */
    public GeometricBoard(float p, int k) {
        super(k); // isoGridWidth = k
        this.probPerTrial = p;
        this.numUniqueTrials = k;

        // Set peg probability values to create geometric distribution
        fixAllPegs();

        // Set bucket labels (k=1, k=2, ..., k>numUniqueTrials)
        fixAllBucketTags();
    }

    /**
     * Reset all of the peg values so that they follow the Geometric distribution.
     */
    private void fixAllPegs() {
        for (Peg peg : getPegs()) {
            Vector2i gp = peg.getGridPos();
            if(gp.x == gp.y) { peg.setProbability(probPerTrial); }
            else { peg.setProbability(1f); }
        }
    }

    /**
     * Reset all of the bucket tags when we increase the number of buckets
     */
    private void fixAllBucketTags() {
        // TODO - This will break if bucket sizes have been edited, create check for bucket editing
        int i = 1;
        for (Bucket b : getBuckets()) {
            if (i==numUniqueTrials+1) { b.setTag("k>" + numUniqueTrials); }
            else { b.setTag("k=" + i); }
            i++;
        }
    }

    /**
     * Update the probability of trials succeeding.
     * @param p : float - The probability of a given trial succeeding.
     */
    public void setProbPerTrial(float p) {
        if (p >= 0 && p <= 1) {
            probPerTrial = p;
            fixAllPegs();
        }
        else { System.err.println(p + " is not a valid probability for the Geometric distribution.");}
    }

    /**
     * Update the number of unique number of outputs.
     * @param k : int - How many trials outcomes should be shown (excluding the final greater than bucket).
     */
    public void setNumUniqueTrials(int k) {
        if (k >= 0) {
            int rowDifference = k-numUniqueTrials;
            if (rowDifference > 0) {
                // Keep adding rows until numUniqueTrials = n
                for (int i = 0; i < rowDifference; i++) {
                    addRow();
                }
            }
            else if (rowDifference < 0) {
                // Keep removing rows until numTrials = n
                for (int i = 0; i < -rowDifference; i++) {
                    removeRow();
                }
            }
        }
        else { System.err.println(k + " is not a valid number of trials for the Binomial distribution."); }
    }

    /**
     * Add a new row of pegs when editing the board. This method should be called from the UI "plus" button.
     */
    @Override
    public void addRow() {
        super.addRow();
        numUniqueTrials++;
        // Correct the probability values of the new pegs
        List<Peg> pegs = getPegs();
        for (int i = pegs.size() - getIsoGridWidth(); i < pegs.size(); i++) {
            if (i == pegs.size() - 1) { pegs.get(i).setProbability(probPerTrial); }
            else { pegs.get(i).setProbability(1f); }
        }
        // Add tag to the new bucket and correct previous tag if still default
        Bucket newB = getBucket(getIsoGridWidth());
        newB.setTag("k>" + numUniqueTrials);
        Bucket prevB = getBucket(getIsoGridWidth() - 1);
        if (prevB.getTag() == ("k>" + (numUniqueTrials-1))) {prevB.setTag("k=" + (numUniqueTrials)); }
    }

    /**
     * Remove a row of pegs when editing the board. This method should be called from the UI "minus" button.
     */
    @Override
    public void removeRow() {
        super.removeRow();
        numUniqueTrials--;
        // Change the tag of the last bucket if it is still default
        Bucket last = getBucket(getIsoGridWidth());
        if (last.getTag() == ("k=" + (numUniqueTrials+1))) {last.setTag("k>" + (numUniqueTrials)); }
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
            if(sc == numUniqueTrials) { newB.setTag("k>" + (newB.getStartColumn())); }
            else { newB.setTag("k=" + (newB.getStartColumn()+1)); }
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
     * Getter for the probability per trial.
     * @return The probability of a trial succeeding.
     */
    public float getProbPerTrial() {
        return probPerTrial;
    }

    /**
     * Getter for the number of trials until success we can test for.
     * @return The number of trials until success we can test for.
     */
    public int getNumUniqueTrials() {
        return numUniqueTrials;
    }

    @Override
    public String toString() {
        return "Geometric board of width " + super.isoGridWidth;
    }
}
