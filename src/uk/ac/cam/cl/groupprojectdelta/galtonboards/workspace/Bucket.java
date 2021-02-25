package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace;

import java.util.ArrayList;
import java.util.Arrays;
import org.joml.Vector2f;
import org.lwjgl.opengl.INTELBlackholeRender;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.Drawable;
import java.util.*;

public class Bucket implements LogicalLocation, Drawable {

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
        setOutputPosition();
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

    /**
     * Set the output board of the bucket.
     * @param board : Board - the board balls fall into from this bucket
     */
    public void setOutput(Board board) {
        output = board;
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

    /**
     * Calculate the top left position of this bucket so that it can be drawn on the screen.
     * @return The world coordinates of the top left.
     */
    public Vector2f getTopLeft() {
        // TODO - Turn this into a variable if needing optimised - will need to update world pos whenever board sizes are changed
        float xPos = startColumn * Board.unitDistance + board.getWorldPos().x - board.getDimensions().x / 2f;
        float yPos = (Board.bucketDepth) * Board.unitDistance + board.getWorldPos().y - board.getDimensions().y / 2f;
        return new Vector2f(xPos, yPos);
    }

    /**
     * Calculate the bottom right position of this bucket so that it can be drawn on the screen.
     * @return The world coordinates of the bottom right.
     */
    public Vector2f getBottomRight() {
        // TODO - Turn this into a variable if needing optimised - will need to update world pos whenever board sizes are changed
        float xPos = (startColumn + width)* Board.unitDistance + board.getWorldPos().x - board.getDimensions().x / 2f;
        float yPos = board.getWorldPos().y - board.getDimensions().y / 2f;
        return new Vector2f(xPos, yPos);
    }

    /**
     * Calculate the bottom right position of this bucket's left anchor (anchor's top left is just getTopLeft()).
     * @return The world coordinates of the bottom right of the anchor.
     */
    public Vector2f getLeftAnchorBottomRight() {
        // TODO - Turn this into a variable if needing optimised - will need to update world pos whenever board sizes are changed
        float xPos = (startColumn + 0.5f)* Board.unitDistance + board.getWorldPos().x - board.getDimensions().x / 2f;
        float yPos = board.getWorldPos().y - board.getDimensions().y / 2f;
        return new Vector2f(xPos, yPos);
    }

    /**
     * Calculate the top left position of this bucket's right anchor (anchor's bottom right is just getBottomRight()).
     * @return The world coordinates of the bottom right of the anchor.
     */
    public Vector2f getRightAnchorTopLeft() {
        // TODO - Turn this into a variable if needing optimised - will need to update world pos whenever board sizes are changed
        float xPos = (startColumn + width - 0.5f)* Board.unitDistance + board.getWorldPos().x - board.getDimensions().x / 2f;
        float yPos = (Board.bucketDepth) * Board.unitDistance + board.getWorldPos().y - board.getDimensions().y / 2f;
        return new Vector2f(xPos, yPos);
    }

    /**
     * Getter for the board this bucket is on.
     * @return The board.
     */
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

    public Map<String, Integer> liquifiedBallsByTag() {
        // Returns a map of ball tags to the number of them that are
        // liquified in a bucket
        // TODO: set the liquid set to be the union of all columnBottom.balls() for all column bottoms of this bucket:
        Set<Ball> liquid = null;
        Map<String, Integer> nByTag = new HashMap<>();
        for (Ball ball : liquid) {
            if (nByTag.containsKey(ball.getTag())) {
                nByTag.put(ball.getTag(), nByTag.get(ball.getTag()) + 1);
            } else {
                nByTag.put(ball.getTag(), 1);
            }
        }
        return nByTag;
    }

    @Override
    public List<Float> getMesh(float time) {
        List<Float> points = new ArrayList<>();
        Vector2f lowBound = getBottomRight();
        Vector2f highBound = getTopLeft();


        //  +----+
        //  |1 / |
        //  | / 2|
        //  +----+

        float z = 0.75f;

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

        return UVs;
    }
}
