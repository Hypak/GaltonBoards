package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.joml.Vector2f;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.Drawable;

public class Cursor implements Drawable {
  Vector2f position = new Vector2f();

  public void setPosition(Vector2f position) {
    this.position.set(position);
  }

  @Override
  public List<Float> getMesh(float time) {
    List<Float> points = new ArrayList<>();
    Vector2f bound = new Vector2f();

    Vector2f dimensions = new Vector2f(0.1f, 0.1f);
    position.add(dimensions, bound);

    //  +----+
    //  |1 / |
    //  | / 2|
    //  +----+

    float z = 0.25f;

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
}
