package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace;

import org.joml.Vector2f;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.Drawable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Board implements Drawable {

    static float unitDistance = 1f;
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

    //TODO - Be able to update the boards (change peg probabilites, add new rows, change buckets, update bucket tags)
    //TODO - Generate default boards (e.g Gaussian, uniform, poisson etc)

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
        int sum = 0;
        for (int i : bucketWidths) {
            buckets.add(new Bucket(i, sum,this));
            this.bucketWidths.add(i);
            sum += i;
        }
        // Make sure that the bucket instances cover all column outputs
        if (sum != isoGridWidth + 1) {
            System.err.println(String.format("Defined bucket widths do not sum to the amount of output columns. Sum to %d, should sum to %d", sum, isoGridWidth+1));
        }
    }

    /**
     * Set the dimensions of this board upon instantiation and whenever peg layout changes.
     */
    private void setDimensions() {
        float width = (isoGridWidth + 1) * unitDistance;
        float height = ((isoGridWidth * (float) Math.sqrt(3) / 2) + bucketDepth + 2) * unitDistance;
        this.dimensions = new Vector2f(width, height);
    }

    /**
     * Update the position of the board when moving it to a new location.
     * @param newWorldPos : Vector2f - The new position of the board.
     */
    public void updateBoardPosition(Vector2f newWorldPos) {
        this.worldPos = new Vector2f(newWorldPos);
        setPegPositions();
        setBucketOutputPositions();
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

    public void addRow() {

    }

    public void removeRow() {

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

    /**
     * Getter for worldPos.
     * @return The world coordinates of this board.
     */
    public Vector2f getWorldPos() {
        return worldPos;
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
        return dimensions;
    }

    @Override
    public List<Float> getMesh(float time) {
        List<Float> points = new ArrayList<>();
        Vector2f bound = new Vector2f();

        worldPos.add(dimensions, bound);

        //  +----+
        //  |1 / |
        //  | / 2|
        //  +----+

        float z = 5;

        points = new ArrayList<>(Arrays.asList(
                // Face 1
                worldPos.x, worldPos.y, z,
                bound.x, worldPos.y, z,
                bound.x, bound.y, z,

                // Face 2
                worldPos.x, worldPos.y, z,
                worldPos.x, bound.y, z,
                bound.x, bound.y, z
        ));

        // todo: add meshes from pegs and balls
        for (Peg peg : pegs) {
            points.addAll(peg.getMesh(time));
        }

        return points;
    }

    @Override
    public List<Float> getUV() {
        List<Float> UVs = List.of(
                // face 1
                0f,0f,
                1f,0f,
                1f,1f,
                // face 2
                0f,0f,
                0f,1f,
                1f,1f
        );
        return UVs;
    }

    public boolean isOpen() {
        // This should return false if the board's buckets have been closed by the user.
        return true;
    }
}