package uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.gui;

import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.Panel;
import org.liquidengine.legui.component.optional.TextState;
import org.liquidengine.legui.component.optional.align.HorizontalAlign;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.style.color.ColorConstants;

import java.util.List;
import java.util.function.Function;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Class representing a generic panel containing a slider.
 * The slider is a custom version of a regular slider.
 * Here values decrease and increase using buttons.
 */
class SliderButtonsPanel extends Panel {

  private int currentIndex;

  /**
   * Creates a panel with the custom slider.
   *
   * @param xPos        The x coordinate of this panel's position.
   * @param yPos        The y coordinate of this panel's position.
   * @param width       The width of the panel.
   * @param height      The height of the panel.
   * @param size        The size of buttons in this panel.
   * @param spacing     The spacing between buttons in this panel.
   * @param label       The label for this slider.
   * @param values      The possible values users can pick.
   * @param setCallback A callback called when a new value is chosen.
   */
  public SliderButtonsPanel(int xPos, int yPos, int width, int height, int size, int spacing, String label,
                            List<Float> values, Function<Float, Void> setCallback) {
    super(xPos, yPos, width, height);
    getStyle().getBackground().setColor(ColorConstants.transparent());
    getStyle().getBorder().setEnabled(false);
    getStyle().getShadow().setColor(ColorConstants.transparent());
    int halfSize = size / 2;

    // Pick an arbitrary initial value.
    currentIndex = values.size() / 2;

    // Label describing the slider
    Label sliderLabel = new Label(label, spacing, spacing, halfSize * 5, halfSize);
    sliderLabel.getStyle().setHorizontalAlign(HorizontalAlign.CENTER);
    add(sliderLabel);

    // Label showing the current value
    Label sliderValueLabel = new Label(values.get(currentIndex) + "x", spacing + 2 * halfSize,
        spacing + halfSize, halfSize, halfSize);
    sliderValueLabel.getStyle().setHorizontalAlign(HorizontalAlign.CENTER);
    add(sliderValueLabel);

    // Button decreasing the value
    add(new SimpleButton(spacing, spacing + halfSize, halfSize, 0xF374, mouseClickEvent -> {
      if (mouseClickEvent.getAction().equals(MouseClickEvent.MouseClickAction.RELEASE)) {
        currentIndex = max(currentIndex - 1, 0);
        setCallback.apply(values.get(currentIndex));
        sliderValueLabel.setTextState(new TextState(values.get(currentIndex) + "x"));
      }
    }));

    // Button increasing the value
    add(new SimpleButton(spacing + 4 * halfSize, spacing + halfSize, halfSize, 0xF415, mouseClickEvent -> {
      if (mouseClickEvent.getAction().equals(MouseClickEvent.MouseClickAction.RELEASE)) {
        currentIndex = min(currentIndex + 1, values.size() - 1);
        setCallback.apply(values.get(currentIndex));
        sliderValueLabel.setTextState(new TextState(values.get(currentIndex) + "x"));
      }
    }));
  }
}

