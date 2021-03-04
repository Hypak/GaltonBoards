package uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.panel;

public interface PanelIntSliderOption extends PanelOption {
  public Integer getValue();
  public default int getMax() {return 50;}
  public default int getMin() {return 0;}
  public Integer setValue(float value);
}
