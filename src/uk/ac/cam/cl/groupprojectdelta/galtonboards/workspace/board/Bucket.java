package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board;

import java.util.ArrayList;
import java.util.Arrays;
import org.joml.Vector2f;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joml.Vector3f;
import org.joml.Vector4f;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.Drawable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.Ball;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.LogicalLocation;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.ui.PipeEditHandle;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.Simulation;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.Workspace;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.mouse.WorkspaceSelectable;

public class Bucket implements LogicalLocation, Drawable, WorkspaceSelectable {

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

    List<String> ballsTaggedWith;

    String logLocType = "bucket"; // logical location type

    // UI elements for this bucket
    private final PipeEditHandle pipeEditHandle;

    private static final float zEpsilon = z + 5E-3f;

    public boolean relativeScale = false;

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
        setOutputPosition();
        this.ballsInBucket = new HashSet<>();
        this.pipeEditHandle = new PipeEditHandle(this);
        this.ballsTaggedWith = new ArrayList<>();
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
        // To actually change the tag, do:
        // setGivenTags(List.of(tag));
        /* However, currently this function is being used in a very different way
        *  from the rest of the tag system (e.g. in ficAllBucketTags in geometric/binomial/uniform
        *  board functions; if this function interacts with the rest of the tagging system,
        *  things will break (in particular tag colours will not be defined in the Simulation object.*/
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
        clearOutput();
    }

    /**
     * Set the output board of the bucket.
     * @param board : Board - the board balls fall into from this bucket
     */
    public void setOutput(Board board) {
        clearOutput();
        output = board;
        board.updateInputs(this);
    }

    /**
     * Reset the output of this bucket by setting it to null
     */
    public void clearOutput() {
        if (output != null) {
            output.updateInputs(this);
        }
        output = null;
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
        return getGivenTags().get(0);
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

    public Simulation getSimulation() {
        return Workspace.workspace.getSimulation();
    }

    public Float getSize() {
        return getSimulation().getBucketScale();
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

    @Override
    public void removeBall(Ball ball) {
        ballsInBucket.remove(ball);
    }

    @Override
    public void addBall(Ball ball) {
        ballsInBucket.add(ball);
        if (ballsInBucket.size() > getSimulation().getBucketScale() && relativeScale == false) {
            getSimulation().enlargeBuckets();
        }
    }

    @Override
    public List<String> getGivenTags() {
        return ballsTaggedWith;
    }

    @Override
    public void setGivenTags(List<String> newTagList) {
        ballsTaggedWith = newTagList;
        board.getColumnTop(getStartColumn()).setGivenTags(newTagList);
    }

    @Override
    public void clearGivenTags() {
        ballsTaggedWith = new ArrayList<>();
        board.getColumnTop(getStartColumn()).clearGivenTags();
    }

    public Map<String, Integer> liquifiedBallsByTag() {
        // Returns a map of ball tags to the number of them that are
        // liquified in a bucket
        Set<Ball> liquid = ballsInBucket;
        Map<String, Integer> nByTag = new HashMap<>();
        for (Ball ball : liquid) {
            if (nByTag.containsKey(ball.getTag())) {
                nByTag.put(ball.getTag(), nByTag.get(ball.getTag()) + 1);
            } else {
                nByTag.put(ball.getTag(), 1);
            }
        }
        //System.out.println("Seeing tags: " + nByTag.keySet());
        return nByTag;
    }

    public Map<Vector3f, Integer> liquifiedBallsByColour() {
        Map<Vector3f, Integer> numByCol = new HashMap<>();
        Map<String, Integer> numByTag = liquifiedBallsByTag();
        for (String tag : numByTag.keySet()) {
            numByCol.put(getSimulation().getTagColour(tag), numByTag.get(tag));
        }
        return numByCol;
    }

    public List<Vector4f> liquidBarStructure() {
        /* FUTURE OPTIMISATION: this is currently called independently by each of the getMesh/getUV/getColourTemplate
           functions; can save on performance by only calling it once and then having these methods access an
           updated copy.
        */
        List<Vector4f> nrgbs = new ArrayList<>();
        /*
        For a vector in nrgbs, the .w is the number of balls, and .x/.y/.z corresponds to Red/Green/Blue
        (In any sensible language, there would be a built-in pair type that I would've used instead, but I didn't want
        to import things / create a new class just to have a pair type).
         */
        Map<String, Integer> numByTag = liquifiedBallsByTag();
        if (relativeScale) {
            /*
            This is an option to make the bucket always display at full height, coloured in proportion to the balls
            inside it. Good for e.g. the second Bayes demo, where the number of healthy is much higher than the number
            of sick people.
             */
            if (ballsInBucket.size() == 0) {
                nrgbs.add(new Vector4f(0f, 0f, 0f, 1));
            } else {
                for (String tag : numByTag.keySet()) {
                    Vector4f nrgb = new Vector4f();
                    nrgb.w = numByTag.get(tag) / (float)ballsInBucket.size();
                    Vector3f colour = getSimulation().getTagColour(tag);
                    nrgb.x = colour.x;
                    nrgb.y = colour.y;
                    nrgb.z = colour.z;
                    nrgbs.add(nrgb);
                }
            }
        } else { // -> NORMAL NON-RELATIVE SCALE
            // white space sufficient to fill the part of the bar that isn't covered by liquified balls:
            nrgbs.add(new Vector4f(1f, 1f, 1f, 1 - ballsInBucket.size() / getSize()));
            for (String tag : numByTag.keySet()) {
                Vector4f nrgb = new Vector4f();
                nrgb.w = numByTag.get(tag) / getSize(); // getSize is the number of balls that fills a bucket
                Vector3f colour = getSimulation().getTagColour(tag);
                nrgb.x = colour.x;
                nrgb.y = colour.y;
                nrgb.z = colour.z;
                nrgbs.add(nrgb);
            }
        }
        //System.out.println(nrgbs);
        return nrgbs;
    }

    private List<Float> getRectMesh(Vector2f bottomRight, Vector2f topLeft) {
        //  +----+
        //  |1 / |
        //  | / 2|
        //  +----+
        return new ArrayList<>(Arrays.asList(
            // Face 1
            bottomRight.x, bottomRight.y, zEpsilon,
            topLeft.x, bottomRight.y, zEpsilon,
            topLeft.x, topLeft.y, zEpsilon,

            // Face 2
            bottomRight.x, bottomRight.y, zEpsilon,
            bottomRight.x, topLeft.y, zEpsilon,
            topLeft.x, topLeft.y, zEpsilon
        ));

    }

    @Override
    public List<Float> getMesh(float time) {
        List<Float> points = new ArrayList<>();
        Vector2f lowBound = getBottomRight();
        Vector2f highBound = getTopLeft();

        List<Vector4f> nrgbs = liquidBarStructure();
        List<Float> levels = new ArrayList<>();
        float height = highBound.y - lowBound.y;
        float currentHeight = highBound.y;
        for (Vector4f nrgb : nrgbs) {
            levels.add(currentHeight);
            currentHeight -= nrgb.w * height;
        }
        levels.add(currentHeight);
        int i = 0;
        while (i + 1 < levels.size()) {
            float y0 = levels.get(i);
            float y1 = levels.get(i+1);
            Vector2f bottomRight = new Vector2f(lowBound.x, y1);
            Vector2f topLeft = new Vector2f(highBound.x, y0);
            points = Stream.concat(points.stream(), getRectMesh(bottomRight, topLeft).stream()).collect(Collectors.toList());
            ++i;
        }

        if (output != null) {
            final float w = highBound.x - lowBound.x;
            highBound = lowBound;
            lowBound = output.getInputPos();
            lowBound.x -= w / 2;
            lowBound.y += w;

            points.addAll(Arrays.asList(
                    highBound.x, highBound.y, zEpsilon,
                    highBound.x + w, highBound.y, zEpsilon,
                    lowBound.x + w, lowBound.y, zEpsilon,

                    highBound.x, highBound.y, zEpsilon,
                    lowBound.x, lowBound.y, zEpsilon,
                    lowBound.x + w, lowBound.y, zEpsilon
            ));
        }

        return points;
    }

    @Override
    public List<Float> getUV() {
        final float top = 0f;
        final float bottom = 0.5f;
        final float left = 0.5f;
        final float right = 1f;

        List<Vector4f> nrgbs = liquidBarStructure();

        List<Float> UVs = new ArrayList<>();

        for (int i = 0; i < nrgbs.size(); i++) {
            UVs.add(top);
            UVs.add(left);
            UVs.add(bottom);
            UVs.add(left);
            UVs.add(bottom);
            UVs.add(right);

            UVs.add(top);
            UVs.add(left);
            UVs.add(top);
            UVs.add(right);
            UVs.add(bottom);
            UVs.add(right);
        }

        if (output != null) {
            UVs.addAll(List.of(
                    // pipe
                    // face 1
                    top, left,
                    bottom, left,
                    bottom, right,
                    // face 2
                    top, left,
                    top, right,
                    bottom, right
            ));
        }

        return UVs;
    }

    @Override
    public List<Float> getColourTemplate() {
        List<Vector4f> nrgbs = liquidBarStructure();

        List<Float> colours = new ArrayList<>();

        for (Vector4f nrgb : nrgbs) {
            for (int i = 0; i < 6; i++) {
                colours.add(nrgb.x);
                colours.add(nrgb.y);
                colours.add(nrgb.z);
            }
        }

        /*return List.of(
                1f, 1f, 1f,
                1f, 1f, 1f,
                1f, 1f, 1f,

                1f, 1f, 1f,
                1f, 1f, 1f,
                1f, 1f, 1f,

                1f, 0f, 0f, // change colour here
                1f, 0f, 0f,
                1f, 0f, 0f,

                1f, 0f, 0f,
                1f, 0f, 0f,
                1f, 0f, 0f
        ));*/

        if (output != null) {
            colours.addAll(List.of(
                1f, 1f, 1f,
                1f, 1f, 1f,
                1f, 1f, 1f,

                1f, 1f, 1f,
                1f, 1f, 1f,
                1f, 1f, 1f
            ));
        }

        return colours;
    }

    PipeEditHandle getPipeEditHandle() {
        return pipeEditHandle;
    }

    @Override
    public boolean containsPoint(Vector2f point) {
        Vector2f topleft = getTopLeft();
        Vector2f bottomright = getBottomRight();
        System.out.println(point.x > topleft.x
            && point.x < bottomright.x
            && point.y < topleft.y
            && point.y > bottomright.y);
        System.out.println(topleft);
        System.out.println(bottomright);
        System.out.println(point);
        return point.x > topleft.x
            && point.x < bottomright.x
            && point.y < topleft.y
            && point.y > bottomright.y;
    }

    @Override
    public boolean intersectsRegion(Vector2f from, Vector2f to) {
        Vector2f topleft = getTopLeft();
        Vector2f bottomright = getBottomRight();
        return from.x < bottomright.x
            && from.y < topleft.y
            && to.x > topleft.x
            && to.y > bottomright.y;
    }

    @Override
    public void select() {
        //TODO: start drawing highlight around board to show that it's selected.
    }

    public void deselect() {
        //TODO: stop drawing highlight around board to show that it's not selected.
    }
}
