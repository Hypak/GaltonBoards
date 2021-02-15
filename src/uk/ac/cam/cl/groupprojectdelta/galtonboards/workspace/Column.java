package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace;

import org.joml.Vector2f;

import java.util.HashSet;
import java.util.Set;

public class Column implements LogicalLocation {

    private Bucket bucket;
    private Board board;
  /*private*/ int columnIndex;
    private Set<Ball> ballSet;

    public Column(int columnIndex, Bucket bucket, Board board) {
        this.columnIndex = columnIndex;
        this.bucket = bucket;
        this.board = board;
        this.ballSet = new HashSet<>();
    }

    public Bucket getBucket() {
        return bucket;
    }

    public void setPosition() {

    }

    @Override
    public Vector2f getWorldPos() {
        return null;
    }

    @Override
    public Set<Ball> balls() {
        return ballSet;
    }

    @Override
    public Board getBoard() {
        return board;
    }
}
