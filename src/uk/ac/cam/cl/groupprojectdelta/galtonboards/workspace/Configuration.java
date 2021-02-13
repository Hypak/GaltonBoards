package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.Drawable;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.mouse.ClickableMap;

public class Configuration implements Drawable {
  private Board startBoard = new Board();
  private final Collection<Board> boards = new LinkedList<>();
  private final ClickableMap clickableMap;

  public Configuration(ClickableMap clickableMap) {
    boards.add(startBoard);
    this.clickableMap = clickableMap;
  }

  public Board getStartBoard() {
    return startBoard;
  }

  public void addBoard(Board board) {
    boards.add(board);
    clickableMap.addClickable(board);
  }

  public void removeBoard(Board board) {
    boards.remove(board);
    clickableMap.removeClickable(board);
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
