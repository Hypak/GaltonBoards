package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace;

import org.joml.Vector2f;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Bucket implements LogicalLocation {

    // The index of the first implicit board column that feeds into this bucket
    private int startColumn;
    private Vector2f outputWorldPos;

    // The board that this bucket is on
    private Board board;

    // What this bucket connects to
    private Board output;

    // How many consecutive column outputs feed into this bucket
    private int width;

    // Tag that gets given to any ball collected by this bucket
    private String tag;

    private Set<Ball> ballsInBucket; // need this for visualisation

    String logLocType = "bucket"; // logical location type

    /*
    =====================================================================
                             CONSTRUCTORS
    =====================================================================
     */

    /**
     * Default constructor that creates a unit width board.
     * @param startColumn : int - The index of the first implicit board column that feeds into this bucket.
     * @param board : Board - The board that this bucket is a part of.
     */
    public Bucket(int startColumn, Board board) {
        this(1, startColumn, board);
    }

    /**
     * Construct a bucket with a custom width.
     * @param width : int - How many implicit output columns feed into this bucket.
     * @param startColumn : int - The index of the first implicit board column that feeds into this bucket.
     * @param board : Board - The board that this bucket is a part of.
     */
    public Bucket(int width, int startColumn, Board board) {
        this.startColumn = startColumn;
        this.board = board;
        this.output = null;
        this.width = width;
        this.tag = null;
        //setOutputPosition();
        this.ballsInBucket = new HashSet<>();
    }

    /*
    =====================================================================
                                SETTERS
    =====================================================================
     */

    /**
     * Calculate the world position of this bucket's output.
     */
    void setOutputPosition() {
        // Assume that the board's world point is measured from the very centre of the board
        float xPos = (startColumn + width / 2f) * Board.unitDistance + board.getWorldPos().x - board.getDimensions().x / 2f;
        float yPos = board.getWorldPos().y - board.getDimensions().y /2f;
        this.outputWorldPos = new Vector2f(xPos, yPos);
    }

    /**
     * Set the tag of the bucket.
     * @param tag : String - The tag associated with this bucket.
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * Set the width of the bucket.
     * @param newWidth : int - The new width of this bucket.
     */
    public void setWidth(int newWidth) {
        this.width = newWidth;
    }

    /**
     * Set the width of the bucket.
     * @param newStartColumn : int - The new start column for this bucket.
     */
    public void setStartColumn(int newStartColumn) {
        this.startColumn = newStartColumn;
    }

    /**
     * Called whenever a bucket is deleted to make sure there are no funky bugs.
     */
    public void destroy() {
        // Handle deleting this bucket cleanly (e.g. handle the output connection from this bucket)
    }

    /*
    =====================================================================
                            PUBLIC GETTERS
    =====================================================================
     */

    /**
     * Getter for the output.
     * @return The object (board?) that this bucket leads to.
     */
    public Board getOutput() {
        return output;
    }

    /**
     * Getter for the tag.
     * @return The tag that this bucket gives to any balls that are collected by it.
     */
    public String getTag() {
        return tag;
    }

    /**
     * Find the world position of the output of this bucket.
     * @return The world position of this bucket's output (aka where the balls are going to be funnelled to)
     */
    public Vector2f getOutputWorldPos() {
        return new Vector2f(outputWorldPos);
    }

    /**
     * Getter for the width.
     * @return The width of this bucket (aka how many implicit output columns feed into this bucket)
     */
    public int getWidth() {
        return width;
    }

    /**
     * Getter for the startColumn.
     * @return The leftmost column that feeds into this bucket.
     */
    public int getStartColumn() {
        return startColumn;
    }

    /**
     * Get the world position of the top of a bucket given a column index x.
     * @param x : int - The implicit board column that the ball is falling into.
     * @return The coordinates of the top of the bucket for column x.
     */
    public Vector2f getTopWorldPos(int x) {
        float xPos = (x + 0.5f) * Board.unitDistance + board.getWorldPos().x - board.getDimensions().x / 2f;
        float yPos = (Board.bucketDepth) * Board.unitDistance + board.getWorldPos().y - board.getDimensions().y / 2f;
        return new Vector2f(xPos, yPos);
    }

    /**
     * Get the world position of the bottom of a bucket given a column index x.
     * @param x : int - The implicit board column that the ball is falling into.
     * @return The coordinates of the bottom of the bucket for column x.
     */
    public Vector2f getBottomWorldPos(int x) {
        float xPos = (x + 0.5f) * Board.unitDistance + board.getWorldPos().x - board.getDimensions().x / 2f;
        float yPos = board.getWorldPos().y - board.getDimensions().y / 2f;
        return new Vector2f(xPos, yPos);
    }

    public Board getBoard() {
        return board;
    }

    @Override
    public Vector2f getWorldPos() {
        // Needed so it can implement the LogicalLocation interface
        return getTopWorldPos(0);
    }

    @Override
    public Set<Ball> balls() {
        return ballsInBucket;
    }
}
