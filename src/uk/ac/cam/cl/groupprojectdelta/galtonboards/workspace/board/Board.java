package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board;

import com.google.common.collect.Iterables;
import org.joml.Vector2f;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.Ball;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.Configuration;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.Simulation;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.Workspace;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.ui.AddRowButton;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.ui.OutsideBoardRegion;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.ui.PipeEditHandle;
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
import java.util.stream.Collectors;

public class Board implements Drawable, WorkspaceSelectable, WorkspaceDraggable, ClickableMap {

    public static final float unitDistance = 1f;
    static float bucketDepth = 5f;

    // The world coordinates for this board
    private Vector2f worldPos;
    private Vector2f dimensions;

    private Simulation simulation = null;

    // How many pegs are on the bottom row of this board's isometric grid (converts to a triangular number)
    protected int isoGridWidth;
    private List<Peg> pegs;

    // Explicit bucket instances that collect from the grid's implicit output columns
    private List<Bucket> buckets;
    private List<Integer> bucketWidths;
    private List<ColumnTop> columns;

    // Variables and states for editing bucket layouts
    private List<Float> columnBoundaries;
    private boolean updatingBucketLayout = false;
    private Bucket beingEdited;
    private List<Bucket> oldBuckets;
    private List<Bucket> inputs;


    // UI elements for this board
    private final AddRowButton addRowButton = new AddRowButton(this);
    private final RemoveRowButton removeRowButton = new RemoveRowButton(this);
    private final OutsideBoardRegion outsideBoardRegion = new OutsideBoardRegion(this);

    private boolean selected = false;
     /*
    =====================================================================
                             CONSTRUCTORS
    =====================================================================
     */

    /**
     * Default constructor for a board with a single peg and two output buckets.
     */
    public Board() {
        this(1);
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
        this.inputs = new ArrayList<>();
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
    public void generateBuckets(int[] bucketWidths) {
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
     * Update the set of inputs to this board by adding or removing a bucket input.
     * @param b : Bucket - The bucket that we are either adding or removing.
     */
    public void updateInputs(Bucket b) {
        if (!inputs.contains(b)) {
            inputs.add(b);
        }
        else {
            inputs.remove(b);
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
        Vector2f diff = new Vector2f(newWorldPos);
        diff.sub(worldPos);
        if (Workspace.workspace != null) {
            for (Ball ball : Workspace.workspace.getConfiguration().getSimulation().getBalls()) {
                if (ball.getLogLoc().getBoard() == this) {
                    if (ball.getNextLogLoc().getBoard() == this) {
                        ball.getPosition().add(diff);
                    } else {
                        Vector2f move = new Vector2f(diff);
                        move.mul(1 - ball.getTravelledProportion());
                        ball.getPosition().add(move);
                    }
                } else if (ball.getNextLogLoc().getBoard() == this) {
                    Vector2f move = new Vector2f(diff);
                    move.mul(ball.getTravelledProportion());
                    ball.getPosition().add(move);
                }
            }
        }
        this.worldPos = new Vector2f(newWorldPos);
        setPegPositions();
        setBucketOutputPositions();
        setColumnPositions();
        setColumnBoundaries();
    }

    /**
     * Recalculate the world position of the pegs whenever the board position is moved.
     */
    void setPegPositions() {
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

    public void setSimulation(Simulation sim) {
        simulation = sim;
    }

    public void toRelativeScale() {
        for (Bucket bucket : buckets) {
            bucket.relativeScale = true;
        }
    }

    public void toAbsoluteScale() {
        for (Bucket bucket : buckets) {
            bucket.relativeScale = false;
            while (bucket.balls().size() > bucket.getSize()) {
                /*
                If a bucket is in relative scale, adding balls to it doesn't scale it up even if bucket size exceeded.
                So if converting back to absolute scale, need to do this:
                 */
                bucket.getSimulation().enlargeBuckets();
            }
        }
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
        if (Workspace.workspace.getSimulation().getSimulationState() == Simulation.SimulationState.Stopped) {
            Vector2f oldDimensions = new Vector2f(dimensions);
            this.isoGridWidth++;

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
    }

    /**
     * Remove a row of pegs when editing the board. This method should be called from the UI "minus" button.
     */
    public void removeRow() {
        if (Workspace.workspace.getSimulation().getSimulationState() == Simulation.SimulationState.Stopped) {
            // Only attempt to remove row if there is actually a row to remove
            if (isoGridWidth < 2) {
                System.err.println("Can't remove last row.");
                return;
            }

            Vector2f oldDimensions = new Vector2f(dimensions);
            this.isoGridWidth--;

            // Remove the bottom row of pegs
            for (int i = 0; i < isoGridWidth + 1; i++) {
                pegs.remove(pegs.size() - 1);
            }

            // If the rightmost bucket has a width > 1, reduce its size, else delete it
            int oldWidth = bucketWidths.get(bucketWidths.size() - 1);
            if (oldWidth > 1) {
                // Reduce the width of the last bucket by 1
                int newWidth = oldWidth - 1;
                bucketWidths.set(bucketWidths.size() - 1, newWidth);
                buckets.get(buckets.size() - 1).setWidth(newWidth);
            } else {
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
            if (rightInd >= columnBoundaries.size()) {
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
                    buckets.remove(leftBucket);
                    bucketWidths.remove(buckets.indexOf(beingEdited));
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
            bucketWidths.set(buckets.indexOf(beingEdited), beingEdited.getWidth());
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
        if(updatingBucketLayout && buckets.indexOf(beingEdited) < buckets.size() - 1) {
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
                beingEdited.setWidth(beingEdited.getWidth() + 1);

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
                    bucketWidths.remove(buckets.indexOf(beingEdited) + 1);
                }

                // Change the bucket for the column to the right
                getColumnTop(beingEdited.getStartColumn() + beingEdited.getWidth() - 1).setBucket(beingEdited);

            }
            bucketWidths.set(buckets.indexOf(beingEdited), beingEdited.getWidth());
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

    /**
     * Safely delete this board from existence.
     */
    public void destroy() {
        for (Bucket b : getInputs()) {
            b.clearOutput();
        }
        for (Bucket b : getBuckets()) {
            b.destroy();
        }
    }

    /**
     * Change the type of the board (e.g. from a Gaussian board to a Uniform board).
     * @param d : Distribution - The new type for this board/
     */
    public void changeBoard(Distribution d) {
        if (Workspace.workspace.getSimulation().getSimulationState() == Simulation.SimulationState.Stopped) {
            if (this instanceof GaussianBoard && d == Distribution.Gaussian ||
                    this instanceof UniformBoard && d == Distribution.Uniform ||
                    this instanceof BinomialBoard && d == Distribution.Binomial ||
                    this instanceof GeometricBoard && d == Distribution.Geometric ||
                    this.getClass() == Board.class && d == Distribution.Custom) {
                return;
            }

            // We are changing the board to a new type, so instantiate new board (trying to maintain layout) and delete this
            Board newBoard;
            switch (d) {
                case Gaussian:
                    newBoard = new GaussianBoard(0, isoGridWidth + 1);
                    break;
                case Uniform:
                    newBoard = new UniformBoard(0, 1, isoGridWidth + 1);
                    break;
                case Binomial:
                    newBoard = new BinomialBoard(isoGridWidth, 0.5f);
                    break;
                case Geometric:
                    newBoard = new GeometricBoard(0.5f, isoGridWidth);
                    break;
                default:
                    newBoard = new Board(isoGridWidth);
                    break;
            }

            if (buckets.size() != columns.size()) {
                // Buckets have been edited, copy over existing bucket layout
                int[] widths = new int[buckets.size()];
                for (int i = 0; i < buckets.size(); i++) {
                    widths[i] = buckets.get(i).getWidth();
                }
                newBoard.generateBuckets(widths);
                // Copy over tags
                for (Bucket b : buckets) {
                    newBoard.getBucket(b.getStartColumn()).setTag(b.getTag());
                }
            }

            // Copy bucket outputs
            for (Bucket b : buckets) {
                if (b.getOutput() != null) {
                    newBoard.getBucket(b.getStartColumn()).setOutput(b.getOutput());
                }
            }

            newBoard.updateBoardPosition(getWorldPos());

            // For all of the buckets that feed into this board, update their output to the new board
            for (Bucket b : inputs) {
                b.setOutput(newBoard);
                b.destroy();
            }

            // remove board from configuration
            Workspace.workspace.getConfiguration().addBoard(newBoard);
            if (Workspace.workspace.getConfiguration().getStartBoard() == this) {
                Workspace.workspace.getConfiguration().setStartBoard(newBoard);
            }
            Workspace.workspace.getConfiguration().removeBoard(this);
        }

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
     * Get the buckets that acts as the input to this board.
     * @return The list of buckets that input into this board.
     */
    public List<Bucket> getInputs() {
        return inputs;
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

    public ColumnTop getColumnTop(int x) {
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
     * Getter for the list of pipe edit handles.
     * @return The list of pipe edit handles on this board.
     */
    public List<PipeEditHandle> getPipeEditHandles() {
        return buckets.stream().map(Bucket::getPipeEditHandle).collect(Collectors.toList());
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

    public Simulation getSimulation() {
        return simulation;
    }

    public Distribution getDistribution() {return Distribution.Custom;}

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
            for (PipeEditHandle handle : getPipeEditHandles()) {
                UVs.addAll(handle.getUV());
            }
        }

        return UVs;
    }

    @Override
    public List<Float> getColourTemplate() {
        List<Float> ct;
        if (Workspace.workspace.mouseHandler.getSelectionHandler().isSelected(this)) {
             ct = new ArrayList<>(Arrays.asList(
                    0.5f, 0.5f, 1f,
                    0.5f, 0.5f, 1f,
                    0.5f, 0.5f, 1f,

                    0.5f, 0.5f, 1f,
                    0.5f, 0.5f, 1f,
                    0.5f, 0.5f, 1f
            ));
        } else if (Workspace.workspace.mouseHandler.getCurrentClickable() ==  this) {
            ct = new ArrayList<>(Arrays.asList(
                    0.8f, 0.8f, 0.8f,
                    0.8f, 0.8f, 0.8f,
                    0.8f, 0.8f, 0.8f,

                    0.8f, 0.8f, 0.8f,
                    0.8f, 0.8f, 0.8f,
                    0.8f, 0.8f, 0.8f
            ));
        } else {
            ct = new ArrayList<>(Arrays.asList(
                    1f, 1f, 1f,
                    1f, 1f, 1f,
                    1f, 1f, 1f,

                    1f, 1f, 1f,
                    1f, 1f, 1f,
                    1f, 1f, 1f
            ));
        }

        for (Peg peg : pegs) {
            ct.addAll(peg.getColourTemplate());
        }

        for (Bucket bucket : buckets) {
            ct.addAll(bucket.getColourTemplate());
        }

        if (Workspace.workspace.getClickableMap() == this) {
            ct.addAll(addRowButton.getColourTemplate());
            if (isoGridWidth > 1) {
                ct.addAll(removeRowButton.getColourTemplate());
            }
            for (PipeEditHandle handle : getPipeEditHandles()) {
                ct.addAll(handle.getColourTemplate());
            }
        }

        return ct;
    }

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
                List.of(addRowButton, removeRowButton, outsideBoardRegion),
                getPipeEditHandles(),
                pegs,
                buckets
        );
        //TODO: Add other board UI elements
    }

    @Override
    public String toString() {
        return "Board of width " + isoGridWidth;
    }
}