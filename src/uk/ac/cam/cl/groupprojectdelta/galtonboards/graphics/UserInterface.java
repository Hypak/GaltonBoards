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

  private final Window windowBoards;
  private final Window windowControls;
  private boolean running = false;

  UserInterface(Window windowBoards, Window windowControls) {
    this.windowBoards = windowBoards;
    this.windowControls = windowControls;
  }

  /**
   * Initialize OpenGL and all the windows
   * Then, run the main loop
   * @param wb : WindowBoards
   * @param wc : WindowControls
   */
  public void start(WindowBoards wb, WindowControls wc) {
    wc.addComponent(makeButton(64, 32, 32, 0xF40A,
            (MouseClickEventListener) event -> wb.getSimulation().run()));
    wc.addComponent(makeButton(64, 128, 32, 0xF3E4,
            (MouseClickEventListener) event -> wb.getSimulation().pause()));
    wc.addComponent(makeButton(64, 224, 32, 0xF4DB,
            (MouseClickEventListener) event -> wb.getSimulation().stop()));

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
    resetViewButton.getListenerMap().addListener(MouseClickEvent.class, e -> wb.getCamera().Reset());
    wc.addComponent(resetViewButton);

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

    // Initialize window for controls
    long windowControlsID = glfwCreateWindow(windowControls.getWidth(), windowControls.getHeight(), "Controls", NULL, NULL);
    if (windowControlsID == NULL) throw new RuntimeException("Failed to create the GLFW window");
    glfwShowWindow(windowControlsID);
    glfwMakeContextCurrent(windowControlsID);
    GL.createCapabilities();
    windowControls.initialize(windowControlsID);


    // Main loop
    running = true;
    while (running) {
      if (glfwWindowShouldClose(windowBoardsID) || glfwWindowShouldClose(windowControlsID)) {
        running = false;
        break;
      }

      glfwMakeContextCurrent(windowBoardsID);
      GL.getCapabilities();
      windowBoards.loop(windowBoardsID);

      glfwMakeContextCurrent(windowControlsID);
      GL.getCapabilities();
      windowControls.loop(windowControlsID);
    }

    // Destroy windows
    windowBoards.destroy(windowBoardsID);
    windowControls.destroy(windowControlsID);
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

