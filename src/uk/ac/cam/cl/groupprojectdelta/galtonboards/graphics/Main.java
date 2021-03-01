package uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics;

import org.joml.Vector2f;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.UserInput;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.Board;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.Configuration;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.Workspace;

import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Main {

  static final int width = 1200;
  static final int height = 800;

  public static void main(String[] args) {
    // Create windows
    WindowBoards wb = new WindowBoards(width, height,
        "resources/shaders/vertexShader.glsl",
        "resources/shaders/fragmentShader.glsl",
        "resources/textures/texture.png"
    );

    // Start the interface
    new UserInterface(wb).start();
  }

}