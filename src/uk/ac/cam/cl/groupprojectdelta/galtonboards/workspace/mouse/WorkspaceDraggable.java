package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.mouse;

import org.joml.Vector2f;

public interface WorkspaceDraggable {
  public void startDrag(boolean left);
  public void moveDrag(Vector2f delta);
  public void endDrag();
}
