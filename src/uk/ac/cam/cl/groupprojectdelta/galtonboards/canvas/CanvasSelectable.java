package uk.ac.cam.cl.groupprojectdelta.galtonboards.canvas;

import org.joml.Vector2f;

public interface CanvasSelectable extends CanvasClickable {
  default void click(boolean left) {};
  public boolean intersectsRegion(Vector2f from, Vector2f to);
  //List<InspectorOption> getInspectorOptions
}
