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
    Collection<WorkspaceSelectable> result = new LinkedList<>();
    for (WorkspaceClickable clickable : getClickables()) {
      if (clickable instanceof WorkspaceSelectable) {
        if (((WorkspaceSelectable) clickable).intersectsRegion(from, to)) {
          result.add((WorkspaceSelectable) clickable);
        }
      }
    }
    return result;
  }
}
