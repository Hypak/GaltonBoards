package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.ui;

import org.joml.Vector2f;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.Drawable;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.mouse.WorkspaceClickable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class WorkspaceButton implements WorkspaceClickable, Drawable {
  protected boolean hover;
  protected final static float size = 0.4f;

  @Override
  public void mouseEnter() {
    hover = true;
  }

  @Override
  public void mouseExit() {
    hover = false;
  }

  @Override
  public boolean containsPoint(Vector2f point) {
    Vector2f bound = new Vector2f();
    Vector2f position = getPosition();

    Vector2f dimensions = new Vector2f(size);
    position.add(dimensions, bound);

    return point.x > position.x
        && point.x < bound.x
        && point.y > position.y
        && point.y < bound.y;
  }

  protected abstract Vector2f getPosition();

  @Override
  public List<Float> getMesh(float time) {
    List<Float> points;
    Vector2f bound = new Vector2f();
    Vector2f position = getPosition();

    Vector2f dimensions = new Vector2f(size);
    position.add(dimensions, bound);

    //  +----+
    //  |1 / |
    //  | / 2|
    //  +----+

    points = new ArrayList<>(Arrays.asList(
            // Face 1
            position.x, position.y, z,
            bound.x, position.y, z,
            bound.x, bound.y, z,

            // Face 2
            position.x, position.y, z,
            position.x, bound.y, z,
            bound.x, bound.y, z
    ));

    return points;
  }

  @Override
  public List<Float> getUV() {
    final float top = 0.75f;
    final float bottom = 1f;
    final float left = 0.75f;
    final float right = 1f;

    List<Float> UVs = List.of(
            // face 1
            top,left,
            bottom,left,
            bottom,right,
            // face 2
            top,left,
            top,right,
            bottom,right
    );
    return UVs;
  }

  @Override
  public List<Float> getColourTemplate() {
    return listFromColors(1, 0, 1);
  }

  private static List<Float> listFromColors(float red, float green, float blue) {
    return List.of(
        // face 1
        red, green, blue,
        red, green, blue,
        red, green, blue,
        // face 2
        red, green, blue,
        red, green, blue,
        red, green, blue
    );
  }

}
