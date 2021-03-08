package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board;

import org.joml.Vector2f;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.Simulation;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.Workspace;

import java.util.List;

public class BinomialBoard extends Board {
    /*
     * B(n,p)
     * P(X=k) = (nCk)p^k(1-p)^(n-k)
     * */

    // Parameter p - what's the probability of a trial succeeding
    private float probPerTrial;

    // How many trials are taken
    private int numTrials;

    /**
     * Construct a 10 levels board that shows the Binomial Distribution for B(10,0.3).
     */
    public BinomialBoard() { this(10, 0.3f); }

    /**
     * Construct a board that shows the Binomial Distribution for B(n,p).
     * @param n : int - How many trials we want to perform (isoGridWidth).
     * @param p : float - Probability of each trial succeeding.
     */
    public BinomialBoard(int n, float p) {
        super(n); // isoGridWidth = n
        this.probPerTrial = p;
        this.numTrials = n;

        // Set peg probability values to create binomial distribution
        fixAllPegs();

        // Set bucket labels (n=0, n=1, ..., n=numTrials)
        fixAllBucketTags();
    }

    /**
     * Reset all of the peg values so that they follow the binomial distribution.
     */
    private void fixAllPegs() {
        for (Peg peg : getPegs()) {
            peg.setProbability(1-probPerTrial);
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
        for (Bucket b : getBuckets()) {
            b.setTag("n=" + i);
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
        else { System.err.println(p + " is not a valid probability for the Binomial distribution.");}
    }

    /**
     * Update the number of trials to be performed.
     * @param n : int - How many trials should be performed.
     */
    public void setNumTrials(int n) {
        if (n >= 0) {
            int rowDifference = n-numTrials;
            if (rowDifference > 0) {
                // Keep adding rows until numTrials = n
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
        else { System.err.println(n + " is not a valid number of trials for the Binomial distribution."); }
    }

    /**
     * Add a new row of pegs when editing the board. This method should be called from the UI "plus" button.
     */
    @Override
    public void addRow() {
        if (Workspace.workspace.getSimulation().getSimulationState() == Simulation.SimulationState.Stopped) {
            super.addRow();
            numTrials++;
            // Correct the probability values of the new pegs
            List<Peg> pegs = getPegs();
            for (int i = pegs.size() - getIsoGridWidth(); i < pegs.size(); i++) {
                pegs.get(i).setProbability(1 - probPerTrial);
            }
            // Add tag to the new bucket
            Bucket newB = getBucket(getIsoGridWidth());
            newB.setTag("n=" + numTrials);
        }
    }

    /**
     * Remove a row of pegs when editing the board. This method should be called from the UI "minus" button.
     */
    @Override
    public void removeRow() {
        if (Workspace.workspace.getSimulation().getSimulationState() == Simulation.SimulationState.Stopped) {
            super.removeRow();
            numTrials--;
        }
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
            newB.setTag("n=" + newB.getStartColumn());
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
            newB.setTag("n=" + newB.getStartColumn());
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
     * Getter for the number of trials taken.
     * @return The number of trials taken.
     */
    public int getNumTrials() {
        return numTrials;
    }

    @Override
    public Distribution getDistribution() {return Distribution.Binomial;}

    @Override
    public String toString() {
        return "Binomial board of width " + super.isoGridWidth;
    }
}
