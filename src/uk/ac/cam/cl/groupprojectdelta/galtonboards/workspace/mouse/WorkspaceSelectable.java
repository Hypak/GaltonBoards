package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.mouse;

import org.joml.Vector2f;

public interface WorkspaceSelectable extends WorkspaceClickable {
  public boolean intersectsRegion(Vector2f from, Vector2f to);
  public default void select() {};
  public default void deselect() {};
  //List<InspectorOption> getInspectorOptions
}
