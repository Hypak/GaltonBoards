package uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics;

import org.lwjgl.opengl.GL;
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
   */
  public void start() {
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

}

