package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.mouse;

import java.util.Collection;
import java.util.LinkedList;
import org.joml.Vector2f;

/**
 * Class for storing all of the objects that can be clicked at the moment.
 * (e.g. use a different ClickableMap for when a board is selected vs when boards can be moved around)
 */
public class ClickableMap {
  private final Collection<WorkspaceClickable> clickables = new LinkedList<>();

  private Vector2f currentPos;
  private WorkspaceClickable currentHover;

  public WorkspaceClickable getClickableAtPos(Vector2f pos) {
    for (WorkspaceClickable clickable : clickables) {
      if (clickable.containsPoint(pos)) {
        return clickable;
      }
    }
    return null;
  }

  public Collection<WorkspaceSelectable> getSelectablesInRegion(Vector2f from, Vector2f to) {
    Collection<WorkspaceSelectable> result = new LinkedList<>();
    for (WorkspaceClickable clickable : clickables) {
      if (clickable instanceof WorkspaceSelectable) {
        if (((WorkspaceSelectable) clickable).intersectsRegion(from, to)) {
          result.add((WorkspaceSelectable) clickable);
        }
      }
    }
    return result;
  }

  public void addClickable(WorkspaceClickable clickable) {
    clickables.add(clickable);
  }

  public void removeClickable(WorkspaceClickable clickable) {
    clickables.remove(clickable);
  }
}
