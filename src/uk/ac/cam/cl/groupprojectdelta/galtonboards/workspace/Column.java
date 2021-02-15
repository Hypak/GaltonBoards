package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace;

public class Column {

    private Bucket bucket;
    private Board board;
    private int columnIndex;

    public Column(int columnIndex, Bucket bucket, Board board) {
        this.columnIndex = columnIndex;
        this.bucket = bucket;
        this.board = board;
    }

    public Bucket getBucket() {
        return bucket;
    }

    public void setPosition() {

    }
}
