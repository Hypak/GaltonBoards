package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.mouse;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class WorkspaceSelectionHandler {
  private static WorkspaceSelectionHandler selectionHandler;
  private final List<WorkspaceSelectable> currentSelection = new LinkedList<>();

  // Singleton Pattern
  public static WorkspaceSelectionHandler getSelectionHandler() {
    if (selectionHandler == null) {
      selectionHandler = new WorkspaceSelectionHandler();
    }
    return selectionHandler;
  }

  public void addToSelection(WorkspaceSelectable selectable) {
    currentSelection.add(selectable);
    selectable.select();
  }

  public void addToSelection(Collection<WorkspaceSelectable> selectables) {
    currentSelection.addAll(selectables);
    for (WorkspaceSelectable selectable : selectables) {
      selectable.select();
    }
  }

  public void removeFromSelection(WorkspaceSelectable selectable) {
    currentSelection.remove(selectable);
    selectable.deselect();
  }

  void removeFromSelection(Collection<WorkspaceSelectable> selectables) {
    currentSelection.removeAll(selectables);
    for (WorkspaceSelectable selectable : selectables) {
      selectable.deselect();
    }
  }

  public void clearSelection() {
    currentSelection.clear();
  }

  public boolean isSelected(WorkspaceSelectable selectable) {
    return currentSelection.contains(selectable);
  }
}