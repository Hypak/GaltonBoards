package uk.ac.cam.cl.groupprojectdelta.galtonboards.canvas;

import java.util.Collection;
import java.util.List;

public class CanvasSelectionHandler {
  private static CanvasSelectionHandler selectionHandler;
  private List<CanvasSelectable> currentSelection;

  // Singleton Pattern
  public static CanvasSelectionHandler getSelectionHandler() {
    if (selectionHandler == null) {
      selectionHandler = new CanvasSelectionHandler();
    }
    return selectionHandler;
  }

  public void addToSelection(CanvasSelectable selectable) {
    currentSelection.add(selectable);
  }

  public void addToSelection(Collection<CanvasSelectable> selectables) {
    currentSelection.addAll(selectables);
  }

  public void removeFromSelection(CanvasSelectable selectable) {
    currentSelection.remove(selectable);
  }

  void removeFromSelection(Collection<CanvasSelectable> selectables) {
    currentSelection.removeAll(selectables);
  }

  public void clearSelection() {
    currentSelection.clear();
  }

  public boolean isSelected(CanvasSelectable selectable) {
    return currentSelection.contains(selectable);
  }
}