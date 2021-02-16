package uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics;


import org.joml.Matrix4f;
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
import org.liquidengine.legui.system.context.Context;
import org.liquidengine.legui.system.layout.LayoutManager;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.UserInput;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.Workspace;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Vector;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;

class UserInterfaceTest {

  public static void main(String[] args) throws IOException {
    System.setProperty("joml.nounsafe", Boolean.TRUE.toString());
    System.setProperty("java.awt.headless", Boolean.TRUE.toString());

    UserInterfaceTest UIT = new UserInterfaceTest(1000, 1000,
        "resources/shaders/vertexShader.glsl",
        "resources/shaders/fragmentShader.glsl",
        "resources/textures/texture.png"
    );

    Button button = new Button("Galton Boards", 20, 20, 160, 30);
    SimpleLineBorder border = new SimpleLineBorder(ColorConstants.black(), 1);
    button.getStyle().setBorder(border);
    button.getListenerMap().addListener(CursorEnterEvent.class, (CursorEnterEventListener) System.out::println);

    UIT.addComponent(button);
    UIT.start();
  }

  private final int width;
  private final int height;

  private volatile boolean running = false;
  private long window;
  private Frame frame;
  private DefaultInitializer initializer;
  private final Vector<Component> components = new Vector<>();

  protected int programID;
  protected int vertexShaderID;
  protected int fragmentShaderID;
  private int vao, vertexBuffer, uvBuffer;
  private final Vector<String> vertexShaders = new Vector<>();
  private final Vector<String> fragmentShaders = new Vector<>();

  private int textureID;
  private Camera camera = new Camera();
  private UserInput userInput;
  private Workspace workspace = new Workspace();
  private float currentTime;
  private String textureFilePath;
  private int mvpShaderLocation;

  UserInterfaceTest(int width, int height, String vertex_file_path,
                    String fragment_file_path, String texture_file_path) throws IOException {
    this.width = width;
    this.height = height;
    vertexShaders.add(new String(Files.readAllBytes(Path.of(vertex_file_path))));
    fragmentShaders.add(new String(Files.readAllBytes(Path.of(fragment_file_path))));
    textureFilePath = texture_file_path;
  }

  void addComponent(Component component) {
    if (!running) {
      components.add(component);
    }
  }

  public void start() {
    // Initialize OpenGL
    if (!GLFW.glfwInit()) {
      throw new RuntimeException("Can't initialize GLFW");
    }

    glfwDefaultWindowHints();
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
    glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);

    // Create window and userInput
    window = glfwCreateWindow(width, height, "Galton Boards", NULL, NULL);
    if (window == NULL) throw new RuntimeException("Failed to create the GLFW window");
    userInput = new UserInput(window, camera);

    glfwShowWindow(window);
    glfwMakeContextCurrent(window);
    GL.createCapabilities();
    glfwSwapInterval(1);

    // Create frame
    frame = new Frame(width, height);
    frame.getContainer().getStyle().getBackground().setColor(ColorConstants.transparent());
    frame.getContainer().setFocusable(false);
    for (Component component : components) {
      frame.getContainer().add(component);
    }

    // Create GUI initializer
    initializer = new DefaultInitializer(window, frame);

    // Initialize renderer
    initializer.getRenderer().initialize();

    // Get programID
    programID = glCreateProgram();

    // Compile and attach vertex shaders
    for (String vertexShader : vertexShaders) {
      vertexShaderID = glCreateShader(GL_VERTEX_SHADER);
      glShaderSource(vertexShaderID, vertexShader);
      glCompileShader(vertexShaderID);
      if (glGetShaderi(vertexShaderID, GL_COMPILE_STATUS) == GL_FALSE)
        throw new RuntimeException("Error creating vertex shader\n"
            + glGetShaderInfoLog(vertexShaderID, glGetShaderi(vertexShaderID, GL_INFO_LOG_LENGTH)));
      glAttachShader(programID, vertexShaderID);
    }

    // Compile and attach fragment shaders
    for (String fragmentShader : fragmentShaders) {
      fragmentShaderID = glCreateShader(GL_FRAGMENT_SHADER);
      glShaderSource(fragmentShaderID, fragmentShader);
      glCompileShader(fragmentShaderID);
      if (glGetShaderi(fragmentShaderID, GL_COMPILE_STATUS) == GL_FALSE)
        throw new RuntimeException("Error creating fragment shader\n"
            + glGetShaderInfoLog(fragmentShaderID, glGetShaderi(fragmentShaderID, GL_INFO_LOG_LENGTH)));
      glAttachShader(programID, fragmentShaderID);
    }

    // Link the program
    glLinkProgram(programID);
    if (glGetProgrami(programID, GL_LINK_STATUS) == GL_FALSE)
      throw new RuntimeException("Unable to link shader program:");
    glUseProgram(programID);

    mvpShaderLocation = glGetUniformLocation(programID, "MVP");

    // Generate buffers
    vertexBuffer = glGenBuffers();
    uvBuffer = glGenBuffers();

    // Load texture
    TextureLoader tex = new TextureLoader(textureFilePath);
    textureID = glGenTextures();
    glEnable(GL_TEXTURE_2D);
    glBindTexture(GL_TEXTURE_2D, textureID);
    glTexImage2D(GL_TEXTURE_2D, 0,GL_RGB, tex.getWidth(), tex.getHeight(), 0, GL_RGB, GL_UNSIGNED_BYTE, tex.buffer());
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

    currentTime = (float) glfwGetTime();

    final float[] CLEAR_COLOUR = {0.5f, 0.5f, 0.5f, 1};
    float lastTime, deltaTime;
    Matrix4f MVP = new Matrix4f();
    List<Float> auxList;
    float[] mesh, UVs;

    glClearColor(CLEAR_COLOUR[0], CLEAR_COLOUR[1], CLEAR_COLOUR[2], CLEAR_COLOUR[3]);

    Context context = initializer.getContext();

    while (!glfwWindowShouldClose(window)) {
      context.updateGlfwWindow();
      Vector2i windowSize = context.getWindowSize();
      glViewport(0, 0, windowSize.x, windowSize.y);
      glClear(GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
      glEnable(GL_BLEND);
      glDisable(GL_DEPTH_TEST);

      lastTime = currentTime;
      currentTime = (float) glfwGetTime();
      deltaTime = (currentTime - lastTime);
      userInput.update(deltaTime);
      workspace.update(deltaTime);

      int[] windowWidth = new int[1];
      int[] windowHeight = new int[1];
      glfwGetWindowSize(window, windowWidth, windowHeight);
      Matrix4f projection = new Matrix4f().perspective((float) Math.toRadians(45.0f),
          (float) windowWidth[0] / (float) windowHeight[0],
          0.1f,
          100.0f);
      projection.mul(camera.viewMatrix(), MVP);

      glUniformMatrix4fv(mvpShaderLocation, false, MVP.get(new float[16]));

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

      // TODO: this line is not working :(
      initializer.getRenderer().render(frame, context);

      glfwSwapBuffers(window);
      glfwPollEvents();

      initializer.getSystemEventProcessor().processEvents(frame, context);
      EventProcessorProvider.getInstance().processEvents();
      LayoutManager.getInstance().layout(frame);
      AnimatorProvider.getAnimator().runAnimations();
    }

    initializer.getRenderer().destroy();

    glDetachShader(programID, vertexShaderID);
    glDetachShader(programID, fragmentShaderID);

    glDeleteShader(vertexShaderID);
    glDeleteShader(fragmentShaderID);

    glDeleteProgram(programID);

    glfwFreeCallbacks(window);
    glfwDestroyWindow(window);

    glfwTerminate();
  }

}