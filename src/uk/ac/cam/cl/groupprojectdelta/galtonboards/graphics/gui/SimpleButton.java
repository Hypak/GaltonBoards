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

public class SimpleButton extends Button {
  public SimpleButton(int size, int xPos, int yPos, int iconCode, MouseClickEventListener cb) {
    super("", xPos, yPos, size, size);
    Icon iconRun = new CharIcon(new Vector2f(size, size), FontRegistry.MATERIAL_DESIGN_ICONS,
        (char) iconCode, ColorConstants.black());
    iconRun.setHorizontalAlign(HorizontalAlign.CENTER);
    iconRun.setVerticalAlign(VerticalAlign.MIDDLE);
    getStyle().getBackground().setIcon(iconRun);
    getStyle().setBorder(new SimpleLineBorder(ColorConstants.black(), 1));
    getListenerMap().addListener(MouseClickEvent.class, cb);
  }

  public SimpleButton(String label, int xPos, int yPos, int width, int height, MouseClickEventListener cb) {
    super(label, xPos, yPos, width, height);
    getStyle().setBorder(new SimpleLineBorder(ColorConstants.black(), 1));
    getListenerMap().addListener(MouseClickEvent.class, cb);
  }


}
