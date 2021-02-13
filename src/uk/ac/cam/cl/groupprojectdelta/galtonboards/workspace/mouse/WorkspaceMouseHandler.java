package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.mouse;

import org.joml.Vector2f;

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

  public void mouseDown(float time) {
    switch (state) {
      case NONE:
        state = State.DOWN1;
        lastClickTime = time;
        dragStart = currentPos;
        currentClickable.press();
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
        ((WorkspaceDraggable) currentClickable).endDrag();
        break;
      case REGION:
        currentClickableMap.getClickablesInRegion(dragStart, currentPos);
      case NONE:
      case UP1:
        break;
    }
  }

  public void mouseMove(Vector2f pos) {
    switch (state) {
      case DOWN1:
      case DOWN2:
        if (currentClickable instanceof WorkspaceDraggable) {
          state = State.DRAG;
          ((WorkspaceDraggable) currentClickable).startDrag(true);
          ((WorkspaceDraggable) currentClickable).moveDrag(pos.sub(currentPos));
          break;
        } else if (currentClickable == null) {
          state = State.REGION;
        }
      case NONE:
      case UP1:
        state = State.NONE;
        setCurrentClickable(getCurrentClickableMap().getClickableAtPos(pos));
      case DRAG:
        ((WorkspaceDraggable) currentClickable).moveDrag(pos.sub(currentPos));
    }
    currentPos = pos;
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


  public void setCurrentClickableMap(
      ClickableMap currentClickableMap) {
    this.currentClickableMap = currentClickableMap;
  }

  public ClickableMap getCurrentClickableMap() {
    return currentClickableMap;
  }

}
