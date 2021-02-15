package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace;

import java.util.List;
import org.joml.Vector2f;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.Drawable;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.mouse.ClickableMap;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.mouse.WorkspaceMouseHandler;

public class Workspace implements Drawable {
  private final ClickableMap clickableMap = new ClickableMap();
  private final WorkspaceMouseHandler mouseHandler = new WorkspaceMouseHandler(clickableMap);
  private final Configuration configuration = new Configuration(clickableMap);
  private final Simulation simulation = new Simulation(configuration.getStartBoard());

  public Configuration getConfiguration() {
    return configuration;
  }

  public void mouseDown(float time) {
    mouseHandler.mouseDown(time);
  }

  public void mouseUp(float time) {
    mouseHandler.mouseUp(time);
  }

  public void mouseMove(Vector2f pos) {
    mouseHandler.mouseMove(pos);
  }

  @Override
  public List<Float> getMesh(float time) {
    // TODO: add balls
    return configuration.getMesh(time);
  }

  @Override
  public List<Float> getUV() {
    // TODO: add balls
    return configuration.getUV();
  }
}
