package uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.gui;


import org.liquidengine.legui.component.*;
import org.liquidengine.legui.component.event.textinput.TextInputContentChangeEvent;
import org.liquidengine.legui.component.optional.align.HorizontalAlign;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.style.border.SimpleLineBorder;
import org.liquidengine.legui.style.color.ColorConstants;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.UserInterface;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.Configuration;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.Workspace;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom panel for the toolbar at the top of the window.
 * It includes multiple sub-panels, each providing a different functionality.
 */
public class TopPanel extends Panel {
  /**
   * Creates the TopPanel.
   *
   * @param xPos    The x coordinate of this panel's position.
   * @param yPos    The y coordinate of this panel's position.
   * @param width   The width of the panel.
   * @param height  The height of the panel.
   * @param size    The size of buttons in this panel.
   * @param spacing The spacing between buttons in this panel.
   */
  public TopPanel(int xPos, int yPos, int width, int height, int size, int spacing) {
    super(xPos, yPos, width, height);
    getStyle().getBackground().setColor(ColorConstants.gray());
    getStyle().setBorder(new SimpleLineBorder());

    add(new SimulationControls(0, 0, 200, height, size, spacing));
    add(new SetSimulationSpeed(175, 0, 200, height, size, spacing));
    add(new SetSpawnRate(285, 0, 200, height, size, spacing));
    add(new InsertBoards(425, 0, 200, height, size, spacing));
    add(new SelectConfiguration(650, 0, 200, height, size, spacing));
    add(new SavePanel(900, 0, 200, height, size, spacing));
  }

  /**
   * Sub-panel containing the simulation control buttons (i.e. play, pause and stop).
   */
  private static class SimulationControls extends Panel {
    SimulationControls(int xPos, int yPos, int width, int height, int size, int spacing) {
      super(xPos, yPos, width, height);
      getStyle().getBackground().setColor(ColorConstants.transparent());
      getStyle().getBorder().setEnabled(false);
      getStyle().getShadow().setColor(ColorConstants.transparent());
      add(new SimpleButton(spacing, spacing, size, 0xF40A,
          event -> UserInterface.userInterface.getWindowBoards().getSimulation().run()));
      add(new SimpleButton(2 * spacing + size, spacing, size, 0xF3E4,
          event -> UserInterface.userInterface.getWindowBoards().getSimulation().pause()));
      add(new SimpleButton(3 * spacing + 2 * size, spacing, size, 0xF4DB,
          event -> UserInterface.userInterface.getWindowBoards().getSimulation().stop()));
    }
  }

  /**
   * Sub-panel containing the buttons for the insertion of new boards in the workspace.
   */
  private static class InsertBoards extends Panel {
    InsertBoards(int xPos, int yPos, int width, int height, int size, int spacing) {
      super(xPos, yPos, width, height);
      getStyle().getBackground().setColor(ColorConstants.transparent());
      getStyle().getBorder().setEnabled(false);
      getStyle().getShadow().setColor(ColorConstants.transparent());
      add(new InsertBoardButton(spacing, spacing, size, size,
          "Bin", "Binomial", BinomialBoard.class));
      add(new InsertBoardButton(2 * spacing + size, spacing, size, size,
          "Gau", "Gaussian", GaussianBoard.class));
      add(new InsertBoardButton(3 * spacing + 2 * size, spacing, size, size,
          "Geo", "Geometric", GeometricBoard.class));
      add(new InsertBoardButton(4 * spacing + 3 * size, spacing, size, size,
          "Uni", "Uniform", UniformBoard.class));
    }

    /**
     * Helper class creating a button used for the addition of new classes
     */
    private static class InsertBoardButton extends Button {
      InsertBoardButton(int xPos, int yPos, int width, int height, String shortName, String longName,
                        Class<? extends Board> boardClass) {
        super(shortName, xPos, yPos, width, height);
        getStyle().setBorder(new SimpleLineBorder(ColorConstants.black(), 1));
        getListenerMap().addListener(MouseClickEvent.class, event -> {
          if (event.getAction().equals(MouseClickEvent.MouseClickAction.CLICK)) {
            try {
              UserInterface.userInterface.getConfiguration().addBoard(boardClass.getDeclaredConstructor().newInstance());
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        });
        setTooltip(new Tooltip("Add a " + longName + " board"));
        getTooltip().setPosition(0, height);
        getTooltip().getSize().set(128, 16);
        getTooltip().getStyle().setPadding(4f);
      }
    }
  }

  /**
   * Sub-panel containing the slider changing the simulation speed.
   */
  private static class SetSimulationSpeed extends SliderButtonsPanel {
    public SetSimulationSpeed(int xPos, int yPos, int width, int height, int size, int spacing) {
      super(xPos, yPos, width, height, size, spacing, "Simulation speed:",
          List.of(0.05f, 0.1f, 0.2f, 0.5f, 1f, 2f, 5f, 10f, 20f), value -> {
            Workspace.workspace.getSimulation().speed = value;
            return null;
          });
    }
  }

  /**
   * Sub-panel containing the slider changing the balls' spawn rate.
   */
  private static class SetSpawnRate extends SliderButtonsPanel {
    public SetSpawnRate(int xPos, int yPos, int width, int height, int size, int spacing) {
      super(xPos, yPos, width, height, size, spacing, "Spawn rate:",
          List.of(0.5f, 1f, 2f, 5f, 10f, 20f, 50f, 100f, 200f, 500f, 1000f, 2000f, 5000f), value -> {
            Workspace.workspace.getSimulation().timeBetweenBalls = 1 / value;
            Workspace.workspace.getSimulation().timeTillNextBall = 0;
            return null;
          });
    }
  }

  /**
   * Sub-panel containing the buttons for the choice of a new configuration.
   */
  private static class SelectConfiguration extends Panel {
    SelectConfiguration(int xPos, int yPos, int width, int height, int size, int spacing) {
      super(xPos, yPos, width, height);
      getStyle().getBackground().setColor(ColorConstants.transparent());
      getStyle().getBorder().setEnabled(false);
      getStyle().getShadow().setColor(ColorConstants.transparent());

      int halfSize = size / 2;

      Label selectBoxLabel = new Label("New simulation:", 0, spacing, width - 2 * spacing, halfSize);
      selectBoxLabel.getStyle().setHorizontalAlign(HorizontalAlign.CENTER);
      add(selectBoxLabel);

      add(new ConfigurationsSelectBox(spacing, spacing + halfSize, width - 2 * spacing, halfSize));
    }

    /**
     * Helper class creating a custom SelectBox.
     */
    private static class ConfigurationsSelectBox extends SelectBox<String> {
      ConfigurationsSelectBox(int xPos, int yPos, int width, int height) {
        super(xPos, yPos, width, height);
        getStyle().setBorder(new SimpleLineBorder(ColorConstants.black(), 1));
        ArrayList<String> names = new ArrayList<>(Configuration.savedConfigurations.keySet());
        for (int i = names.size() - 1; i >= 0; --i) {
          addElement(names.get(i));
        }
        addSelectBoxChangeSelectionEventListener(event -> {
          if (event.getNewValue().equals(event.getOldValue())) {
            return;
          }
          UserInterface.userInterface.getWindowBoards().getSimulation().stop();
          UserInterface.userInterface.getWindowBoards().getConfiguration().setConfiguration(event.getNewValue());
          Workspace.workspace.mouseHandler.getSelectionHandler().clearSelection();
        });
      }
    }
  }

  /**
   * Sub-panel containing the buttons for saving the current boards configuration.
   */
  static class SavePanel extends Panel {
    SavePanel(int xPos, int yPos, int width, int height, int size, int spacing) {
      super(xPos, yPos, width, height);
      final int textWidth = 100;
      getStyle().getBackground().setColor(ColorConstants.transparent());
      getStyle().getBorder().setEnabled(false);
      getStyle().getShadow().setColor(ColorConstants.transparent());
      add(new SaveButton(spacing, spacing, size, size, "Save"));
      add(new SaveTextInput(width - 2 * spacing - textWidth, spacing, textWidth, size));
    }

    static class SaveButton extends Button {
      public static String saveName = "untitled";
      SaveButton(int xPos, int yPos, int width, int height, String shortName) {
        super(shortName, xPos, yPos, width, height);
        getStyle().setBorder(new SimpleLineBorder(ColorConstants.black(), 1));
        getListenerMap().addListener(MouseClickEvent.class, event -> {
          if (event.getAction().equals(MouseClickEvent.MouseClickAction.CLICK)) {
            try {
              String finalName = "";
              if (Configuration.savedConfigurations.containsKey(saveName)) {
                for (Integer i = 1; i < 100000; ++i) {
                  finalName = saveName + i.toString();
                  if (!Configuration.savedConfigurations.containsKey(finalName)) {
                    Configuration.savedConfigurations.put(saveName, Workspace.workspace.getConfiguration());
                    UserInterface.userInterface.reloadPanels();
                    break;
                  }
                }
              } else {
                finalName = saveName;
              }
              Configuration.savedConfigurations.put(finalName, Workspace.workspace.getConfiguration());
              UserInterface.userInterface.reloadPanels();
            } catch (Exception e) {
              e.printStackTrace();
            }
          }
        });
        setTooltip(new Tooltip("Saves only work for the current session!"));
        getTooltip().setPosition(0, height);
        getTooltip().getSize().set(256, 32);
        getTooltip().getStyle().setPadding(4f);
      }
    }
    static class SaveTextInput extends TextInput {
      SaveTextInput(int xPos, int yPos, int width, int height) {
        super(xPos, yPos, width, height);
        getStyle().setBorder(new SimpleLineBorder(ColorConstants.black(), 1));
        getListenerMap().addListener(TextInputContentChangeEvent.class, event -> {
          SaveButton.saveName = event.getNewValue();
        });
      }
    }
  }

}

