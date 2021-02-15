package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace;

import java.util.List;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.Drawable;

public class Workspace implements Drawable {
  private final Configuration configuration = new Configuration();;
  private final Simulation simulation = new Simulation(configuration.getStartBoard());

  public Configuration getConfiguration() {
    return configuration;
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