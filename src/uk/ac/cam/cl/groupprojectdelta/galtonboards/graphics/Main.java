package uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics;

import org.joml.Vector2f;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.UserInput;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.Board;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.Configuration;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.Workspace;

import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Main {

  private long window;
  private int programID;

  private int vertexBuffer;
  private int uvBuffer;

  private Camera camera = new Camera();
  private UserInput userInput;

  private int mvpShaderLocation;

  private Workspace workspace = Workspace.workspace;

  float currentTime;


  public static void main(String[] args) {
    // Create windows
    WindowBoards wb = new WindowBoards(1000, 1000,
        "resources/shaders/vertexShader.glsl",
        "resources/shaders/fragmentShader.glsl",
        "resources/textures/texture.png"
    );

    // Start the interface
    new UserInterface(wb).start();
  }

}