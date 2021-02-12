package uk.ac.cam.cl.groupprojectdelta.galtonboards.canvas;

import org.joml.Vector2f;

public interface CanvasSelectable extends CanvasClickable {
  public boolean intersectsRegion(Vector2f from, Vector2f to);
  public default void select() {};
  public default void deselect() {};
  //List<InspectorOption> getInspectorOptions
}
