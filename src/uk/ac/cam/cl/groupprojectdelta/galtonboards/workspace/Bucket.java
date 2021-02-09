package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace;

import org.joml.Vector2f;

public class Bucket {
    private Vector2f gridPos;
    private Board board;
    private Object output;
    private int width;
    private String tag;

    public Bucket(Board board) {
        this.gridPos = new Vector2f(0,0);
        this.board = board;
        this.output = null;
        this.width = 1;
        this.tag = null;
    }

    public Object getOutput() {
        return output;
    }

    public String getTag() {
        return tag;
    }

    public Vector2f getOutputWorldPos() {
        return board.getWorldPos();
    }
}
