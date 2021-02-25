package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.mouse;

import org.joml.Vector2f;

public interface WorkspaceSelectable extends WorkspaceClickable {

  /**
   * @param from one corner of the selection box
   * @param to another corner of the selection box
   * @return whether creating a selection from 'from' to 'to' would include this object.
   * By convention, the rectangle does not need to contain the object to select it, just touch it.
   */
  public boolean intersectsRegion(Vector2f from, Vector2f to);
  public default void select() {};
  public default void deselect() {};
  //List<InspectorOption> getInspectorOptions
}
