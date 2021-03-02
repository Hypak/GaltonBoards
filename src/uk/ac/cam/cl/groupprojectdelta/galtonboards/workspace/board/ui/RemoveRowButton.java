package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.ui;

import org.joml.Vector2f;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.Board;

public class RemoveRowButton extends WorkspaceButton {
  private final Board board;

  public RemoveRowButton(Board board) {
    this.board = board;
  }

  @Override
  protected Vector2f getPosition() {
    return new Vector2f()
        .add(board.getWorldPos())
        .add(Board.unitDistance + size * 2, board.getDimensions().y/2);
  }

  @Override
  public void release() {
    board.removeRow();
  }
}