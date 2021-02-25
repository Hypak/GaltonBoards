package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.joml.Vector2f;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.Drawable;

public class Configuration implements Drawable {
  private List<Board> boards = new LinkedList<>();

  public Configuration() {
    setConfiguration("");
  }

  public Board getStartBoard() {
    return boards.get(0);
  }

  public void addBoard(Board board) {
    boards.add(board);
  }

  public void setConfiguration(String label) {
    boards = new LinkedList<>();
    switch (label) {
      case "Geometric":
        boards.add(new GeometricBoard(0.4f, 10));
        break;
      case "Uniform":
        boards.add(new UniformBoard(0.5f, 0.5f, 10));
      default:
        boards.add(new Board(5));
        boards.add(new Board(3));
        boards.add(new Board(3));
        boards.get(0).getBucket(0).setOutput(boards.get(2));
        boards.get(0).getBucket(1).setOutput(boards.get(1));
        boards.get(1).updateBoardPosition(new Vector2f(3, -15));
        boards.get(2).updateBoardPosition(new Vector2f(-2, -15));
    }
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
