package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.ui;

import org.joml.Vector2f;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.Board;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddRowButton extends WorkspaceButton {
  private final Board board;

  public AddRowButton(Board board) {
    this.board = board;
  }

  protected Vector2f getPosition() {
    return new Vector2f()
        .add(board.getWorldPos())
        .add(Board.unitDistance, board.getDimensions().y/2);
  }

  @Override
  public void release() {
    board.addRow();
  }

  @Override
  public List<Float> getUV() {
    final float top = 0.0f;
    final float bottom = 0.25f;
    final float left = 0.0f;
    final float right = 0.25f;

    List<Float> UVs = List.of(
            // face 1
            top,left,
            bottom,left,
            bottom,right,
            // face 2
            top,left,
            top,right,
            bottom,right
    );
    return UVs;
  }

}
