package uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.listener.MouseClickEventListener;
import org.liquidengine.legui.style.border.SimpleLineBorder;
import org.liquidengine.legui.style.color.ColorConstants;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.UserInput;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.Board;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.Configuration;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.Simulation;

import java.io.IOException;
import java.nio.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.Workspace;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {

  private long window;
  private int programID;

  private int vertexBuffer;
  private int uvBuffer;

  private Camera camera = new Camera();
  private UserInput userInput;

  private int mvpShaderLocation;

  private Workspace workspace = new Workspace();

  float currentTime;

  public Main() {
    Configuration configuration = workspace.getConfiguration();
    Board board1 = configuration.getStartBoard();

    // Add two more boards (other than default)
    Board board2 = new Board(3);
    Board board3 = new Board(3);
    board1.getBucket(0).setOutput(board2);
    board1.getBucket(1).setOutput(board3);
    board2.updateBoardPosition(new Vector2f(3, -15));
    board3.updateBoardPosition(new Vector2f(-2, -15));
    configuration.addBoard(board2);
    configuration.addBoard(board3);

    //Merge a couple buckets
    //board1.startDraggingBucket(board1.getBucket(2));
    //board1.edgeExtendedRight(false);
    //board1.edgeExtendedRight(false);
    //board1.confirmDraggingBucket();
  }

  public void run() {
    System.out.println("Galton Boards! Using LWJGL " + Version.getVersion() + "!");

    try {
      init();
    } catch (IOException e) {
      System.err.println(e.getMessage());
      return;
    }
    loop();

    glfwFreeCallbacks(window);
    glfwDestroyWindow(window);

    glfwTerminate();
    glfwSetErrorCallback(null).free();
  }

  private void init() throws IOException {

    // Setup an error callback. The default implementation
    // will print the error message in System.err.
    GLFWErrorCallback.createPrint(System.err).set();

    if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");

    glfwDefaultWindowHints();
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

    // set OpenGL version number
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);

    // todo: default window dimensions
    window = glfwCreateWindow(1000, 1000, "Galton Boards", NULL, NULL);
    if (window == NULL) throw new RuntimeException("Failed to create the GLFW window");
    userInput = new UserInput(window, camera);

    // Setup a key callback. It will be called every time a key is pressed, repeated or released.
    glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
      // todo: remove this exit condition
      if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
        glfwSetWindowShouldClose(window, true);
    });

    //TODO PUT THIS SOMEWEHRE SENSIBLE - MATT

    //GLFW.glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {
      // todo: actually convert from screen-space to world-space
      // so essentially we're going to be generating a ray cast and then solving that as a system of linear
      // equations along with the boundary condition z = 1. Might be able to take a shortcut (i.e. no need to
      // matrix inversion, could just linearly scale matrix.

      //workspace.mouseMove(new Vector2f((float) xpos / 200, (float) ypos / 200));

    //});

    glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
      System.out.println("mouse pressed: " + (button == GLFW_MOUSE_BUTTON_RIGHT ? "right" : "something else"));
      if (button == GLFW_MOUSE_BUTTON_LEFT) {
        if (action == GLFW_PRESS) {
          workspace.mouseDown(currentTime);
        } else { // the only other action is GLFW_RELEASE
          workspace.mouseUp(currentTime);
        }
      }
    });

    //-------------------------------------

    // centering window position on screen - requires finer memory control than typical java
    try ( MemoryStack stack = stackPush() ) {
      IntBuffer pWidth = stack.mallocInt(1); // int*
      IntBuffer pHeight = stack.mallocInt(1); // int*

      glfwGetWindowSize(window, pWidth, pHeight);

      // Get the resolution of the primary monitor
      GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

      assert vidmode != null;
      glfwSetWindowPos(
          window,
          (vidmode.width() - pWidth.get(0)) / 2,
          (vidmode.height() - pHeight.get(0)) / 2
      );
    } // the stack frame is popped automatically

    glfwMakeContextCurrent(window);
    glfwSwapInterval(1); // enable v-sync

    glfwShowWindow(window);

    GL.createCapabilities();

    System.out.println("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));

    try {
      programID = LoadShaders(
              "resources/shaders/vertexShader.glsl",
              "resources/shaders/fragmentShader.glsl"
      );
    } catch (IOException e) {
      System.err.println("Unable to load the shaders: " + e.getMessage());
    }

    glUseProgram(programID);

    // enable transparency for wireframe
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    glDisable(GL_CULL_FACE);

    mvpShaderLocation = glGetUniformLocation(programID, "MVP");

    vertexBuffer = glGenBuffers();
    uvBuffer = glGenBuffers();
    //indexBuffer = glGenBuffers(); todo: potentially add later

    loadTexture("resources/textures/texture.png");

    currentTime = (float) glfwGetTime();

    final float[] CLEAR_COLOUR = {0.5f, 0.5f, 0.5f, 1};
    glClearColor(CLEAR_COLOUR[0], CLEAR_COLOUR[1], CLEAR_COLOUR[2], CLEAR_COLOUR[3]);
  }

  private void loop() {

    float lastTime, deltaTime;
    Matrix4f MVP = new Matrix4f();
    int vao;

    int[] indexBuffer = new int[]{0, 1, 2, 3, 4, 5};

    List<Float> auxList;
    float[] mesh, UVs;

     // mouse things
    final DoubleBuffer mouseX = BufferUtils.createDoubleBuffer(1);
    final DoubleBuffer mouseY = BufferUtils.createDoubleBuffer(1);
    Vector2f mousePos = new Vector2f();
    Matrix4f MVPInverse = new Matrix4f();
    Vector4f mouseRayCast = new Vector4f();

    while ( !glfwWindowShouldClose(window) ) {
      glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

      lastTime = currentTime;
      currentTime = (float) glfwGetTime();
      deltaTime = (currentTime - lastTime);
      userInput.update(deltaTime);
      workspace.update(deltaTime);

      int[] windowWidth = new int[1];
      int[] windowHeight = new int[1];
      glfwGetWindowSize(window, windowWidth, windowHeight);
      // todo: orthographic rather than perspective
      Matrix4f projection = new Matrix4f().perspective((float) Math.toRadians(45.0f),
              (float) windowWidth[0] / (float) windowHeight[0],
              0.1f,
              100.0f);
      final float size = 20f;
      /*projection = new Matrix4f().ortho(
              camera.getPosition().x + size,
              camera.getPosition().x - size,
              camera.getPosition().y - size,
              camera.getPosition().y + size,
              0.1f,
              100.0f
      );*/
      projection.mul(camera.viewMatrix(), MVP);

      glUniformMatrix4fv(mvpShaderLocation, false, MVP.get(new float[16]));


      mouseRayCast.x = (float)mouseX.get(0);
      mouseRayCast.y = (float)mouseY.get(0);
      mouseRayCast.z = 1;
      mouseRayCast.w = 1;
      MVP.invert(MVPInverse);
      mouseRayCast.mul(MVPInverse);
      mouseRayCast.normalize();
      mousePos.x = mouseRayCast.x;
      mousePos.y = mouseRayCast.y;
      workspace.mouseMove(mousePos);


      vao = glGenVertexArrays();
      glBindVertexArray(vao);

      auxList = workspace.getMesh(currentTime);
      mesh = new float[auxList.size()];
      for (int i = 0; i < auxList.size(); i++)
        mesh[i] = auxList.get(i);

      auxList = workspace.getUV();
      UVs = new float[auxList.size()];
      for (int i = 0; i < auxList.size(); i++)
        UVs[i] = auxList.get(i);

      glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
      glBufferData(GL_ARRAY_BUFFER, mesh, GL_STATIC_DRAW);
      glVertexAttribPointer(0, 3, GL_FLOAT, false,0, 0);
      glEnableVertexAttribArray(0);

      glBindBuffer(GL_ARRAY_BUFFER, uvBuffer);
      glBufferData(GL_ARRAY_BUFFER, UVs, GL_STATIC_DRAW);
      glVertexAttribPointer(1, 2, GL_FLOAT, false,0, 0);
      glEnableVertexAttribArray(1);

      glDrawArrays(
              GL_TRIANGLES,
              0,
              mesh.length / 3
      );

      glfwSwapBuffers(window);

      glfwPollEvents();
    }
  }

  static int LoadShaders(String vertex_file_path, String fragment_file_path) throws IOException {

    int VertexShaderID = glCreateShader(GL_VERTEX_SHADER);
    int FragmentShaderID = glCreateShader(GL_FRAGMENT_SHADER);

    String VertexShaderCode = new String(Files.readAllBytes(Path.of(vertex_file_path)));
    String FragmentShaderCode = new String(Files.readAllBytes(Path.of(fragment_file_path)));

    int InfoLogLength;

    glShaderSource(VertexShaderID, VertexShaderCode);
    glCompileShader(VertexShaderID);

    // Check Vertex Shader
    glGetShaderi(VertexShaderID, GL_COMPILE_STATUS);
    InfoLogLength = glGetShaderi(VertexShaderID, GL_INFO_LOG_LENGTH);
    if ( InfoLogLength > 0 ){
      System.err.println(glGetShaderInfoLog(VertexShaderID));
    }

    glShaderSource(FragmentShaderID, FragmentShaderCode);
    glCompileShader(FragmentShaderID);

    // Check Fragment Shader
    glGetShaderi(FragmentShaderID, GL_COMPILE_STATUS);
    InfoLogLength = glGetShaderi(FragmentShaderID, GL_INFO_LOG_LENGTH);
    if ( InfoLogLength > 0 ){
      System.err.println(glGetShaderInfoLog(FragmentShaderID));
    }

    // Link the program
    int ProgramID = glCreateProgram();
    glAttachShader(ProgramID, VertexShaderID);
    glAttachShader(ProgramID, FragmentShaderID);
    glLinkProgram(ProgramID);

    // Check the program
    glGetProgrami(ProgramID, GL_LINK_STATUS);
    InfoLogLength = glGetProgrami(ProgramID, GL_INFO_LOG_LENGTH);
    if ( InfoLogLength > 0 ){
      System.err.println(glGetProgramInfoLog(ProgramID));
    }

    glDetachShader(ProgramID, VertexShaderID);
    glDetachShader(ProgramID, FragmentShaderID);

    glDeleteShader(VertexShaderID);
    glDeleteShader(FragmentShaderID);
    return ProgramID;
  }

  private int loadTexture(String path) {

    int textureID;

    TextureLoader tex = new TextureLoader(path);

    textureID = glGenTextures();

    glEnable(GL_TEXTURE_2D);

    // "Bind" the newly created texture : all future texture functions will modify this texture
    glBindTexture(GL_TEXTURE_2D, textureID);

    // Give the image to OpenGL
    glTexImage2D(GL_TEXTURE_2D, 0,GL_RGBA, tex.getWidth(), tex.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, tex.buffer());

    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

    return textureID;
  }

  public static void main(String[] args) {
    // Create windows
    WindowBoards wb = new WindowBoards(1000, 1000,
        "resources/shaders/vertexShader.glsl",
        "resources/shaders/fragmentShader.glsl",
        "resources/textures/texture.png"
    );

    // Set up Boards window by adding boards
    Configuration configuration = wb.getConfiguration();
    Board board1 = configuration.getStartBoard();
    Board board2 = new Board(3);
    Board board3 = new Board(3);
    board1.getBucket(0).setOutput(board2);
    board1.getBucket(1).setOutput(board3);
    board2.updateBoardPosition(new Vector2f(3, -15));
    board3.updateBoardPosition(new Vector2f(-2, -15));
    configuration.addBoard(board2);
    configuration.addBoard(board3);

    // Set up Controls window by adding a simple button
    Button button = new Button("Galton Boards", 25, 25, 200, 30);
    button.getStyle().setBorder(new SimpleLineBorder(ColorConstants.black(), 1));
    button.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener)  System.out::println);
    wb.addComponent(button);

    // Add button to reset view
    Button buttonReset = new Button("Reset view", 25, 80, 200, 30);
    buttonReset.getStyle().setBorder(new SimpleLineBorder(ColorConstants.black(), 1));
    buttonReset.getListenerMap().addListener(MouseClickEvent.class,
        (MouseClickEventListener) event -> wb.getCamera().Reset());
    wb.addComponent(buttonReset);

    // Start the interface
    new UserInterface(wb).start();
  }

}