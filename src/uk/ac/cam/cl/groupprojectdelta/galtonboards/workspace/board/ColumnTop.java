package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board;

import org.joml.Vector2f;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.Ball;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.LogicalLocation;

import java.util.HashSet;
import java.util.Set;

public class ColumnTop extends Column implements LogicalLocation {

    // References to the bottom of this column
    private ColumnBottom columnBottom;

    // The world coordinates of the top of this column
    private Vector2f worldPos;

    private Set<Ball> ballSet;

    private Bucket bucket;

    /**
     * Create the top part of a column output of a board.
     * @param columnIndex : int - The index of this column.
     * @param bucket : Bucket - The bucket this column feeds in to.
     * @param board : Board - The board this column is a part of.
     * @param columnBottom : ColumnBottom - The bottom part of this column.
     */
    public ColumnTop(int columnIndex, Bucket bucket, Board board, ColumnBottom columnBottom) {
        super(columnIndex, bucket, board);
        this.columnBottom = columnBottom;
        this.bucket = bucket;
        setPosition();
        ballSet = new HashSet<>();
    }

    /**
     * Update the world coordinates of this column top.
     */
    @Override
    public void setPosition() {
        float xPos = (super.columnIndex + 0.5f) * Board.unitDistance + getBoard().getWorldPos().x - getBoard().getDimensions().x / 2f;
        float yPos = (Board.bucketDepth) * Board.unitDistance + getBoard().getWorldPos().y - getBoard().getDimensions().y / 2f;
        worldPos =  new Vector2f(xPos, yPos);
        columnBottom.setPosition();
    }

    /**
     * Update the bucket that this column feeds into as well as updating the column bottom.
     */
    @Override
    public void setBucket(Bucket bucket) {
        super.setBucket(bucket);
        columnBottom.setBucket(bucket);
    }

    /**
     * Getter for the bottom part of this column.
     * @return The bottom of the column.
     */
    public ColumnBottom getColumnBottom() {
        return columnBottom;
    }

    @Override
    public Vector2f getWorldPos() {
        return new Vector2f(worldPos);
    }

    @Override
    public Set<Ball> balls() {
        return ballSet;
    }

    @Override
    public void addBall(Ball ball) {
        ballSet.add(ball);
        //bucket.addBall(ball);
    }

    @Override
    public void removeBall(Ball ball) {
        ballSet.remove(ball);
        //bucket.removeBall(ball);
    }
}
