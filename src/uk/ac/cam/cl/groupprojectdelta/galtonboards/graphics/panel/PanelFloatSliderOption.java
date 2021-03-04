package uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.panel;

public interface PanelFloatSliderOption extends PanelOption {
  public Float getValue();
  public default float getMax() {return 1f;}
  public default float getMin() {return 0f;}
  public void setValue(float value);
}
