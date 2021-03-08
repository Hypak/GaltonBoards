package uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.panel;

import java.util.List;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.Distribution;

public interface PanelBoardTypeOption extends PanelOption{
  public Distribution getDistribution();
  public void setDistribution(Distribution distribution);
}
