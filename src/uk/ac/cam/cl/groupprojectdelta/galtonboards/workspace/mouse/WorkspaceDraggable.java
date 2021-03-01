package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.mouse;

import org.joml.Vector2f;

public interface WorkspaceDraggable {

  /**
   * called when mouse is pressed, then moved over this object.
   */
  public default void startDrag() {};

  /**
   * @param delta : the change in mouse position since the last this moveDrag was called, only called during a drag.
   */
  public default void moveDrag(Vector2f delta) {};


  /**
   * called when the mouse is released, and this object was being dragged. The object is no longer dragged.
   */
  public default void endDrag() {};
}
