package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.ui;

import org.joml.Vector2f;

public class RemoveRowButton extends WorkspaceButton {
  private Vector2f getPosition() {
    return null;
  }

  @Override
  public boolean containsPoint(Vector2f point) {
    return false;
  }
}