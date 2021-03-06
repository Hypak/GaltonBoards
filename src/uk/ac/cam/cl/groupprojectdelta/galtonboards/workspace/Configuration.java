package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace;

import java.util.*;
import java.util.stream.Collector;

import org.joml.Vector2f;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.Drawable;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.*;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.mouse.ClickableMap;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.mouse.WorkspaceClickable;

public class Configuration implements Drawable, ClickableMap {
  public static LinkedHashMap<String, Configuration> savedConfigurations;
  static {
    savedConfigurations = new LinkedHashMap<>();

    Configuration normal = new Configuration();
    normal.addBoard(new Board(5));
    normal.addBoard(new Board(5));
    normal.addBoard(new Board(5));
    normal.boards.get(0).getBucket(0).setOutput(normal.boards.get(2));
    normal.boards.get(0).getBucket(1).setOutput(normal.boards.get(1));
    normal.boards.get(1).updateBoardPosition(new Vector2f(3, -15));
    normal.boards.get(2).updateBoardPosition(new Vector2f(-2, -15));
    normal.boards.get(0).getRootPeg().setGivenTags(List.of("testtag1", "testtag2"));
    savedConfigurations.put("Normal", normal);

    Configuration geometric = new Configuration();
    geometric.addBoard(new GeometricBoard(0.3f, 10));
    savedConfigurations.put("Geometric", geometric);

    Configuration uniform = new Configuration();
    uniform.addBoard(new UniformBoard(0.3f, 0.7f, 10));
    savedConfigurations.put("Uniform", uniform);

    Configuration gaussian = new Configuration();
    gaussian.addBoard(new GaussianBoard(0.3f, 10));
    gaussian.boards.get(0).getRootPeg().setGivenTags(List.of("testtag1", "testtag2"));
    savedConfigurations.put("Gaussian", gaussian);

    Configuration test = new Configuration();
    test.boards.add(new GeometricBoard(0.5f, 5));
    test.boards.add(new GeometricBoard(0.3f, 3));
    test.boards.add(new GeometricBoard(0.3f, 3));
    test.boards.get(0).getBucket(0).setOutput(normal.boards.get(2));
    test.boards.get(0).getBucket(1).setOutput(normal.boards.get(1));
    test.boards.get(1).updateBoardPosition(new Vector2f(3, -15));
    test.boards.get(2).updateBoardPosition(new Vector2f(-2, -15));
    test.boards.get(0).getRootPeg().setGivenTags(List.of("testtag1", "testtag2"));
    savedConfigurations.put("Test", test);


    // BAYES DEMONSTRATION CONFIGURATION
    Configuration bayes = new Configuration();

    int k = 4;

    bayes.boards.add(new BinomialBoard(1, 0.5f)); // which factory?
    bayes.boards.add(new GeometricBoard(0.5f, k)); // factory 1
    bayes.boards.add(new GeometricBoard(0.2f, k)); // factory 2
    //bayes.boards.add(new BinomialBoard(1, 1f)); // broken
    //bayes.boards.add(new BinomialBoard(1, 0f)); // working
    bayes.boards.add(new CollectorBoard()); // broken lightbulbs
    bayes.boards.add(new CollectorBoard()); // working lightbulbs

    bayes.boards.get(0).getBucket(0).setOutput(bayes.boards.get(1));
    bayes.boards.get(0).getBucket(1).setOutput(bayes.boards.get(2));

    for (int i = 0; i < k; i++) { // route broken ones to the right board
      bayes.boards.get(1).getBucket(i).setOutput(bayes.boards.get(3));
      bayes.boards.get(2).getBucket(i).setOutput(bayes.boards.get(3));
    }
    // route working ones to the right board:
    bayes.boards.get(1).getBucket(k).setOutput(bayes.boards.get(4));
    bayes.boards.get(2).getBucket(k).setOutput(bayes.boards.get(4));

    bayes.boards.get(1).updateBoardPosition(new Vector2f(-3, -10));
    bayes.boards.get(2).updateBoardPosition(new Vector2f(3, -10));
    bayes.boards.get(3).updateBoardPosition(new Vector2f(-4, -20));
    bayes.boards.get(4).updateBoardPosition(new Vector2f(4, -20));

    bayes.boards.get(0).getRootPeg().setGivenTags(List.of("factory1", "factory2"));

    savedConfigurations.put("Bayes", bayes);

  }
  static Configuration defaultConfig = savedConfigurations.get("Normal");
  private List<Board> boards = new LinkedList<>();
  private Simulation simulation = null;

  public Simulation getSimulation() {
    return simulation;
  }

  void setSimulation(Simulation sim) {
    simulation = sim;
  }


  public Board getStartBoard() {
    return boards.get(0);
  }

  public void addBoard(Board board) {
    boards.add(board);
    board.setSimulation(simulation);
  }

  public void removeBoard(Board board) {
    boards.remove(board);
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
    List<Float> uv = new ArrayList<>();
    for (Board board : boards) {
      uv.addAll(board.getUV());
    }
    return uv;
  }

  @Override
  public List<Float> getColourTemplate() {
    List<Float> ct = new ArrayList<>();
    for (Board board : boards) {
      ct.addAll(board.getColourTemplate());
    }
    return ct;
  }

  @Override
  public Iterable<? extends WorkspaceClickable> getClickables() {
    return boards;
  }
}
