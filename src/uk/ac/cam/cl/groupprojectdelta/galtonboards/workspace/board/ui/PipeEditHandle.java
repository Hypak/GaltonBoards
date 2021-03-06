package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.ui;

import org.joml.Vector2f;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.UserInterface;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.Board;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.Bucket;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.mouse.WorkspaceClickable;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.mouse.WorkspaceDraggable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class represent the handles at the bottom of every bucket.
 * The handles are used to connect each bucket with another board.
 */
public class PipeEditHandle extends WorkspaceButton implements WorkspaceDraggable {

  // deltaDrag is the current position with respect to the initial position while dragging
  private Vector2f deltaDrag = new Vector2f();
  private boolean selected = false;
  private final Bucket bucket;

  public PipeEditHandle(Bucket bucket) {
    this.bucket = bucket;
  }

  @Override
  public Vector2f getPosition() {
    float width = bucket.getBottomRight().x - bucket.getTopLeft().x;
    return new Vector2f()
        .add(deltaDrag)
        .add(bucket.getBottomRight())
        .add(-width / 2 - size / 2, size / 2);
  }

  @Override
  public List<Float> getMesh(float time) {
    Vector2f bound = new Vector2f();
    Vector2f position = getPosition();

    Vector2f dimensions = new Vector2f(size);
    position.add(dimensions, bound);

    //  +----+
    //  |1 / |
    //  | / 2|
    //  +----+

    return new ArrayList<>(Arrays.asList(
            (position.x + bound.x)/2, position.y, z,
            position.x, bound.y, z,
            bound.x, bound.y, z
    ));
  }

  @Override
  public List<Float> getUV() {
    final float top = 0.5f;
    final float bottom = 0.75f;
    final float left = 0.0f;
    final float right = 0.25f;

    return List.of(
        // face 1
        top, left,
        bottom, left,
        bottom, right
        /* face 2
        top, left,
        top, right,
        bottom, right
         */
    );
  }

  @Override
  public List<Float> getColourTemplate() {
    if (selected) {
      return listFromColors(0.3f, 0.8f, 0.8f);
    } else if (hover) {
      return listFromColors(0.3f, 0.8f, 0.8f);
    } else {
      return listFromColors(0.75f, 0.75f, 0.75f);
    }
  }

  protected static List<Float> listFromColors(float red, float green, float blue) {
    return List.of(
            red, green, blue,
            red, green, blue,
            red, green, blue
    );
  }

  @Override
  public void startDrag() {
    selected = true;
  }

  @Override
  public void moveDrag(Vector2f delta) {
    deltaDrag.add(delta);
  }

  @Override
  public void endDrag() {
    // On end drag, check if the handle was dropped on a board
    // If it is, connect the bucket to the board
    // Otherwise, remove the pipe
    boolean droppedOnABoard = false;
    for (WorkspaceClickable clickable : UserInterface.userInterface.getConfiguration().getClickables()) {
      if (clickable instanceof Board) {
        Board board = (Board) clickable;
        if (board.containsPoint(getPosition())) {
          // Make sure it is not a self loop
          if (!bucket.getBoard().equals(board)) {
            bucket.setOutput(board);
            droppedOnABoard = true;
            break;
          }
        }
      }
    }
    if (!droppedOnABoard) {
      bucket.clearOutput();
    }
    selected = false;
    deltaDrag = new Vector2f();
  }
}
