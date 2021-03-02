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
  public List<Float> getMesh(float time) {
    List<Float> points;
    Vector2f bound = new Vector2f();
    Vector2f position = getPosition();

    Vector2f dimensions = new Vector2f(size);
    position.add(dimensions, bound);

    //  +----+
    //  |1 / |
    //  | / 2|
    //  +----+

    float z = 0.25f;

    points = new ArrayList<>(Arrays.asList(
            // Face 1
            position.x, position.y, z,
            bound.x, position.y, z,
            bound.x, bound.y, z,

            // Face 2
            position.x, position.y, z,
            position.x, bound.y, z,
            bound.x, bound.y, z
    ));

    return points;
  }

  @Override
  public List<Float> getUV() {
    final float top = 0.75f;
    final float bottom = 1f;
    final float left = 0.75f;
    final float right = 1f;

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
