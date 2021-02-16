package uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.liquidengine.legui.DefaultInitializer;
import org.liquidengine.legui.animation.AnimatorProvider;
import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Frame;
import org.liquidengine.legui.event.CursorEnterEvent;
import org.liquidengine.legui.listener.CursorEnterEventListener;
import org.liquidengine.legui.listener.processor.EventProcessorProvider;
import org.liquidengine.legui.style.border.SimpleLineBorder;
import org.liquidengine.legui.style.color.ColorConstants;
import org.liquidengine.legui.system.layout.LayoutManager;
import org.lwjgl.opengl.GL;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.UserInput;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.Board;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.Configuration;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.Workspace;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Vector;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
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

