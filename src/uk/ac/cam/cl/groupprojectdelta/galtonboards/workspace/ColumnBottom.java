package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace;

import org.joml.Vector2f;

public class ColumnBottom extends Column implements LogicalLocation {

    // The world coordinates of the top of this column
    private Vector2f worldPos;

    // The index of this column
    private int columnIndex;

    /**
     * Create the top part of a column output of a board.
     * @param columnIndex : int - The index of this column.
     * @param bucket : Bucket - The bucket this column feeds in to.
     * @param board : Board - The board this column is a part of.
     */
    public ColumnBottom(int columnIndex, Bucket bucket, Board board) {
        super(columnIndex, bucket, board);
    }

    /**
     * Update the world coordinates of this column bottom.
     */
    @Override
    public void setPosition() {
        float xPos = (super.columnIndex + 0.5f) * Board.unitDistance + getBoard().getWorldPos().x - getBoard().getDimensions().x / 2f;
        float yPos = getBoard().getWorldPos().y - getBoard().getDimensions().y / 2f;
        worldPos =  new Vector2f(xPos, yPos);
    }

    @Override
    public Vector2f getWorldPos() {
        return new Vector2f(worldPos);
    }
}
