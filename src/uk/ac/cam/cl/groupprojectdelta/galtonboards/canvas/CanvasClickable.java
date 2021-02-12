package uk.ac.cam.cl.groupprojectdelta.galtonboards.canvas;

import org.joml.Vector2f;

public interface CanvasClickable
{
  public default void press() {};
  public default void release() {};
  public default void doubleClick() {};
  public default void mouseEnter() {};
  public default void mouseExit() {};
  public boolean containsPoint(Vector2f point);
}
