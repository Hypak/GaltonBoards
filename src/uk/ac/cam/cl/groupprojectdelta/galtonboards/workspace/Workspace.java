package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace;

import java.util.List;
import org.joml.Vector2f;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.Drawable;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.mouse.ClickableMap;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.mouse.Cursor;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.mouse.WorkspaceClickable;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.mouse.WorkspaceMouseHandler;

public class Workspace implements Drawable {
  public static final Workspace workspace = new Workspace();

  private final Configuration configuration = Configuration.defaultConfig;
  private final WorkspaceMouseHandler mouseHandler = new WorkspaceMouseHandler(configuration);
  private final Simulation simulation = new Simulation(configuration);
  private final Cursor cursor = new Cursor();

  public Configuration getConfiguration() {
    return configuration;
  }

  public Simulation getSimulation() {
    return simulation;
  }

  public void update(float deltaTime) {
    simulation.update(deltaTime);
  }

  public void mouseDown(float time) {
    mouseHandler.mouseDown(time);
  }

  public void mouseUp(float time) {
    mouseHandler.mouseUp(time);
  }

  public void mouseMove(Vector2f pos) {
    mouseHandler.mouseMove(pos);
    cursor.setPosition(pos);
  }

  @Override
  public List<Float> getMesh(float time) {
    List<Float> mesh = configuration.getMesh(time);
    mesh.addAll(simulation.getMesh(time));
    mesh.addAll(cursor.getMesh(time));
    return mesh;
  }

  @Override
  public List<Float> getUV() {
    List<Float> uv = configuration.getUV();
    uv.addAll(simulation.getUV());
    uv.addAll(cursor.getUV());
    return uv;
  }
}
