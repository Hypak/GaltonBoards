package uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.panel;

import java.util.List;

public interface PanelTagOption extends PanelOption {
  public List<String> getTags();
  public void setTags(List<String> tag);
  public void clearTags();
}
