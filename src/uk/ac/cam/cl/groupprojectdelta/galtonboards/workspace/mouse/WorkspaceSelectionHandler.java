package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.mouse;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.Panel;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.UserInterface;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.panel.*;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.Configuration;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.LogicalLocation;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.Simulation;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.Workspace;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.BinomialBoard;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.Board;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.Bucket;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.Distribution;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.GaussianBoard;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.GeometricBoard;
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
      panelOptions.add(new PanelLabel("PEG PROPERTIES:"));

      panelOptions.add(new PanelFloatSliderOption() {
        @Override
        public Float getValue() {
          Board firstBoard = (Board) currentSelection.get(0);
          Float probability = firstBoard.getRootPeg().rightProb();

           for (WorkspaceSelectable board : currentSelection) {
             for (Peg peg : ((Board) board).getPegs()) {
               if (probability != null) {
                 if (peg.rightProb() != probability) {
                   probability = null;
                   break;
                 }
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

      panelOptions.add(new PanelButtonOption() {

        @Override
        public String getLabel() {
          return "Delete Board";
        }

        @Override
        public void click() {
          Board firstBoard = (Board) currentSelection.get(0);
          Workspace.workspace.getConfiguration().removeBoard(firstBoard);
        }

        @Override
        public String getName() {
          return "Delete Board";
        }
      });

      panelOptions.add(new PanelButtonOption() {

        @Override
        public String getLabel() {
          return "Mark Board as Root";
        }

        @Override
        public void click() {
          Board firstBoard = (Board) currentSelection.get(0);
          Workspace.workspace.getConfiguration().setStartBoard(firstBoard);
        }

        @Override
        public String getName() {
          return "Mark Board as Root";
        }
      });

      panelOptions.add(new PanelLabel("BOARD PROPERTIES:"));
      panelOptions.add(new PanelLabel(((Board)currentSelection.get(0)).toString()));
      panelOptions.add(new PanelBoardTypeOption() {
        @Override
        public Distribution getDistribution() {
          Distribution firstDistribution = ((Board) currentSelection.get(0)).getDistribution();
          for (WorkspaceSelectable board : currentSelection) {
            if (((Board) board).getDistribution() != firstDistribution) {
              firstDistribution = Distribution.Custom;
              break;
            }
          }
          return firstDistribution;
        }

        @Override
        public void setDistribution(Distribution distribution) {
          System.out.println(distribution);
          if (distribution == Distribution.Custom) {
            for (WorkspaceSelectable board : currentSelection) {
              ((Board) board).reset();
            }
          } else {
            for (WorkspaceSelectable board : currentSelection) {
              ((Board) board).changeBoard(distribution);
            }
          }
        }

        @Override
        public String getName() {
          return "Use a preset distribution";
        }
      });

      if (BinomialBoard.class.isAssignableFrom(selectionType)) {
        panelOptions.add(new PanelLabel("BINOMIAL BOARD PROPERTIES"));
        panelOptions.add(new PanelFloatSliderOption() {
          @Override
          public Float getValue() {
            Float probability = ((BinomialBoard) currentSelection.get(0)).getProbPerTrial();
            for (WorkspaceSelectable board : currentSelection) {
              if (((BinomialBoard) board).getProbPerTrial() != probability) {
                probability = null;
                break;
              }
            }
            return probability;
          }

          @Override
          public void setValue(float value) {
            for (WorkspaceSelectable board : currentSelection) {
              ((BinomialBoard) board).setProbPerTrial(value);
            }
          }

          @Override
          public String getName() {
            return "Probability";
          }
        });
      } else if (GaussianBoard.class.isAssignableFrom(selectionType)) {

      } else if (GeometricBoard.class.isAssignableFrom(selectionType)) {
        panelOptions.add(new PanelLabel("GEOMETRIC BOARD PROPERTIES"));
        panelOptions.add(new PanelFloatSliderOption() {
          @Override
          public Float getValue() {
            Float probability = ((GeometricBoard) currentSelection.get(0)).getProbPerTrial();
            for (WorkspaceSelectable board : currentSelection) {
              if (((GeometricBoard) board).getProbPerTrial() != probability) {
                probability = null;
                break;
              }
            }
            return probability;
          }

          @Override
          public void setValue(float value) {
            for (WorkspaceSelectable board : currentSelection) {
              ((GeometricBoard) board).setProbPerTrial(value);
            }
          }

          @Override
          public String getName() {
            return "Probability";
          }
        });
      } else if (GeometricBoard.class.isAssignableFrom(selectionType)) {

      }

    } else if (Bucket.class.isAssignableFrom(selectionType)) {
      panelOptions.add(new PanelLabel("BUCKET PROPERTIES"));
      panelOptions.add(new PanelTagOption() {
        @Override
        public String getName() {
          return "Bucket tag";
        }

        @Override
        public List<String> getTags() {
          List<String> returnTags = ((LogicalLocation) currentSelection.get(0)).getGivenTags();
          for (WorkspaceSelectable bucket : currentSelection) {
            List<String> bucketTags = ((Bucket) bucket).getGivenTags();
            if (!bucketTags.containsAll(returnTags) || !returnTags.containsAll(bucketTags)) {
              returnTags = new LinkedList<>();
            }
          }
          return returnTags;
        }

        @Override
        public void setTags(List<String> tags) {
          for (WorkspaceSelectable bucket : currentSelection) {
            ((Bucket) bucket).clearGivenTags();
            ((Bucket) bucket).setGivenTags(tags);
          }
        }

        @Override
        public void clearTags() {
          for (WorkspaceSelectable bucket : currentSelection) {
            ((Bucket) bucket).clearGivenTags();
          }
        }
      });
    } else {
      panelOptions.add(new PanelLabel(selectionType.getSimpleName()));
    }

    System.out.println(currentSelection.toString());

    UserInterface.userInterface.updateEditPanel(panelOptions);
  }
}