package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace;

import org.joml.Vector2f;

import java.util.Set;

public class ColumnBottom extends Column implements LogicalLocation {

    private Bucket bucket;
    private Board board;
    private Vector2f worldPos;
    private int columnIndex;

    public ColumnBottom(int columnIndex, Bucket bucket, Board board) {
        super(columnIndex, bucket, board);
        this.bucket = bucket;
        this.board = board;
    }

    @Override
    public void setBucket(Bucket bucket) {
        this.bucket = bucket;
    }

    @Override
    public void setPosition() {
        float xPos = (super.columnIndex + 0.5f) * Board.unitDistance + board.getWorldPos().x - board.getDimensions().x / 2f;
        float yPos = board.getWorldPos().y - board.getDimensions().y / 2f;
        worldPos =  new Vector2f(xPos, yPos);
    }

    @Override
    public Vector2f getWorldPos() {
        return new Vector2f(worldPos);
    }
}
