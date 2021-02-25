package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.mouse;

import org.joml.Vector2f;

public interface WorkspaceClickable
{

  /**
   * Called when the mouse is depressed while over the object.
   */
  public default void press() {};

  /**
   * Called when the mouse is released while over the object.
   */
  public default void release() {};

  /**
   * Called when the mouse is pressed, released, then pressed, then released again in quick succession.
   * (press and release still call during a double-click)
   */
  public default void doubleClick() {};

  /**
   * Called when the mouse moves such that it is over this object.
   */
  public default void mouseEnter() {};

  /**
   * Called when the mouse moves such that it is no longer over this object.
   */
  public default void mouseExit() {};

  /**
   * @param point the point to test
   * @return whether, if the mouse is at point point, that means the mouse is over the object.
   */
  public boolean containsPoint(Vector2f point);
}
