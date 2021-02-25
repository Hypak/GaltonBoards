package uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics;

import org.joml.Vector2f;
import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.SelectBox;
import org.liquidengine.legui.component.event.selectbox.SelectBoxChangeSelectionEvent;
import org.liquidengine.legui.component.optional.align.HorizontalAlign;
import org.liquidengine.legui.component.optional.align.VerticalAlign;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.icon.CharIcon;
import org.liquidengine.legui.icon.Icon;
import org.liquidengine.legui.listener.EventListener;
import org.liquidengine.legui.listener.MouseClickEventListener;
import org.liquidengine.legui.style.border.SimpleLineBorder;
import org.liquidengine.legui.style.color.ColorConstants;
import org.liquidengine.legui.style.font.FontRegistry;
import org.lwjgl.opengl.GL;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class UserInterface {

  private final WindowBoards windowBoards;

  UserInterface(WindowBoards windowBoards) {
    this.windowBoards = windowBoards;
  }

  /**
   * Initialize OpenGL and all the windows
   * Then, run the main loop
   */
  public void start() {
    windowBoards.addComponent(makeButton(64, 32, 32, 0xF40A,
            (MouseClickEventListener) event -> windowBoards.getSimulation().run()));
    windowBoards.addComponent(makeButton(64, 128, 32, 0xF3E4,
            (MouseClickEventListener) event -> windowBoards.getSimulation().pause()));
    windowBoards.addComponent(makeButton(64, 224, 32, 0xF4DB,
            (MouseClickEventListener) event -> windowBoards.getSimulation().stop()));

    EventListener<SelectBoxChangeSelectionEvent<String>> selectEL = new EventListener<>() {
      @Override
      public void process(SelectBoxChangeSelectionEvent<String> event) {
        if (event.getNewValue().equals(event.getOldValue())) {
          return;
        }
        wb.getSimulation().stop();
        wb.getConfiguration().setConfiguration(event.getNewValue());
        wb.getSimulation().run();
      }
    };

    List<String> labels = new ArrayList(Configuration.savedConfigurations.keySet());
    wc.addComponent(makeSelectBox(128, 16, 96, 192, labels, selectEL));




    Button resetViewButton = new Button("Reset view", 80, 128, 160, 32);
    resetViewButton.getStyle().setBorder(new SimpleLineBorder(ColorConstants.black(), 1));
    resetViewButton.getListenerMap().addListener(MouseClickEvent.class, e -> windowBoards.getCamera().Reset());
    windowBoards.addComponent(resetViewButton);

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

  private static Button makeButton(int size, int xPos, int yPos, int iconCode, EventListener<MouseClickEvent> cb) {
    Icon iconRun = new CharIcon(new Vector2f(size, size), FontRegistry.MATERIAL_DESIGN_ICONS,
            (char) iconCode, ColorConstants.black());
    iconRun.setHorizontalAlign(HorizontalAlign.CENTER);
    iconRun.setVerticalAlign(VerticalAlign.MIDDLE);
    Button button = new Button("", xPos, yPos, size, size);
    button.getStyle().getBackground().setIcon(iconRun);
    button.getStyle().setBorder(new SimpleLineBorder(ColorConstants.black(), 1));
    button.getListenerMap().addListener(MouseClickEvent.class, cb);
    return button;
  }

  private static SelectBox<String> makeSelectBox (int width, int height, int xPos, int yPos,Iterable<String> labels,
                                                  EventListener<SelectBoxChangeSelectionEvent<String>> callback) {
    SelectBox<String> selectBox = new SelectBox<String>(xPos, yPos, width, height);
    selectBox.getStyle().setBorder(new SimpleLineBorder(ColorConstants.black(), 1));
    for (String label : labels) {
      selectBox.addElement(label);
    }
    selectBox.addSelectBoxChangeSelectionEventListener( callback);
    return selectBox;
  }

}

