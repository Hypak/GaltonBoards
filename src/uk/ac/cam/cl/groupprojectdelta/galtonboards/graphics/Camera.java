package uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

  private Vector3f r;

  public Camera() {
    r = new Vector3f();
  }

  public void setPosition(Vector3f position) {
    r = position;
  }

  public Matrix4f viewMatrix() {

    Vector3f facing, up;

    facing = new Vector3f(0, 0, 1);
    facing.add(r);

    // camera wont need to adjust roll in this project
    up = new Vector3f(0, 1, 0);

    return new Matrix4f().lookAt(
            r,
            facing,
            up
    );
  }
}
