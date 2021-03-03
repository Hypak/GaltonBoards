package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.mouse;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import org.joml.Vector2f;

/**
 * Interface for something that represents an interface. Implemented by boards and configurations.
 */
public interface ClickableMap {
  public Iterable<? extends WorkspaceClickable> getClickables();

  public default WorkspaceClickable getClickableAtPos(Vector2f pos) {
    for (WorkspaceClickable clickable : getClickables()) {
      if (clickable.containsPoint(pos)) {
        return clickable;
      }
    }
    return null;
  }

  public default Collection<WorkspaceSelectable> getSelectablesInRegion(Vector2f from, Vector2f to) {
    Vector2f topleft = new Vector2f(Float.min(from.x, to.x), Float.min(from.y,to.y));
    Vector2f bottomright = new Vector2f(Float.max(from.x, to.x), Float.max(from.y,to.y));
    Collection<WorkspaceSelectable> result = new LinkedList<>();
    for (WorkspaceClickable clickable : getClickables()) {
      if (clickable instanceof WorkspaceSelectable) {
        if (((WorkspaceSelectable) clickable).intersectsRegion(topleft, bottomright)) {
          result.add((WorkspaceSelectable) clickable);
        }
      }
    }
    return result;
  }
}
