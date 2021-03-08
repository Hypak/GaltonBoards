package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board;

import org.joml.Vector2f;

import java.util.HashSet;
import java.util.Set;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.Ball;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.LogicalLocation;

public class Column {

    // References to the bucket this column feeds into and the board it is on
    private Bucket bucket;
    private Board board;

    // The index of the column on the board
  /*private*/ int columnIndex;

    // Ball set for Logical Location
    //private Set<Ball> ballSet;

    /**
     * Create a column output for a board (this class should only be instantiated via a super() call).
     * @param columnIndex : int - The index of this column.
     * @param bucket : Bucket - The bucket this column feeds in to.
     * @param board : Board - The board this column is a part of.
     */
    public Column(int columnIndex, Bucket bucket, Board board) {
        this.columnIndex = columnIndex;
        this.bucket = bucket;
        this.board = board;
        //this.ballSet = new HashSet<>();
    }

    /**
     * Update the bucket that this column feeds into.
     */
    public void setBucket(Bucket bucket) {
        this.bucket = bucket;
    }

    /**
     * Getter for the bucket currently being edited.
     * @return The bucket being edited.
     */
    public Bucket getBucket() {
        return bucket;
    }

    /**
     * This method exists to be overridden by ColumnTop and ColumnBottom.
     */
    public void setPosition() {
        return;
    }

    /*
    //@Override
    public Vector2f getWorldPos() {
        return null;
    }

    //@Override
    public Set<Ball> balls() {
        return ballSet;
    }

    //@Override
    public void addBall(Ball ball) {
        //System.out.println("ADDING BALL TO COLUMN");
        //ballSet.add(ball);
        //bucket.addBall(ball);
    }

    public void removeBall(Ball ball) {
        //ballSet.remove(ball);
        //bucket.removeBall(ball);
    }*/

    /**
     * Getter for the board this column is on.
     * @return The board.
     */
    //@Override
    public Board getBoard() {
        return board;
    }
}
