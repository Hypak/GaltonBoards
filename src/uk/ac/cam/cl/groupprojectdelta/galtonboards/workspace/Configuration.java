package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace;

import java.util.*;

import org.joml.Vector2f;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.Drawable;

public class Configuration implements Drawable {
  public static LinkedHashMap<String, Configuration> savedConfigurations;
  static {
    savedConfigurations = new LinkedHashMap<>();

    Configuration normal = new Configuration();
    normal.boards.add(new Board(5));
    normal.boards.add(new Board(5));
    normal.boards.add(new Board(5));
    normal.boards.get(0).getBucket(0).setOutput(normal.boards.get(2));
    normal.boards.get(0).getBucket(1).setOutput(normal.boards.get(1));
    normal.boards.get(1).updateBoardPosition(new Vector2f(3, -15));
    normal.boards.get(2).updateBoardPosition(new Vector2f(-2, -15));
    savedConfigurations.put("Normal", normal);

    Configuration geometric = new Configuration();
    geometric.boards.add(new GeometricBoard(0.3f, 10));
    savedConfigurations.put("Geometric", geometric);

    Configuration uniform = new Configuration();
    uniform.boards.add(new UniformBoard(0.3f, 0.7f, 10));
    savedConfigurations.put("Uniform", uniform);

    Configuration gaussian = new Configuration();
    gaussian.boards.add(new GaussianBoard(0.3f, 10));
    savedConfigurations.put("Gaussian", gaussian);

    Configuration test = new Configuration();
    test.boards.add(new GeometricBoard(0.5f, 5));
    test.boards.add(new GeometricBoard(0.3f, 3));
    test.boards.add(new GeometricBoard(0.3f, 3));
    test.boards.get(0).getBucket(0).setOutput(normal.boards.get(2));
    test.boards.get(0).getBucket(1).setOutput(normal.boards.get(1));
    test.boards.get(1).updateBoardPosition(new Vector2f(3, -15));
    test.boards.get(2).updateBoardPosition(new Vector2f(-2, -15));
    savedConfigurations.put("Test", test);
  }
  static Configuration defaultConfig = savedConfigurations.get("Normal");
  private List<Board> boards = new LinkedList<>();

  public Configuration() { }



  public Board getStartBoard() {
    return boards.get(0);
  }

  public void addBoard(Board board) {
    boards.add(board);
  }

  public void setConfiguration(String label) {
    boards = savedConfigurations.get(label).boards;
  }

  @Override
  public List<Float> getMesh(float time) {
    List<Float> mesh = new ArrayList<>();
    for (Board board : boards) {
      mesh.addAll(board.getMesh(time));
    }
    return mesh;
  }

  @Override
  public List<Float> getUV() {
    // TODO
    List<Float> uv = new ArrayList<>();
    for (Board board : boards) {
      uv.addAll(board.getUV());
    }
    return uv;
  }
}
