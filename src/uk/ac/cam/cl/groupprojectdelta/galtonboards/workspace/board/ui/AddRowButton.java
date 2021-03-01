package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.ui;

import org.joml.Vector2f;

public class AddRowButton extends WorkspaceButton {
  private Vector2f position;

  @Override
  public boolean containsPoint(Vector2f point) {
    return false;
  }
}
