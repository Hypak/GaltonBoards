package uk.ac.cam.cl.groupprojectdelta.galtonboards.canvas;

import org.joml.Vector2f;

public class CanvasMouseHandler {
  private final MouseHandler mainMouseHandler = new MouseHandler();
  private MouseHandler currentMouseHandler;

  private enum State {
    NONE, DOWN1, DOWN2, DRAG, UP1, REGION
  }

  private static final float DOUBLE_CLICK = 0.5f;
  private float lastClickTime;
  private Vector2f dragStart;
  private State state = State.NONE;
  private CanvasClickable currentClickable;
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
        ((CanvasDraggable) currentClickable).endDrag();
        break;
      case REGION:
        currentMouseHandler.getClickablesInRegion(dragStart, currentPos);
      case NONE:
      case UP1:
        break;
    }
  }

  public void mouseMove(Vector2f pos) {
    switch (state) {
      case DOWN1:
      case DOWN2:
        if (currentClickable instanceof CanvasDraggable) {
          state = State.DRAG;
          ((CanvasDraggable) currentClickable).startDrag(true);
          ((CanvasDraggable) currentClickable).moveDrag(pos.sub(currentPos));
          break;
        } else if (currentClickable == null) {
          state = State.REGION;
        }
      case NONE:
      case UP1:
        state = State.NONE;
        setCurrentClickable(getCurrentMouseHandler().getClickableAtPos(pos));
      case DRAG:
        ((CanvasDraggable) currentClickable).moveDrag(pos.sub(currentPos));
    }
    currentPos = pos;
  }

  private void setCurrentClickable(CanvasClickable newClickable) {
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


  public void setCurrentMouseHandler(
      MouseHandler currentMouseHandler) {
    this.currentMouseHandler = currentMouseHandler;
  }

  public MouseHandler getCurrentMouseHandler() {
    return currentMouseHandler;
  }

  public MouseHandler getMainMouseHandler() {
    return mainMouseHandler;
  }

}
