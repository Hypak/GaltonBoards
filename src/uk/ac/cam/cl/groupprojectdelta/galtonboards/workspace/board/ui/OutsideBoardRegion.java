package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.ui;

import org.joml.Vector2f;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.Workspace;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.Board;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.mouse.WorkspaceClickable;

public class OutsideBoardRegion implements WorkspaceClickable {
  private final Board board;

  public OutsideBoardRegion(Board board) {
    this.board = board;
  }

  @Override
  public void doubleClick() {
    Workspace.workspace.resetClickableMAp();
  }

  @Override
  public boolean containsPoint(Vector2f point) {
    return !board.containsPoint(point);
  }
}
