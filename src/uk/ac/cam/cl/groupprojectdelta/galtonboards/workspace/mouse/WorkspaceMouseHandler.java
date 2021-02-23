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
  private WorkspaceSelectionHandler selectionHandler = new WorkspaceSelectionHandler();

  public WorkspaceMouseHandler(ClickableMap clickableMap) {
    currentClickableMap = clickableMap;
  }

  public void mouseDown(float time) {
    System.out.println(state.name());
    System.out.println(currentClickable);
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
    System.out.println(state.name());
    System.out.println(currentClickable);
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
        selectionHandler.clearSelection();
        selectionHandler.addToSelection(currentClickableMap.getSelectablesInRegion(dragStart, currentPos));
        state = State.NONE;
      case NONE:
      case UP1:
        break;
    }
  }

  public void mouseMove(Vector2f pos) {
    System.out.println(state.name());
    System.out.println(currentClickable);
    System.out.println(getCurrentClickableMap().getClickableAtPos(pos));
    System.out.println(pos);
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
          break;
        }
      case NONE:
      case UP1:
        state = State.NONE;
        setCurrentClickable(getCurrentClickableMap().getClickableAtPos(pos));
        break;
      case DRAG:
        ((WorkspaceDraggable) currentClickable).moveDrag(pos.sub(currentPos));
        break;
      case REGION:
        break;
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


  public void setCurrentClickableMap(ClickableMap currentClickableMap) {
    this.currentClickableMap = currentClickableMap;
  }

  public ClickableMap getCurrentClickableMap() {
    return currentClickableMap;
  }

}
