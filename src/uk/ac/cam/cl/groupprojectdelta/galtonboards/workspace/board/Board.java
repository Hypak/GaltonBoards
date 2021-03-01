package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board;

import com.google.common.collect.Iterables;
import org.joml.Vector2f;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.Workspace;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.ui.AddRowButton;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.ui.OutsideBoardRegion;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.ui.RemoveRowButton;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.mouse.ClickableMap;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.mouse.WorkspaceClickable;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.mouse.WorkspaceDraggable;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.mouse.WorkspaceMouseHandler;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.mouse.WorkspaceSelectable;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.Drawable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board implements Drawable, WorkspaceSelectable, WorkspaceDraggable, ClickableMap {

    public static final float unitDistance = 1f;
    static float bucketDepth = 5f;

    // The world coordinates for this board
    private Vector2f worldPos;
    private Vector2f dimensions;

    // How many pegs are on the bottom row of this board's isometric grid (converts to a triangular number)
    private int isoGridWidth;
    private List<Peg> pegs;

    // Explicit bucket instances that collect from the grid's implicit output columns
    private List<Bucket> buckets;
    private List<Integer> bucketWidths;
    private List<Column> columns;

    // Variables and states for editing bucket layouts
    private List<Float> columnBoundaries;
    private boolean updatingBucketLayout = false;
    private Bucket beingEdited;
    private List<Bucket> oldBuckets;

    // UI elements for this board
    private final AddRowButton addRowButton = new AddRowButton(this);
    private final RemoveRowButton removeRowButton = new RemoveRowButton(this);
    private final OutsideBoardRegion outsideBoardRegion = new OutsideBoardRegion(this);

     /*
    =====================================================================
                             CONSTRUCTORS
    =====================================================================
     */

    /**
     * Default constructor for an empty board with a single output bucket.
     */
    public Board() {
        this(0);
    }

    /**
     * Construct a board with isoGridWidth + 1 many output buckets of unit width.
     * @param isoGridWidth : int - How many pegs are an the bottom row of the board's isometric grid.
     */
    public Board(int isoGridWidth) {
        this(isoGridWidth, null);
    }

    /**
     * Construct a board with a custom amount of pegs on the bottom grid row, and customised output buckets.
     * @param isoGridWidth : int - How many pegs are an the bottom row of the board's isometric grid.
     * @param bucketWidths : int[] - Array of integers defining the width of each bucket.
     */
    public Board(int isoGridWidth, int[] bucketWidths) {
        this.worldPos = new Vector2f(0,0);
        this.isoGridWidth = isoGridWidth;
        // Sanitising input for isoGridWidth
        if (isoGridWidth < 0) {
            System.err.println(String.format("%d is an invalid value for isoGridWidth, defaulting to 0", isoGridWidth));
            this.isoGridWidth = 0;
        }
        // To create a unit width bucket for every output column, leave bucketWidths as null
        if(bucketWidths == null) {
            bucketWidths = new int[isoGridWidth+1];
            Arrays.fill(bucketWidths,1);
        }
        setDimensions();
        generatePegs(0.5f);
        generateBuckets(bucketWidths);
    }

     /*
    =====================================================================
                                SETTERS
    =====================================================================
     */

    /**
     * Once board instance has been created with a defined width for
     * the bottom row of pegs (isoGridWidth), generate the peg instances.
     * @param probability : float - The initial probability for all pegs on this board.
     */
    private void generatePegs(float probability) {
        this.pegs = new ArrayList<>();
        int pegAmount = (isoGridWidth * (isoGridWidth+1)) / 2;
        for (int i = 0; i < pegAmount; i++) {
            pegs.add(new Peg(probability, i, this));
        }
    }

    /**
     * Once the board instance has been created with a defined width for the bottom
     * row of pegs (isoGridWidth), generate buckets that collect from the columns.
     * @param bucketWidths : int[] - Array of integers defining the width of each bucket.
     */
    private void generateBuckets(int[] bucketWidths) {
        this.buckets = new ArrayList<>();
        this.bucketWidths = new ArrayList<>();
        this.columns = new ArrayList<>();
        int sum = 0;
        for (int i : bucketWidths) {
            buckets.add(new Bucket(i, sum,this));
            this.bucketWidths.add(i);
            sum += i;
        }
        for (int i=0; i<isoGridWidth+1; i++) {
            Bucket bucket = getBucket(i);
            ColumnBottom cb = new ColumnBottom(i, bucket, this);
            ColumnTop ct = new ColumnTop(i, bucket, this, cb);
            columns.add(ct);
        }
        // Make sure that the bucket instances cover all column outputs
        if (sum != isoGridWidth + 1) {
            System.err.println(String.format("Defined bucket widths do not sum to the amount of output columns. Sum to %d, should sum to %d", sum, isoGridWidth+1));
        }
    }

    /**
     * Set the dimensions of this board upon instantiation and whenever peg layout changes.
     * Also updates the column boundaries.
     */
    private void setDimensions() {
        float width = (isoGridWidth + 1) * unitDistance;
        float height = ((isoGridWidth * (float) Math.sqrt(3) / 2) + bucketDepth + 2) * unitDistance;
        this.dimensions = new Vector2f(width, height);
        setColumnBoundaries();
    }

    /**
     * Update the position of the board when moving it to a new location.
     * Also updates the column boundaries.
     * @param newWorldPos : Vector2f - The new position of the board.
     */
    public void updateBoardPosition(Vector2f newWorldPos) {
        this.worldPos = new Vector2f(newWorldPos);
        setPegPositions();
        setBucketOutputPositions();
        setColumnPositions();
        setColumnBoundaries();
    }

    /**
     * Recalculate the world position of the pegs whenever the board position is moved.
     */
    private void setPegPositions() {
        for(Peg p : pegs) {
            p.setPosition();
        }
    }

    /**
     * Recalculate the world position of the bucket outputs whenever the board position is moved.
     */
    private void setBucketOutputPositions() {
        for(Bucket b : buckets) {
            b.setOutputPosition();
        }
    }

    /**
     * Recalculate the world position of the columns whenever the board position is moved.
     */
    private void setColumnPositions() {
        for(Column c : columns) {
            c.setPosition();
        }
    }

    /**
     * Whenever the board is moved or it's shape changed, update the x-coordinates for the column boundaries
     * (used for dragging buckets in the UI).
     */
    private void setColumnBoundaries() {
        columnBoundaries = new ArrayList<>();
        for (int i = 0; i < isoGridWidth + 2; i++) {
            float xPos = i * unitDistance + worldPos.x - dimensions.x / 2f;
            columnBoundaries.add(xPos);
        }
    }

    /**
     * Helper method for addRow() and removeRow() for repositioning the board after a dimension change.
     * @param oldDimensions : Vector2f - The old board dimensions.
     */
    private void updateYPos(Vector2f oldDimensions) {
        setDimensions();
        float deltaHeight = (dimensions.y - oldDimensions.y) / 2f;
        float newYPos = worldPos.y - deltaHeight;
        updateBoardPosition(new Vector2f(worldPos.x, newYPos));
    }

    /*
    =====================================================================
                                UI METHODS
    =====================================================================
     */

    /**
     * Add a new row of pegs when editing the board. This method should be called from the UI "plus" button.
     */
    public void addRow() {
        Vector2f oldDimensions = new Vector2f(dimensions);
        this.isoGridWidth ++;

        // Add the new row of pegs
        int newNumberOfPegs = pegs.size() + isoGridWidth;
        for (int i = pegs.size(); i < newNumberOfPegs; i++) {
            pegs.add(new Peg(0.5f, i, this));
        }

        // Add a new unit bucket
        bucketWidths.add(1);
        buckets.add(new Bucket(1, isoGridWidth, this));

        ColumnBottom cb = new ColumnBottom(isoGridWidth, buckets.get(buckets.size() - 1), this);
        ColumnTop ct = new ColumnTop(isoGridWidth, buckets.get(buckets.size() - 1), this, cb);
        columns.add(ct);

        // Update the bucket and column positions
        for (Bucket b : buckets) {
            b.setOutputPosition();
        }
        for (Column c : columns) {
            c.setPosition();
        }

        // Update the boards position so that the ball input point remains constant
        updateYPos(oldDimensions);
    }

    /**
     * Remove a row of pegs when editing the board. This method should be called from the UI "minus" button.
     */
    public void removeRow() {
        // Only attempt to remove row if there is actually a row to remove
        if(isoGridWidth < 1) {
            System.err.println("No row to remove.");
            return;
        }

        Vector2f oldDimensions = new Vector2f(dimensions);
        this.isoGridWidth --;

        // Remove the bottom row of pegs
        for (int i = 0; i < isoGridWidth + 1; i++) {
            pegs.remove(pegs.size() - 1);
        }

        // If the rightmost bucket has a width > 1, reduce its size, else delete it
        int oldWidth = bucketWidths.get(bucketWidths.size() - 1);
        if(oldWidth > 1) {
            // Reduce the width of the last bucket by 1
            int newWidth = oldWidth - 1;
            bucketWidths.set(bucketWidths.size() - 1, newWidth);
            buckets.get(buckets.size() - 1).setWidth(newWidth);
        }
        else {
            // Delete the last bucket
            bucketWidths.remove(bucketWidths.size() - 1);
            buckets.get(buckets.size() - 1).destroy();
            buckets.remove(buckets.size() - 1);
        }

        columns.remove(columns.size() - 1);

        // Update the bucket and column positions
        for (Bucket b : buckets) {
            b.setOutputPosition();
        }
        for (Column c : columns) {
            c.setPosition();
        }

        // Update the boards position so that the ball input point remains constant
        updateYPos(oldDimensions);
    }

    /**
     * Get the position of the addRow() button.
     * @return The coordinates of the centre of the addRow() button.
     */
    public Vector2f getPlusButtonPos() {
        float xPos = worldPos.x - 2f * unitDistance;
        float yPos = worldPos.y + dimensions.y / 2f - (1 + isoGridWidth * (float) Math.sqrt(3) / 2f + 1) * unitDistance;
        return new Vector2f(xPos, yPos);
    }

    /**
     * Get the position of the removeRow() button.
     * @return The coordinates of the centre of the removeRow() button.
     */
    public Vector2f getMinusButtonPos() {
        float xPos = worldPos.x + 2f * unitDistance;
        float yPos = worldPos.y + dimensions.y / 2f - (1 + isoGridWidth * (float) Math.sqrt(3) / 2f + 1) * unitDistance;
        return new Vector2f(xPos, yPos);
    }

    /**
     * Signal that the board's bucket layout is being changed, save the old layout in case changes cancelled.
     */
    public void startDraggingBucket(Bucket bucket) {
        if (!updatingBucketLayout) {
            updatingBucketLayout = true;
            beingEdited = bucket;
            oldBuckets = new ArrayList<>(buckets);
        }
    }

    /**
     * Cancel the changing of the bucket layout (e.g. by right clicking when dragging. Restore old layout.
     */
    public void cancelDraggingBucket() {
        if (updatingBucketLayout) {
            updatingBucketLayout = false;
            beingEdited = null;
            buckets = new ArrayList<>(oldBuckets);
        }
    }

    /**
     * Confirm the change of the bucket layout (e.g. by letting go of LMB when dragging).
     */
    public void confirmDraggingBucket() {
        if (updatingBucketLayout) {
            updatingBucketLayout = false;
            beingEdited = null;
        }
    }

    /**
     * Get the neighbouring column boundaries for the left edge of the bucket being edited.
     * @return The world positions of the column boundaries (or infinity if edge can't be extended).
     */
    public Vector2f getNeighbouringLeftSideBoundaries() {
        if (updatingBucketLayout) {
            int start = beingEdited.getStartColumn();
            int leftInd = start - 1;
            int rightInd = start + 1;
            float leftPos;
            float rightPos;
            if (leftInd < 0) {
                leftPos = Float.NEGATIVE_INFINITY;
            }
            else {
                leftPos = columnBoundaries.get(leftInd);
            }
            if (beingEdited.getWidth() == 1) {
                rightPos = Float.POSITIVE_INFINITY;
            }
            else {
                rightPos = columnBoundaries.get(rightInd);
            }
            return new Vector2f(leftPos, rightPos);
        }
        // The board is not aware that bucket layout is being changed
        System.err.println("No bucket has been selected.");
        return null;
    }

    /**
     * Get the neighbouring column boundaries for the right edge of the bucket being edited.
     * @return The world positions of the column boundaries (or infinity if edge can't be extended).
     */
    public Vector2f getNeighbouringRightSideBoundaries() {
        if (updatingBucketLayout) {
            int last = beingEdited.getStartColumn() + beingEdited.getWidth() - 1;
            int leftInd = last;
            int rightInd = last + 2;
            float leftPos;
            float rightPos;
            if (beingEdited.getWidth() == 1) {
                leftPos = Float.NEGATIVE_INFINITY;
            }
            else {
                leftPos = columnBoundaries.get(leftInd);
            }
            if (rightInd > columnBoundaries.size()) {
                rightPos = Float.POSITIVE_INFINITY;
            }
            else {
                rightPos = columnBoundaries.get(rightInd);
            }
            return new Vector2f(leftPos, rightPos);
        }
        // The board is not aware that bucket layout is being changed
        System.err.println("No bucket has been selected.");
        return null;
    }

    /**
     * Signal that the boundaries of a bucket have been moved and get new neighbouring column positions.
     * @param leftEdge : boolean - Has the bucket's left or right edge been extended left?
     * @return The new neighbouring column positions of the edge being moved.
     */
    public Vector2f edgeExtendedLeft(boolean leftEdge) {
        if(updatingBucketLayout && buckets.indexOf(beingEdited) > 0) {
            if (leftEdge) {
                // if left edge has been stretched left, increase bucket width, decrement startColumn, and reduce bucket to the left
                beingEdited.setWidth(beingEdited.getWidth() + 1);
                beingEdited.setStartColumn(beingEdited.getStartColumn() - 1);
                Bucket leftBucket = buckets.get(buckets.indexOf(beingEdited) - 1);
                if (leftBucket.getWidth() > 1) {
                    // Reduce width of bucket to the left
                    leftBucket.setWidth(leftBucket.getWidth() - 1);
                }
                else {
                    // Delete bucket to the left as was of unit width
                    leftBucket.destroy();
                    buckets.remove(buckets.indexOf(beingEdited) - 1);
                }

                // Change the bucket for the column to the left
                getColumnTop(beingEdited.getStartColumn()).setBucket(beingEdited);

            }
            else {
                // if right edge has been stretched left, decrement width and spawn new unit bucket to the right
                beingEdited.setWidth(beingEdited.getWidth() - 1);
                Bucket newBucket = new Bucket(1, beingEdited.getStartColumn() + beingEdited.getWidth(), this);
                buckets.add(buckets.indexOf(beingEdited) + 1, newBucket);

                // Change the bucket for the column to the right
                getColumnTop(beingEdited.getStartColumn() + beingEdited.getWidth()).setBucket(newBucket);

            }
            return leftEdge ? getNeighbouringLeftSideBoundaries() : getNeighbouringRightSideBoundaries();
        }
        // The board isn't aware of the layout being changed, or invalid bucket being extended left
        System.err.println("Error extending bucket edge left.");
        return null;
    }

    /**
     * Signal that the boundaries of a bucket have been moved and get new neighbouring column positions.
     * @param leftEdge : boolean - Has the bucket's left or right edge been extended right?
     * @return The new neighbouring column positions of the edge being moved.
     */
    public Vector2f edgeExtendedRight(boolean leftEdge) {
        if(updatingBucketLayout && buckets.indexOf(beingEdited) > 0) {
            if (leftEdge) {
                // if left edge has been stretched right, decrease bucket width, increment startColumn, and spawn new unit bucket to the left
                beingEdited.setWidth(beingEdited.getWidth() - 1);
                beingEdited.setStartColumn(beingEdited.getStartColumn() + 1);
                Bucket newBucket = new Bucket(1, beingEdited.getStartColumn() - 1, this);
                buckets.add(buckets.indexOf(beingEdited), newBucket);

                // Change the bucket for the column to the left
                getColumnTop(beingEdited.getStartColumn() - 1).setBucket(newBucket);

            }
            else {
                // if right edge has been stretched right, increase width and reduce bucket to the right
                beingEdited.setWidth(beingEdited.getWidth() - 1);

                Bucket rightBucket = buckets.get(buckets.indexOf(beingEdited) + 1);
                if (rightBucket.getWidth() > 1) {
                    // Reduce width of bucket to the right and increase its start column
                    rightBucket.setWidth(rightBucket.getWidth() - 1);
                    rightBucket.setStartColumn(rightBucket.getStartColumn() + 1);
                }
                else {
                    // Delete bucket to the right as was of unit width
                    rightBucket.destroy();
                    buckets.remove(buckets.indexOf(beingEdited) + 1);
                }

                // Change the bucket for the column to the right
                getColumnTop(beingEdited.getStartColumn() + beingEdited.getWidth() - 1).setBucket(beingEdited);

            }
            return leftEdge ? getNeighbouringLeftSideBoundaries() : getNeighbouringRightSideBoundaries();
        }
        // The board isn't aware of the layout being changed, or invalid bucket being extended right
        System.err.println("Error extending bucket edge right.");
        return null;
    }

    /**
     * Reset this board to its default given its parameters (should be overridden by child instances).
     * Reset all of the bucket outputs here as it will be a common feature for all boards.
     */
    public void reset() {
        int[] bw = new int[isoGridWidth+1];
        Arrays.fill(bw,1);
        generateBuckets(bw);
    }

    /*
    =====================================================================
                            PUBLIC GETTERS
    =====================================================================
     */

    /**
     * Get the root peg, this is the first peg that balls come across at the top of the grid.
     * @return The root peg instance for this board.
     */
    public Peg getRootPeg() {
        return pegs.get(0);
    }

    /**
     * Get a specific peg from this board (first peg has index 0, increases from left to right, row by row).
     * @param x : int - The index of the peg.
     * @return The peg at index x.
     */
    public Peg getPeg(int x) {
        return pegs.get(x);
    }

    /**
     * Getter for isoGridWidth.
     * @return the amount of pegs on the bottom row of this board's grid.
     */
    public int getIsoGridWidth() {
        return isoGridWidth;
    }

    /**
     * Get the explicit bucket instance that collects balls from the implicit output column at index x.
     * @param x : int - The index of the implicit bucket (grid column) that we want to map to an output bucket instance.
     * @return The bucket instance that collects from grid output at column x.
     */
    public Bucket getBucket(int x) {
        // Input sanitation
        if(x > isoGridWidth || x < 0) {
            System.err.println(String.format("%d is an invalid bucket index.", x));
            return null;
        }
        // Assume that the sum of bucket widths is equal to isoGridWidth + 1
        int accumulator = 0;
        for (Bucket b : buckets) {
            accumulator += b.getWidth();
            if (accumulator > x) {
                return b;
            }
        }
        // Above assumption didn't hold, valid bucket index could not be found
        System.err.println(String.format("Bucket collecting from output %d could not be found.", x));
        return null;
    }

    public Column getColumnTop(int x) {
        // Input sanitation
        if(x > isoGridWidth || x < 0) {
            System.err.println(String.format("%d is an invalid column index.", x));
            return null;
        }
        return columns.get(x);
    }

    /**
     * Getter for worldPos.
     * @return The world coordinates of this board.
     */
    public Vector2f getWorldPos() {
        return new Vector2f(worldPos);
    }

    /**
     * Getter for the ball input position in world coordinates.
     * @return The coordinates of the ball input position.
     */
    public Vector2f getInputPos() {
        return new Vector2f(worldPos.x, worldPos.y + dimensions.y / 2);
    }

    /**
     * Getter for the dimensions.
     * @return The width and height dimensions of this board.
     */
    public Vector2f getDimensions() {
        return new Vector2f(dimensions);
    }

    /**
     * Getter for the pegs list.
     * @return The list of pegs on this board.
     */
    public List<Peg> getPegs() {
        return pegs;
    }

    /**
     * Getter for the buckets list.
     * @return The list of buckets on this board.
     */
    public List<Bucket> getBuckets() {
        return buckets;
    }

    /**
     * Getter to determine whether the bucket layout is being updated.
     * @return updatingBucketLayout.
     */
    public boolean isUpdatingBucketLayout() {
        return updatingBucketLayout;
    }

    /**
     * Getter for the bucket currently being edited.
     * @return The bucket being edited.
     */
    public Bucket getBeingEdited() {
        return beingEdited;
    }

    public boolean isOpen() {
        // This should return false if the board's buckets have been closed by the user.
        return true;
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


        worldPos.add(halfDimensions, highBound);
        worldPos.sub(halfDimensions, lowBound);


        //  +----+
        //  |1 / |
        //  | / 2|
        //  +----+

        float z = 1;

        points = new ArrayList<>(Arrays.asList(
                // Face 1
                lowBound.x, lowBound.y, z,
                highBound.x, lowBound.y, z,
                highBound.x, highBound.y, z,

                // Face 2
                lowBound.x, lowBound.y, z,
                lowBound.x, highBound.y, z,
                highBound.x, highBound.y, z
        ));

        for (Peg peg : pegs) {
            points.addAll(peg.getMesh(time));
        }

        for (Bucket bucket : buckets) {
            points.addAll(bucket.getMesh(time));
        }

        if (Workspace.workspace.getClickableMap() == this) {
            points.addAll(addRowButton.getMesh(time));
            if (isoGridWidth > 1) {
                points.addAll(removeRowButton.getMesh(time));
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

        for (Peg peg : pegs) {
            UVs.addAll(peg.getUV());
        }

        for (Bucket bucket : buckets) {
            UVs.addAll(bucket.getUV());
        }

        if (Workspace.workspace.getClickableMap() == this) {
            UVs.addAll(addRowButton.getUV());
            if (isoGridWidth > 1) {
                UVs.addAll(removeRowButton.getUV());
            }
        }

        return UVs;
    }

    //TODO: Add the addRowButton and removeRowButton to the color when merged.

    /*
    =====================================================================
                               MOUSE EVENTS
    =====================================================================
     */

    @Override
    public boolean containsPoint(Vector2f point) {
        Vector2f topleft = new Vector2f();
        Vector2f bottomright = new Vector2f();
        Vector2f halfDimensions = new Vector2f();
        getDimensions().mul(0.5f, halfDimensions);
        getWorldPos().sub(halfDimensions, topleft);
        getWorldPos().add(halfDimensions, bottomright);
        return point.x > topleft.x
            && point.x < bottomright.x
            && point.y > topleft.y
            && point.y < bottomright.y;
    }

    @Override
    public boolean intersectsRegion(Vector2f from, Vector2f to) {
        Vector2f topleft = new Vector2f();
        Vector2f bottomright = new Vector2f();
        Vector2f halfDimensions = new Vector2f();
        getDimensions().mul(0.5f, halfDimensions);
        getWorldPos().sub(halfDimensions, topleft);
        getWorldPos().add(halfDimensions, bottomright);
        return from.x < bottomright.x
            && from.y < bottomright.y
            && to.x > topleft.x
            && to.y > topleft.y;
    }

    @Override
    public void select() {
        //TODO: start drawing highlight around board to show that it's selected.
    }

    public void deselect() {
        //TODO: stop drawing highlight around board to show that it's not selected.
    }

    @Override
    public void doubleClick() {
        Workspace.workspace.setClickableMap(this);
    }

    @Override
    public void moveDrag(Vector2f delta) {
        updateBoardPosition(getWorldPos().add(delta));
    }

    @Override
    public Iterable<? extends WorkspaceClickable> getClickables() {
        return Iterables.concat(
            pegs,
            List.of(addRowButton, removeRowButton, outsideBoardRegion)
        );
        //TODO: Add other board UI elements
    }
}