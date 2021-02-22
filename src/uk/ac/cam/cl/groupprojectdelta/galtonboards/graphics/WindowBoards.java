package uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics;

import org.joml.Matrix4f;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.UserInput;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.Configuration;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.Simulation;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.Workspace;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

class WindowBoards extends Window {

  private final Camera camera = new Camera();
  private UserInput userInput;
  private final Workspace workspace = new Workspace();
  private float currentTime;
  private int mvpShaderLocation;

  private int programID;
  private int vertexShaderID;
  private int fragmentShaderID;
  private int vertexBuffer, uvBuffer;

  private final String textureFilePath;
  private final String vertexShaderPath;
  private final String fragmentShaderPath;

  final float[] CLEAR_COLOUR = {0.5f, 0.5f, 0.5f, 1};

  public WindowBoards(int width, int height, String vertexShaderPath, String fragmentShaderPath, String textureFilePath) {
    super(width, height);
    this.vertexShaderPath = vertexShaderPath;
    this.fragmentShaderPath = fragmentShaderPath;
    this.textureFilePath = textureFilePath;
  }

  Configuration getConfiguration() {
    return workspace.getConfiguration();
  }

  Simulation getSimulation() {
    return workspace.getSimulation();
  }

  Camera getCamera() {
    return camera;
  }

  @Override
  void initialize(long window) {
    userInput = new UserInput(window, camera);
    programID = glCreateProgram();

    // Load, compile and attach shaders
    vertexShaderID = loadShader(vertexShaderPath, GL_VERTEX_SHADER);
    fragmentShaderID = loadShader(fragmentShaderPath, GL_FRAGMENT_SHADER);

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
    loadTexture(textureFilePath);
  }

  @Override
  void loop(long window) {
    glfwSwapInterval(1);

    float lastTime, deltaTime;
    Matrix4f MVP = new Matrix4f();
    List<Float> auxList;
    float[] mesh, UVs;

    // Clear window and setup OpenGL
    glClearColor(CLEAR_COLOUR[0], CLEAR_COLOUR[1], CLEAR_COLOUR[2], CLEAR_COLOUR[3]);
    glClear(GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glEnable(GL_BLEND);
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    glDisable(GL_CULL_FACE);
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

    int vao = glGenVertexArrays();
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
    glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
    glEnableVertexAttribArray(0);

    glBindBuffer(GL_ARRAY_BUFFER, uvBuffer);
    glBufferData(GL_ARRAY_BUFFER, UVs, GL_STATIC_DRAW);
    glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
    glEnableVertexAttribArray(1);

    glDrawArrays(
        GL_TRIANGLES,
        0,
        mesh.length / 3
    );

    glfwSwapBuffers(window);
    glfwPollEvents();
  }

  @Override
  void destroy(long window) {
    // Detach and delete shaders
    glDetachShader(programID, vertexShaderID);
    glDetachShader(programID, fragmentShaderID);
    glDeleteShader(vertexShaderID);
    glDeleteShader(fragmentShaderID);

    glDeleteProgram(programID);
    glfwFreeCallbacks(window);
    glfwDestroyWindow(window);
  }

  /**
   * Load, compile and attach a shader
   *
   * @param shaderPath: shader file name
   * @param shaderType: vertex (GL_VERTEX_SHADER) or fragment (GL_FRAGMENT_SHADER)
   * @return shader identifier
   */
  private int loadShader(String shaderPath, int shaderType) {
    // Load the shader
    String shaderSource;
    try {
      shaderSource = new String(Files.readAllBytes(Path.of(shaderPath)));
    } catch (IOException e) {
      throw new RuntimeException("Can't open fragment shader");
    }

    // Create, compile and attach the shader
    int shaderID = glCreateShader(shaderType);
    glShaderSource(shaderID, shaderSource);
    glCompileShader(shaderID);
    if (glGetShaderi(shaderID, GL_COMPILE_STATUS) == GL_FALSE)
      throw new RuntimeException("Error creating vertex shader\n"
          + glGetShaderInfoLog(shaderID, glGetShaderi(shaderID, GL_INFO_LOG_LENGTH)));
    glAttachShader(programID, shaderID);

    return shaderID;
  }

  /**
   * Load a texture
   *
   * @param texturePath: shader file name
   * @return texture identifier
   */
  private int loadTexture(String texturePath) {
    // Load the texture
    TextureLoader tex = new TextureLoader(texturePath);

    // Generate and bind texture
    int textureID = glGenTextures();
    glEnable(GL_TEXTURE_2D);
    glBindTexture(GL_TEXTURE_2D, textureID);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, tex.getWidth(), tex.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, tex.buffer());
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

    return textureID;
  }


}
