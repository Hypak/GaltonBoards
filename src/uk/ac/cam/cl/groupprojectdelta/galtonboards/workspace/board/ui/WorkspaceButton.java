package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.ui;

import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.mouse.WorkspaceClickable;

public abstract class WorkspaceButton implements WorkspaceClickable {
  protected boolean hover;

  @Override
  public void mouseEnter() {
    hover = true;
  }

  @Override
  public void mouseExit() {
    hover = false;
  }
}
