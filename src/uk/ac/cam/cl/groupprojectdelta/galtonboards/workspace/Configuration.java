package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.Drawable;

public class Configuration implements Drawable {
  private Board startBoard = new Board();
  private Collection<Board> boards = new LinkedList<>();

  public Configuration() {
    boards.add(startBoard);
  }

  public Board getStartBoard() {
    return startBoard;
  }

  public void addBoard(Board board) {
    boards.add(board);
  }

  @Override
  public List<Float> getMesh(float time) {
    // TODO
    return null;
  }

  @Override
  public List<Float> getUV() {
    // TODO
    return null;
  }
}
