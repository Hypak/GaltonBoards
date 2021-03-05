package uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.gui;


import org.liquidengine.legui.component.*;
import org.liquidengine.legui.component.optional.TextState;
import org.liquidengine.legui.component.optional.align.HorizontalAlign;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.style.border.SimpleLineBorder;
import org.liquidengine.legui.style.color.ColorConstants;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.UserInterface;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.Configuration;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.Workspace;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.*;

import java.util.List;
import java.util.function.Function;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class TopPanel extends Panel {
  public TopPanel(int xPos, int yPos, int width, int height, int size, int spacing) {
    super(xPos, yPos, width, height);
    getStyle().getBackground().setColor(ColorConstants.gray());
    getStyle().setBorder(new SimpleLineBorder());

    add(new SimulationControls(0, 0, 200, height, size, spacing));
    add(new SetSimulationSpeed(175, 0, 200, height, size, spacing));
    add(new SetSpawnRate(285, 0, 200, height, size, spacing));
    add(new InsertBoards(425, 0, 200, height, size, spacing));
    add(new SelectConfiguration(650, 0, 200, height, size, spacing));
  }

  // Buttons for play/pause/stop
  static class SimulationControls extends Panel {
    SimulationControls(int xPos, int yPos, int width, int height, int size, int spacing) {
      super(xPos, yPos, width, height);
      getStyle().getBackground().setColor(ColorConstants.transparent());
      getStyle().getBorder().setEnabled(false);
      getStyle().getShadow().setColor(ColorConstants.transparent());
      add(new SimpleButton(size, spacing, spacing, 0xF40A,
          event -> UserInterface.userInterface.getWindowBoards().getSimulation().run()));
      add(new SimpleButton(size, 2 * spacing + size, spacing, 0xF3E4,
          event -> UserInterface.userInterface.getWindowBoards().getSimulation().pause()));
      add(new SimpleButton(size, 3 * spacing + 2 * size, spacing, 0xF4DB,
          event -> UserInterface.userInterface.getWindowBoards().getSimulation().stop()));
    }
  }

  // Buttons for adding boards
  static class InsertBoards extends Panel {
    InsertBoards(int xPos, int yPos, int width, int height, int size, int spacing) {
      super(xPos, yPos, width, height);
      getStyle().getBackground().setColor(ColorConstants.transparent());
      getStyle().getBorder().setEnabled(false);
      getStyle().getShadow().setColor(ColorConstants.transparent());
      add(new InsertBoardButton(spacing, spacing, size, size, "Bin", "Binomial", BinomialBoard.class));
      add(new InsertBoardButton(2 * spacing + size, spacing, size, size, "Gau", "Gaussian", GaussianBoard.class));
      add(new InsertBoardButton(3 * spacing + 2 * size, spacing, size, size, "Geo", "Geometric", GeometricBoard.class));
      add(new InsertBoardButton(4 * spacing + 3 * size, spacing, size, size, "Uni", "Uniform", UniformBoard.class));
    }

    static class InsertBoardButton extends Button {
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

  static class SetSimulationSpeed extends SliderButtons {
    public SetSimulationSpeed(int xPos, int yPos, int width, int height, int size, int spacing) {
      super(xPos, yPos, width, height, size, spacing, "Simulation speed:",
          List.of(0.05f, 0.1f, 0.2f, 0.5f, 1f, 2f, 5f, 10f, 20f), value -> {
            Workspace.workspace.getSimulation().speed = value;
            return null;
          });
    }
  }

  static class SetSpawnRate extends SliderButtons {
    public SetSpawnRate(int xPos, int yPos, int width, int height, int size, int spacing) {
      super(xPos, yPos, width, height, size, spacing, "Spawn rate:",
          List.of(0.5f, 1f, 2f, 5f, 10f, 20f), value -> {
            Workspace.workspace.getSimulation().timeBetweenBalls = 1 / value / value;
            Workspace.workspace.getSimulation().timeTillNextBall = 0;
            return null;
          });
    }
  }

  // Buttons for play/pause/stop
  static class SelectConfiguration extends Panel {
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

    static class ConfigurationsSelectBox extends SelectBox<String> {
      ConfigurationsSelectBox(int xPos, int yPos, int width, int height) {
        super(xPos, yPos, width, height);
        getStyle().setBorder(new SimpleLineBorder(ColorConstants.black(), 1));
        for (String label : Configuration.savedConfigurations.keySet()) {
          addElement(label);
        }
        addSelectBoxChangeSelectionEventListener(event -> {
          if (event.getNewValue().equals(event.getOldValue())) {
            return;
          }
          UserInterface.userInterface.getWindowBoards().getSimulation().stop();
          UserInterface.userInterface.getWindowBoards().getConfiguration().setConfiguration(event.getNewValue());
          UserInterface.userInterface.getWindowBoards().getSimulation().run();
        });
      }
    }
  }

  private static class SliderButtons extends Panel {

    private int currentIndex;
    List<Float> values;
    Function<Float, Void> setCallback;
    Label simulationSpeedLabelValue;

    public SliderButtons(int xPos, int yPos, int width, int height, int size, int spacing, String label,
                         List<Float> values, Function<Float, Void> setCallback) {
      super(xPos, yPos, width, height);
      getStyle().getBackground().setColor(ColorConstants.transparent());
      getStyle().getBorder().setEnabled(false);
      getStyle().getShadow().setColor(ColorConstants.transparent());

      this.values = values;
      this.currentIndex = values.size() / 2;
      this.setCallback = setCallback;

      int halfSize = size / 2;

      Label simulationSpeedLabel = new Label(label, spacing, spacing, halfSize * 5, halfSize);
      simulationSpeedLabel.getStyle().setHorizontalAlign(HorizontalAlign.CENTER);
      add(simulationSpeedLabel);

      simulationSpeedLabelValue = new Label(values.get(currentIndex) + "x", spacing + 2 * halfSize,
          spacing + halfSize, halfSize, halfSize);
      simulationSpeedLabelValue.getStyle().setHorizontalAlign(HorizontalAlign.CENTER);
      add(simulationSpeedLabelValue);

      add(new SimpleButton(halfSize, spacing, spacing + halfSize, 0xF374, mouseClickEvent -> {
        if (mouseClickEvent.getAction().equals(MouseClickEvent.MouseClickAction.RELEASE)) {
          currentIndex = max(currentIndex - 1, 0);
          setCallback.apply(values.get(currentIndex));
          simulationSpeedLabelValue.setTextState(new TextState(values.get(currentIndex) + "x"));
        }
      }));
      add(new SimpleButton(halfSize, spacing + 4 * halfSize, spacing + halfSize, 0xF415, mouseClickEvent -> {
        if (mouseClickEvent.getAction().equals(MouseClickEvent.MouseClickAction.RELEASE)) {
          currentIndex = min(currentIndex + 1, values.size() - 1);
          setCallback.apply(values.get(currentIndex));
          simulationSpeedLabelValue.setTextState(new TextState(values.get(currentIndex) + "x"));
        }
      }));
    }
  }

}

