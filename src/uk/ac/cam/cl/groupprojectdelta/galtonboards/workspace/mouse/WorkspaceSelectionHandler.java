package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.mouse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.Panel;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.UserInterface;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.panel.PanelFloatSliderOption;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.panel.PanelLabel;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.panel.PanelOption;
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
    List<PanelOption> panelOptions = new ArrayList<>();

    if (Peg.class.isAssignableFrom(selectionType)) {
      panelOptions.add(new PanelLabel("PEG PROPERTIES"));
      panelOptions.add(new PanelFloatSliderOption() {
        @Override
        public Float getValue() {
          Float probability = ((Peg) currentSelection.get(0)).rightProb();
          for (WorkspaceSelectable peg : currentSelection) {
            if (((Peg) peg).rightProb() != probability) {
              probability = null;
              break;
            }
          }
          return probability;
        }

        @Override
        public void setValue(float value) {
          for (WorkspaceSelectable peg : currentSelection) {
            ((Peg) peg).setProbability(1 - value);
          }
        }

        @Override
        public String getName() {
          return "Peg probability";
        }
      });
    } else if (Board.class.isAssignableFrom(selectionType)) {
      panelOptions.add(new PanelLabel("PEG PROPERTIES"));

      panelOptions.add(new PanelFloatSliderOption() {
        @Override
        public Float getValue() {
          Board firstBoard = (Board) currentSelection.get(0);
          Float probability = firstBoard.getRootPeg().rightProb();

           for (WorkspaceSelectable board : currentSelection) {
             for (Peg peg : ((Board) board).getPegs()) {
               if (peg.rightProb() != probability) {
                 probability = null;
                 break;
               }
             }
           }
           return probability;
        }

        @Override
        public void setValue(float value) {
          for (WorkspaceSelectable board : currentSelection) {
            for (Peg peg : ((Board) board).getPegs()) {
              peg.setProbability(1-value);
            }
          }
        }

        @Override
        public String getName() {
          return "Peg probability";
        }
      });

      panelOptions.add(new PanelLabel("BOARD PROPERTIES"));
    }

    UserInterface.userInterface.updateEditPanel(panelOptions);

    //((Label) UserInterface.userInterface.editPanel.getChildComponents().get(0)).getTextState().setText(description);
  }
}