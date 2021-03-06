package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board;

import org.joml.Vector2f;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.Workspace;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.ui.PipeEditHandle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CollectorBoard extends Board {

    private Peg singlePeg;

    public CollectorBoard() {
        super();
        singlePeg = new CollectorPeg(this);
    }

    @Override
    public List<Peg> getPegs() {
        return List.of(singlePeg);
    }

    @Override
    public Peg getRootPeg() {
        return singlePeg;
    }

    @Override
    public Peg getPeg(int x) {
        return singlePeg;
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

    @Override
    public void addRow() {
        return;
    }

    /**
     * Remove a row of pegs when editing the board. This method should be called from the UI "minus" button.
     */
    @Override
    public void removeRow() {
        return;
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
    }

    @Override
    public String toString() {
        return "Collector board";
    }

     /*
    =====================================================================
                                 GRAPHICS
    =====================================================================
     */

    @Override
    public List<Float> getMesh(float time) {
        List<Float> points = new ArrayList<>();
        Vector2f lowBound = new Vector2f();
        Vector2f highBound = new Vector2f();
        Vector2f halfDimensions = new Vector2f();
        getDimensions().mul(0.5f, halfDimensions);


        super.getWorldPos().add(halfDimensions, highBound);
        super.getWorldPos().sub(halfDimensions, lowBound);


        //  +----+
        //  |1 / |
        //  | / 2|
        //  +----+

        float zEpsilon = z + 1E-2f;

        points = new ArrayList<>(Arrays.asList(
                // Face 1
                lowBound.x, lowBound.y, zEpsilon,
                highBound.x, lowBound.y, zEpsilon,
                highBound.x, highBound.y, zEpsilon,

                // Face 2
                lowBound.x, lowBound.y, zEpsilon,
                lowBound.x, highBound.y, zEpsilon,
                highBound.x, highBound.y, zEpsilon
        ));

        for (Bucket bucket : super.getBuckets()) {
            points.addAll(bucket.getMesh(time));
        }

        if (Workspace.workspace.getClickableMap() == this) {
            /*points.addAll(super.addRowButton.getMesh(time));
            if (isoGridWidth > 1) {
                points.addAll(super.removeRowButton.getMesh(time));
            }*/
            for (PipeEditHandle handle : getPipeEditHandles()) {
                points.addAll(handle.getMesh(time));
            }
        }

        return points;
    }

    @Override
    public List<Float> getUV() {
        final float top = 0f;
        final float bottom = 0.5f;
        final float left = 0.5f;
        final float right = 1f;

        List<Float> UVs = new ArrayList<>(Arrays.asList(
                // face 1
                top, left,
                bottom, left,
                bottom, right,
                // face 2
                top, left,
                top, right,
                bottom, right
        ));

        for (Bucket bucket : getBuckets()) {
            UVs.addAll(bucket.getUV());
        }

        if (Workspace.workspace.getClickableMap() == this) {
            /*UVs.addAll(addRowButton.getUV());
            if (isoGridWidth > 1) {
                UVs.addAll(removeRowButton.getUV());
            }*/
            for (PipeEditHandle handle : getPipeEditHandles()) {
                UVs.addAll(handle.getUV());
            }
        }

        return UVs;
    }

    @Override
    public List<Float> getColourTemplate() {
        List<Float> ct = new ArrayList<>(Arrays.asList(
                1f, 1f, 1f,
                1f, 1f, 1f,
                1f, 1f, 1f,

                1f, 1f, 1f,
                1f, 1f, 1f,
                1f, 1f, 1f
        ));

        for (Bucket bucket : getBuckets()) {
            ct.addAll(bucket.getColourTemplate());
        }
        if (Workspace.workspace.getClickableMap() == this) {
            /*ct.addAll(addRowButton.getColourTemplate());
            if (isoGridWidth > 1) {
                ct.addAll(removeRowButton.getColourTemplate());
            }*/
            for (PipeEditHandle handle : getPipeEditHandles()) {
                ct.addAll(handle.getColourTemplate());
            }
        }

        return ct;
    }
}
