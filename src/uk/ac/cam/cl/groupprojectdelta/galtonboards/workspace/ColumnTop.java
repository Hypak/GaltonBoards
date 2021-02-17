package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace;

import org.joml.Vector2f;

import java.util.Set;

public class ColumnTop extends Column implements LogicalLocation {

    private Bucket bucket;
    private ColumnBottom columnBottom;
    private Board board;
    private Vector2f worldPos;

    public ColumnTop(int columnIndex, Bucket bucket, Board board, ColumnBottom columnBottom) {
        super(columnIndex, bucket, board);
        this.bucket = bucket;
        this.board = board;
        this.columnBottom = columnBottom;
        setPosition();
    }

    @Override
    public void setBucket(Bucket bucket) {
        this.bucket = bucket;
        columnBottom.setBucket(bucket);
    }

    @Override
    public void setPosition() {
        float xPos = (super.columnIndex + 0.5f) * Board.unitDistance + board.getWorldPos().x - board.getDimensions().x / 2f;
        float yPos = (Board.bucketDepth) * Board.unitDistance + board.getWorldPos().y - board.getDimensions().y / 2f;
        worldPos =  new Vector2f(xPos, yPos);
        columnBottom.setPosition();
    }

    public ColumnBottom getColumnBottom() {
        return columnBottom;
    }

    @Override
    public Vector2f getWorldPos() {
        return new Vector2f(worldPos);
    }
}
