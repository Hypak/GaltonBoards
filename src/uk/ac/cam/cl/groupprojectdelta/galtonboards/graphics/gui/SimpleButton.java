package uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.gui;

import org.joml.Vector2f;
import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.optional.align.HorizontalAlign;
import org.liquidengine.legui.component.optional.align.VerticalAlign;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.icon.CharIcon;
import org.liquidengine.legui.icon.Icon;
import org.liquidengine.legui.listener.MouseClickEventListener;
import org.liquidengine.legui.style.border.SimpleLineBorder;
import org.liquidengine.legui.style.color.ColorConstants;
import org.liquidengine.legui.style.font.FontRegistry;

/**
 * Class representing a simple button.
 * It sets various style properties, to avoid code duplication and to keep a consistent style for all buttons.
 */
public class SimpleButton extends Button {
  /**
   * This button is a square and contains an icon.
   *
   * @param xPos                    The x coordinate of this panel's position.
   * @param yPos                    The y coordinate of this panel's position.
   * @param size                    The size of this button (it is a square).
   * @param iconCode                The icon code in FontRegistry.MATERIAL_DESIGN_ICONS.
   * @param mouseClickEventListener The callback on mouse click.
   */
  public SimpleButton(int xPos, int yPos, int size, int iconCode, MouseClickEventListener mouseClickEventListener) {
    super("", xPos, yPos, size, size);
    Icon iconRun = new CharIcon(new Vector2f(size, size), FontRegistry.MATERIAL_DESIGN_ICONS,
        (char) iconCode, ColorConstants.black());
    iconRun.setHorizontalAlign(HorizontalAlign.CENTER);
    iconRun.setVerticalAlign(VerticalAlign.MIDDLE);
    getStyle().getBackground().setIcon(iconRun);
    getStyle().setBorder(new SimpleLineBorder(ColorConstants.black(), 1));
    getListenerMap().addListener(MouseClickEvent.class, mouseClickEventListener);
  }

  /**
   * This button is a rectangle and contains a text description.
   *
   * @param xPos                    The x coordinate of this panel's position.
   * @param yPos                    The y coordinate of this panel's position.
   * @param width                   The width of this button.
   * @param height                  The height of this button.
   * @param label                   The text description of this button.
   * @param mouseClickEventListener The callback on mouse click.
   */
  public SimpleButton(int xPos, int yPos, int width, int height, String label,
                      MouseClickEventListener mouseClickEventListener) {
    super(label, xPos, yPos, width, height);
    getStyle().setBorder(new SimpleLineBorder(ColorConstants.black(), 1));
    getListenerMap().addListener(MouseClickEvent.class, mouseClickEventListener);
  }

}
