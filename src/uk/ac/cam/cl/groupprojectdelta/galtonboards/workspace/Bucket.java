package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace;

import org.joml.Vector2f;

public class Bucket {

    // The index of the first implicit board column that feeds into this bucket
    private int startColumn;
    private Vector2f outputWorldPos;

    // The board that this bucket is on
    private Board board;

    // What this bucket connects to
    private Object output;

    // How many consecutive column outputs feed into this bucket
    private int width;

    // Tag that gets given to any ball collected by this bucket
    private String tag;

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
        setOutputPosition();
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

    /*
    =====================================================================
                            PUBLIC GETTERS
    =====================================================================
     */

    /**
     * Getter for the output.
     * @return The object (board?) that this bucket leads to.
     */
    public Object getOutput() {
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
        return outputWorldPos;
    }

    /**
     * Getter for the width.
     * @return The width of this bucket (aka how many implicit output columns feed into this bucket)
     */
    public int getWidth() {
        return width;
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
}
