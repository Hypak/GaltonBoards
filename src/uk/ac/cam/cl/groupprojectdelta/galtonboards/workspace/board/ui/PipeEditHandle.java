package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.ui;

import org.joml.Vector2f;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.UserInterface;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.Board;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.Bucket;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.mouse.WorkspaceClickable;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.mouse.WorkspaceDraggable;

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
            .add(-width/2 - size/2, size/2);
  }

  @Override
  public List<Float> getColourTemplate() {
    if (selected) {
      return listFromColors(0, 1, 0);
    } else if (hover) {
      return listFromColors(1, 1, 0);
    } else {
      return listFromColors(1, 0, 1);
    }
  }

  @Override
  public void startDrag() {
    selected = true;
  };

  @Override
  public void moveDrag(Vector2f delta) {
    deltaDrag.add(delta);
  };

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
      bucket.setOutput(null);
    }
    selected = false;
    deltaDrag = new Vector2f();
  };
}
