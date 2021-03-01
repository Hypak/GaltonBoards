package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.mouse;

import java.util.Collection;
import java.util.LinkedList;
import org.joml.Vector2f;
import org.liquidengine.legui.component.Label;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.UserInterface;

public class WorkspaceMouseHandler {
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
  private WorkspaceSelectionHandler selectionHandler = new WorkspaceSelectionHandler();

  public WorkspaceMouseHandler(ClickableMap clickableMap) {
    currentClickableMap = clickableMap;
  }

  public void mouseDown(float time) {
    ((Label)UserInterface.userInterface.editPanel.getChildComponents().get(0)).getTextState().setText(currentClickable.toString());
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
          } else if (currentClickable == null) {
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
}
