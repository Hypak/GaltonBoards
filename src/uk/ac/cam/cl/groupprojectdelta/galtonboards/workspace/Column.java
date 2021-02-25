package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace;

import org.joml.Vector2f;

import java.util.HashSet;
import java.util.Set;

public class Column implements LogicalLocation {

    // References to the bucket this column feeds into and the board it is on
    private Bucket bucket;
    private Board board;

    // The index of the column on the board
  /*private*/ int columnIndex;

    // Ball set for Logical Location
    private Set<Ball> ballSet;

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
        this.ballSet = new HashSet<>();
    }

    /**
     * Update the bucket that this column feeds into.
     */
    public void setBucket(Bucket bucket) {
        return;
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

    @Override
    public Vector2f getWorldPos() {
        return null;
    }

    @Override
    public Set<Ball> balls() {
        return ballSet;
    }

    /**
     * Getter for the board this column is on.
     * @return The board.
     */
    @Override
    public Board getBoard() {
        return board;
    }
}
