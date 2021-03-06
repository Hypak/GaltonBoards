package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.mouse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.joml.Vector2f;
import org.liquidengine.legui.component.Label;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.Drawable;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.UserInterface;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.Workspace;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.Board;

public class WorkspaceMouseHandler implements Drawable {
  private ClickableMap currentClickableMap;

  private enum State {
    NONE, DOWN1, DOWN2, DRAG, UP1, REGION
  }

  private static final float DOUBLE_CLICK = 0.5f;
  private float lastClickTime;
  private Vector2f dragStart;
  private State state = State.NONE;
  private WorkspaceClickable currentClickable;
  private Vector2f currentPos = new Vector2f();
  private final WorkspaceSelectionHandler selectionHandler = new WorkspaceSelectionHandler();

  public WorkspaceMouseHandler(ClickableMap clickableMap) {
    currentClickableMap = clickableMap;
  }

  public WorkspaceSelectionHandler getSelectionHandler() {return selectionHandler;}

  public void mouseDown(float time) {
    switch (state) {
      case NONE:
        lastClickTime = time;
        dragStart = currentPos;
        if (currentClickable == null) {
          state = State.REGION;
        } else {
          state = State.DOWN1;
          currentClickable.press();
          if (currentClickable instanceof WorkspaceSelectable) {
            selectionHandler.clearSelection();
            selectionHandler.addToSelection((WorkspaceSelectable) currentClickable);
          }
        }
        break;
      case UP1:
        if (time - lastClickTime < DOUBLE_CLICK) {
          state = State.DOWN2;
          currentClickable.press();
        } else {
          state = State.DOWN1;
          lastClickTime = time;
          dragStart = currentPos;
          currentClickable.press();
        }
      case DOWN1:
      case DOWN2:
      case DRAG:
      case REGION:
        // These states shouldn't happen.
        break;
    }
  }

  public void mouseUp(float time) {
    switch (state) {
      case DOWN1:
        state = State.UP1;
        currentClickable.release();
        break;
      case DOWN2:
        state = State.NONE;
        currentClickable.release();
        currentClickable.doubleClick();
        break;
      case DRAG:
        state = State.NONE;
        ((WorkspaceDraggable) currentClickable).endDrag();
        break;
      case REGION:
        selectionHandler.clearSelection();
        selectionHandler.addToSelection(currentClickableMap.getSelectablesInRegion(dragStart, currentPos));
        state = State.NONE;
      case NONE:
      case UP1:
        break;
    }
  }

  public void mouseMove(Vector2f pos) {
    if (!pos.equals(currentPos)) {
      switch (state) {
        case DOWN1:
        case DOWN2:
          if (currentClickable instanceof WorkspaceDraggable) {
            state = State.DRAG;
            ((WorkspaceDraggable) currentClickable).startDrag();
            ((WorkspaceDraggable) currentClickable).moveDrag(new Vector2f().set(pos).sub(currentPos));
            break;
          } else if (currentClickable == null || currentClickable instanceof WorkspaceSelectable) {
            state = State.REGION;
            break;
          }
        case NONE:
        case UP1:
          state = State.NONE;
          setCurrentClickable(getCurrentClickableMap().getClickableAtPos(pos));
          break;
        case DRAG:
          ((WorkspaceDraggable) currentClickable).moveDrag(new Vector2f().set(pos).sub(currentPos));
          break;
        case REGION:
          break;
      }
      currentPos = pos;
    }
  }

  private void setCurrentClickable(WorkspaceClickable newClickable) {
    if (newClickable != currentClickable) {
      if (currentClickable != null) {
        currentClickable.mouseExit();
      }
      currentClickable = newClickable;
      if (currentClickable != null) {
        currentClickable.mouseEnter();
      }
    }
  }

  public void setCurrentClickableMap(ClickableMap currentClickableMap) {
    this.currentClickableMap = currentClickableMap;
  }

  public ClickableMap getCurrentClickableMap() {
    return currentClickableMap;
  }

  @Override
  public List<Float> getMesh(float time) {
    List<Float> list;
    float zEpsilon = z - 1E-3f;

    if (state == State.REGION) {
      list = List.of(
          dragStart.x, dragStart.y, zEpsilon,
          dragStart.x, currentPos.y, zEpsilon,
          currentPos.x, dragStart.y, zEpsilon,

          currentPos.x, currentPos.y, zEpsilon,
          currentPos.x, dragStart.y, zEpsilon,
          dragStart.x, currentPos.y, zEpsilon
      );
    } else {
      list = new ArrayList<>();
    }
    return list;
  }

  @Override
  public List<Float> getUV() {
    List<Float> list;
    if (state == State.REGION) {
      //todo: fix the graphics for this region selection

      final float top = 0.5f;
      final float bottom = 0.75f;
      final float left = 0.0f;
      final float right = 0.25f;

      list = new ArrayList<>(Arrays.asList(
          // face 1
          top, left,
          bottom, left,
          bottom, right,
          // face 2
          top, left,
          top, right,
          bottom, right
      ));
    } else {
      list = new ArrayList<>();
    }
    return list;
  }

  @Override
  public List<Float> getColourTemplate() {
    List<Float> list;
    if (state == State.REGION) {
      list = new ArrayList<>(Arrays.asList(
          0.5f, 0.7f, 1f,
          0.5f, 0.7f, 1f,
          0.5f, 0.7f, 1f,

          0.5f, 0.7f, 1f,
          0.5f, 0.7f, 1f,
          0.5f, 0.7f, 1f
      ));
    } else {
      list = new ArrayList<>();
    }
    return list;
  }
}
