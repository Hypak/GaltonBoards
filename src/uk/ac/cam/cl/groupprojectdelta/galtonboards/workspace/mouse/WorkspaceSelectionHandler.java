package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.mouse;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import org.liquidengine.legui.component.Label;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.UserInterface;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.BinomialBoard;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.Board;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.Peg;

public class WorkspaceSelectionHandler {
  private final List<WorkspaceSelectable> currentSelection = new LinkedList<>();

  public void addToSelection(WorkspaceSelectable selectable) {
    currentSelection.add(selectable);
    selectable.select();
    updatePanel();
  }

  public void addToSelection(Collection<WorkspaceSelectable> selectables) {
    currentSelection.addAll(selectables);
    for (WorkspaceSelectable selectable : selectables) {
      selectable.select();
    }
    updatePanel();
  }

  public void removeFromSelection(WorkspaceSelectable selectable) {
    currentSelection.remove(selectable);
    selectable.deselect();
    updatePanel();
  }

  void removeFromSelection(Collection<WorkspaceSelectable> selectables) {
    currentSelection.removeAll(selectables);
    for (WorkspaceSelectable selectable : selectables) {
      selectable.deselect();
    }
    updatePanel();
  }

  public void clearSelection() {
    currentSelection.clear();
    updatePanel();
  }

  public boolean isSelected(WorkspaceSelectable selectable) {
    return currentSelection.contains(selectable);
  }

  public List<WorkspaceSelectable> getSelection() {
    return currentSelection;
  }

  public Class<? extends WorkspaceSelectable> getSelectionType() {
    Class<? extends WorkspaceSelectable> lca_class;
    if (currentSelection.isEmpty()) {
      lca_class = WorkspaceSelectable.class;
    } else {
      lca_class = currentSelection.get(0).getClass();
      for (WorkspaceSelectable s : currentSelection) {
        Class<? extends WorkspaceSelectable> s_class = s.getClass();
        while (s_class != lca_class) {
          if (s_class.isAssignableFrom(lca_class)) {
            lca_class = s_class;
          } else if (lca_class.isAssignableFrom(s_class)) {
            s_class = lca_class;
          } else {
            if (WorkspaceSelectable.class.isAssignableFrom(s_class.getSuperclass())) {
              s_class = (Class<? extends WorkspaceSelectable>) s_class.getSuperclass();
            } else {
              lca_class = WorkspaceSelectable.class;
            }
          }
        }
      }
    }
    return lca_class;
  }

  private void updatePanel() {
    Class<? extends WorkspaceSelectable> selectionType = getSelectionType();
    String description = "";

    if (Peg.class.isAssignableFrom(selectionType)) {
      float probability = ((Peg) currentSelection.get(0)).rightProb();
      for (WorkspaceSelectable peg : currentSelection) {
        if (((Peg) peg).rightProb() != probability) {
          probability = -1f; //todo: change to some sort of null value
          break;
        }
      }

      UserInterface.userInterface.probabilitySlider.setValue(probability);

      description = "peg (x" + Integer.toString(currentSelection.size()) + ")";
    } else if (BinomialBoard.class.isAssignableFrom(selectionType)) {
      description = "binomial board (x" + Integer.toString(currentSelection.size()) + ")";
    } else if (Board.class.isAssignableFrom(selectionType)) {
      description = "board (x" + Integer.toString(currentSelection.size()) + ")";
    } else {
      description = selectionType.getName() + "(x" + Integer.toString(currentSelection.size()) + ")";
    }

    ((Label) UserInterface.userInterface.editPanel.getChildComponents().get(0)).getTextState().setText(description);
  }
}