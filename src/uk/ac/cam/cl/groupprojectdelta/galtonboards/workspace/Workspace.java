package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace;

import java.util.List;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.Drawable;

public class Workspace implements Drawable {
  private final Configuration configuration = new Configuration();
  private final Simulation simulation = new Simulation(configuration.getStartBoard());

  public Configuration getConfiguration() {
    return configuration;
  }

  public void update(float deltaTime) {
    simulation.update(deltaTime);
  }

  @Override
  public List<Float> getMesh(float time) {
    // TODO: add balls
    List<Float> mesh = configuration.getMesh(time);
    mesh.addAll(simulation.getMesh(time));
    return mesh;
  }

  @Override
  public List<Float> getUV() {
    // TODO: add balls
    List<Float> uv = configuration.getUV();
    uv.addAll(simulation.getUV());
    return uv;
  }
}
