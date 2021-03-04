package uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.panel;

public class PanelLabel implements PanelOption {

  private final String label;

  public PanelLabel(String label) {
    this.label = label;
  }

  @Override
  public String getName() {
    return label;
  }
}
