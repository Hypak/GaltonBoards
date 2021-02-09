package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace;

import org.joml.Vector2f;
import org.joml.Vector2i;

public class Peg {
    private Vector2i gridPos;
    private float probability;
    private Board board;

    public Peg(Board board) {
        this(0.5f, 0, board);
    }

    public Peg(float probability, int pegInd, Board board) {
        this.gridPos = new Vector2i(0,0);
        this.probability = probability;
        this.board = board;
    }

    public Peg getLeft() {
        return null;
    }

    public Peg getRight() {
        return null;
    }

    public float leftProb() {
        return probability;
    }

    public float rightProb() {
        return 1-probability;
    }

    public Vector2i getGridPos() {
        return gridPos;
    }

    public Vector2f getWorldPos() {
        return board.getWorldPos();
    }

    public int getLeftBucketIndex() {
        return -1;
    }

    public int getRightBucketIndex() {
        return -1;
    }
}
