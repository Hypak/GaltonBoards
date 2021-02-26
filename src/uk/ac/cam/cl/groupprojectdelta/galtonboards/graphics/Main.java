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

  private Workspace workspace = new Workspace();

  float currentTime;


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

    // Start the interface
    new UserInterface(wb).start();
  }

}