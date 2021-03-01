package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.ui;

import org.joml.Vector2f;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.Board;

public class AddRowButton extends WorkspaceButton {
  private final Board board;

  public AddRowButton(Board board) {
    this.board = board;
  }

  private Vector2f getPosition() {
    return new Vector2f()
        .add(board.getWorldPos())
        .mul(2f)
        .add(board.getDimensions().x/2, -board.getDimensions().y/2)
        .add(Board.unitDistance, 0);
  }

  @Override
  public boolean containsPoint(Vector2f point) {
    return false;
  }

  @Override
  public void release() {

  }
}
