package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board;

public class CollectorPeg extends Peg {
    CollectorPeg(Board board) {
        super(board);
        setPosition();
    }

    @Override
    public ColumnTop getRightColumn() {
        return super.getBoard().getColumnTop(0);
    }

    public ColumnTop getLeftColumn() {
        return super.getBoard().getColumnTop(0);
    }

    @Override
    public int getLeftColumnIndex() {
        return 0;
    }
}
