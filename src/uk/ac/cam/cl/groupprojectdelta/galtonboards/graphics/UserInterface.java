package uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics;

import java.util.Objects;

import org.liquidengine.legui.component.*;
import org.liquidengine.legui.component.event.slider.SliderChangeValueEvent;
import org.liquidengine.legui.component.event.slider.SliderChangeValueEventListener;
import org.liquidengine.legui.component.optional.TextState;
import org.liquidengine.legui.style.border.SimpleLineBorder;
import org.liquidengine.legui.style.color.ColorConstants;
import org.lwjgl.opengl.GL;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.gui.MainPanel;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.gui.TopPanel;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.panel.PanelFloatSliderOption;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.panel.PanelLabel;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.panel.PanelOption;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.Configuration;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.Workspace;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.*;

import java.util.List;

import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.mouse.WorkspaceSelectable;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.mouse.WorkspaceSelectionHandler;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class UserInterface {
  public static UserInterface userInterface;
  public Panel editPanel;
  public Slider probabilitySlider;

  private final WindowBoards windowBoards;

  public Configuration getConfiguration() {
    return windowBoards.getConfiguration();
  }

  public WindowBoards getWindowBoards() {
    return windowBoards;
  }

  public WorkspaceSelectionHandler getSelectionHandler() {
    return windowBoards.getSelectionHandler();
  }

  UserInterface(WindowBoards windowBoards) {
    this.windowBoards = windowBoards;
  }

  /**
   * Initialize OpenGL and all the windows
   * Then, run the main loop
   */
  public void start() {
    final int editPanelWidth = 400;
    final int topPanelHeight = 48;

    // Panels for boards and UI sections
    editPanel = getEditPanel(editPanelWidth);
    windowBoards.addComponent(editPanel);
    windowBoards.addComponent(new TopPanel(0, 0, windowBoards.getWidth(), topPanelHeight, 36, 6));
    windowBoards.addComponent(new MainPanel(0, topPanelHeight, windowBoards.getWidth() - editPanelWidth,
                                            windowBoards.getHeight() - topPanelHeight, 24, 4));

    System.setProperty("joml.nounsafe", Boolean.TRUE.toString());
    System.setProperty("java.awt.headless", Boolean.TRUE.toString());
    // Initialize OpenGL
    if (!org.lwjgl.glfw.GLFW.glfwInit()) {
      throw new RuntimeException("Can't initialize GLFW");
    }

    // Set window hints
    glfwDefaultWindowHints();
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);

    // Initialize window for the boards
    long windowBoardsID = glfwCreateWindow(windowBoards.getWidth(), windowBoards.getHeight(), "Galton Boards", NULL, NULL);
    if (windowBoardsID == NULL) throw new RuntimeException("Failed to create the GLFW window for the boards");
    glfwShowWindow(windowBoardsID);
    glfwMakeContextCurrent(windowBoardsID);
    GL.createCapabilities();
    windowBoards.initialize(windowBoardsID);

    // Main loop
    while (!glfwWindowShouldClose(windowBoardsID)) {
      windowBoards.loop(windowBoardsID);
    }

    // Destroy windows
    windowBoards.destroy(windowBoardsID);
  }

  public void speedSliderChangeEvent(SliderChangeValueEvent<Slider> event) {
    Workspace.workspace.getSimulation().speed = event.getNewValue();
  }

  public void spawnSliderChangeEvent(SliderChangeValueEvent<Slider> event) {
    Workspace.workspace.getSimulation().timeBetweenBalls = 1 / event.getNewValue() / event.getNewValue();
    Workspace.workspace.getSimulation().timeTillNextBall *= (event.getOldValue() * event.getOldValue());
    Workspace.workspace.getSimulation().timeTillNextBall /= (event.getNewValue() * event.getNewValue());
  }

  private Panel getEditPanel(int editPanelWidth) {
    Panel editPanel = new Panel(windowBoards.getWidth() - editPanelWidth, 0, editPanelWidth, windowBoards.getHeight());
    editPanel.getStyle().getBackground().setColor(ColorConstants.lightGray());
    editPanel.getStyle().setBorder(new SimpleLineBorder());

    Label selectedLabel = new Label(100, 50, 200, 100);
    selectedLabel.setTextState(new TextState("Test"));
    editPanel.add(selectedLabel);

    probabilitySlider = new Slider(100, 150, 200, 30);
    probabilitySlider.setMaxValue(1);
    probabilitySlider.getListenerMap().addListener(SliderChangeValueEvent.class, this::sliderChangeEvent);
    editPanel.add(probabilitySlider);

    return editPanel;
  }

  public void updateEditPanel(List<PanelOption> panelOptions) {
    int current_y = 50;
    editPanel.clearChildComponents();

    for (PanelOption panelOption : panelOptions) {
      if (panelOption instanceof PanelLabel) {
        Label label = new Label(100, current_y, 200, 100);
        label.setTextState(new TextState(panelOption.getName()));
        editPanel.add(label);

        current_y += 50;
      } else if (panelOption instanceof PanelFloatSliderOption) {
        Float value = ((PanelFloatSliderOption) panelOption).getValue();

        Label label = new Label(100, current_y, 200, 100);
        label.setTextState(new TextState(panelOption.getName() + " ( " + value + ")"));
        editPanel.add(label);

        Slider newSlider = new Slider(100, current_y + 50, 200, 30);
        newSlider.setMaxValue(((PanelFloatSliderOption) panelOption).getMax());
        newSlider.setMinValue(((PanelFloatSliderOption) panelOption).getMin());
        newSlider.setValue(Objects.requireNonNullElse(value, 0.5f));
        newSlider.getListenerMap().addListener(SliderChangeValueEvent.class,
            (SliderChangeValueEventListener) sliderChangeValueEvent -> {
              ((PanelFloatSliderOption) panelOption).setValue(sliderChangeValueEvent.getNewValue());
              label.getTextState().setText(panelOption.getName() + " ( " + sliderChangeValueEvent.getNewValue() + ")");
            });
        editPanel.add(newSlider);

        current_y += 100;
      }
    }
  }

  public void sliderChangeEvent(SliderChangeValueEvent<Slider> event) {
    WorkspaceSelectionHandler selectionHandler = getSelectionHandler();
    if (Peg.class.isAssignableFrom(selectionHandler.getSelectionType())) {
      for (WorkspaceSelectable peg : selectionHandler.getSelection()) {
        ((Peg) peg).setProbability(1 - event.getNewValue());
      }
    }
  }
}
