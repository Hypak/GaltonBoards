package uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.gui;

import org.liquidengine.legui.component.Panel;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.event.ScrollEvent;
import org.liquidengine.legui.listener.MouseClickEventListener;
import org.liquidengine.legui.listener.ScrollEventListener;
import org.liquidengine.legui.style.border.SimpleLineBorder;
import org.liquidengine.legui.style.color.ColorConstants;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.Camera;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.UserInterface;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.WindowBoards;

/**
 * Custom panel for the main section of the window, containing the boards.
 * It is a transparent panel listening to user events and passing them on to the OpenGL code.
 * It also includes some GUI elements, the navigation buttons and the zoom buttons
 */
public class MainPanel extends Panel {
  /**
   * Creates the MainPanel.
   *
   * @param xPos    The x coordinate of this panel's position.
   * @param yPos    The y coordinate of this panel's position.
   * @param width   The width of the panel.
   * @param height  The height of the panel.
   * @param size    The size of buttons in this panel.
   * @param spacing The spacing between buttons in this panel.
   */
  public MainPanel(int xPos, int yPos, int width, int height, int size, int spacing) {
    super(xPos, yPos, width, height);
    getStyle().getBackground().setColor(ColorConstants.transparent());
    getStyle().setBorder(new SimpleLineBorder());
    getListenerMap().addListener(MouseClickEvent.class,
        (MouseClickEventListener) UserInterface.userInterface.getWindowBoards()::mouseClickEvent);
    getListenerMap().addListener(ScrollEvent.class,
        (ScrollEventListener) event -> UserInterface.userInterface.getWindowBoards().getUserInput().scroll(event));

    add(new NavigationControls(0, height - 3 * size - 4 * spacing, 3 * size + 4 * spacing,
        3 * size + 4 * spacing, size, spacing));
    add(new ZoomControls(3 * size + 4 * spacing, height - 2 * size - 3 * spacing, size + 2 * spacing,
        2 * size + 3 * spacing, size, spacing));
  }

  /**
   * Sub-panel of MainPanel, it contains the controls for Camera movements.
   */
  private static class NavigationControls extends Panel {
    NavigationControls(int xPos, int yPos, int width, int height, int size, int spacing) {
      super(xPos, yPos, width, height);
      getStyle().getBackground().setColor(ColorConstants.transparent());
      getStyle().getBorder().setEnabled(false);
      getStyle().getShadow().setColor(ColorConstants.transparent());

      float movement = 1.0f;
      WindowBoards windowBoards = UserInterface.userInterface.getWindowBoards();

      add(new SimpleButton(spacing, size + 2 * spacing, size, 0xF141,
          makeMovementCallback(windowBoards.getCamera(), movement, 0)));
      add(new SimpleButton(size + 2 * spacing, 2 * size + 3 * spacing, size, 0xF140,
          makeMovementCallback(windowBoards.getCamera(), 0, -movement)));
      add(new SimpleButton(size + 2 * spacing, size + 2 * spacing, size, 0xF44A,
          event -> windowBoards.getCamera().Reset()));
      add(new SimpleButton(size + 2 * spacing, spacing, size, 0xF143,
          makeMovementCallback(windowBoards.getCamera(), 0, movement)));
      add(new SimpleButton(2 * size + 3 * spacing, size + 2 * spacing, size, 0xF142,
          makeMovementCallback(windowBoards.getCamera(), -movement, 0)));
    }

    /**
     * Helper method creating a callback that moves camera by (dx,dy).
     */
    private MouseClickEventListener makeMovementCallback(Camera camera, float dx, float dy) {
      return event -> {
        if (event.getAction().equals(MouseClickEvent.MouseClickAction.CLICK)) {
          camera.setPosition(camera.getPosition().add(dx, dy, 0));
        }
      };
    }

  }

  /**
   * Sub-panel of MainPanel, it contains the controls for zooming in and out.
   */
  private static class ZoomControls extends Panel {
    ZoomControls(int xPos, int yPos, int width, int height, int size, int spacing) {
      super(xPos, yPos, width, height);
      getStyle().getBackground().setColor(ColorConstants.transparent());
      getStyle().getBorder().setEnabled(false);
      getStyle().getShadow().setColor(ColorConstants.transparent());

      float zoomOffset = 0.2f;
      WindowBoards windowBoards = UserInterface.userInterface.getWindowBoards();

      add(new SimpleButton(spacing, size + 2 * spacing, size, 0xF374,
          makeZoomCallback(windowBoards.getCamera(), zoomOffset)));
      add(new SimpleButton(spacing, spacing, size, 0xF415,
          makeZoomCallback(windowBoards.getCamera(), -zoomOffset)));
    }

    /**
     * Helper method creating a callback that changes the camera's zoom by dz.
     */
    private static MouseClickEventListener makeZoomCallback(Camera camera, float dz) {
      return event -> {
        if (event.getAction().equals(MouseClickEvent.MouseClickAction.CLICK)) {
          camera.zoom(dz);
        }
      };
    }
  }
}

