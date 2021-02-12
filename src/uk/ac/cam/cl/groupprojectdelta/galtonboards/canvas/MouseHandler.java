package uk.ac.cam.cl.groupprojectdelta.galtonboards.canvas;

import java.util.Collection;
import java.util.LinkedList;
import org.joml.Vector2f;

public class MouseHandler {
  private final Collection<CanvasClickable> clickables = new LinkedList<>();

  private Vector2f currentPos;
  private CanvasClickable currentHover;

  public CanvasClickable getClickableAtPos(Vector2f pos) {
    for (CanvasClickable clickable : clickables) {
      if (clickable.containsPoint(pos)) {
        return clickable;
      }
    }
    return null;
  }

  public Collection<CanvasClickable> getClickablesInRegion(Vector2f from, Vector2f to) {
    Collection<CanvasClickable> result = new LinkedList<>();
    for (CanvasClickable clickable : clickables) {
      if (clickable instanceof CanvasSelectable) {
        if (((CanvasSelectable) clickable).intersectsRegion(from, to)) {
          result.add(clickable);
        }
      }
    }
    return result;
  }

  public void addClickable(CanvasClickable clickable) {
    clickables.add(clickable);
  }

  public void removeClickable(CanvasClickable clickable) {
    clickables.remove(clickable);
  }
}
