package uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics;

import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Camera {

  public static Camera camera;
  static {
    camera = new Camera();
    camera.Reset();
  }

  private Vector3f r;

  public void Reset() {
    r = new Vector3f(0, 0, -15);
  }

  public void setPosition(Vector3f position) {
    r = position;
  }

  public void zoom(float offset) {
    Matrix3f m = new Matrix3f();
    m.m22 = (float)Math.exp(offset);
    r.mul(m);
  }

  public Vector3f getPosition() {
    return r;
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
    ).reflect(new Vector3f(1,0,0), new Vector3f(0,0,0));
  }

  public void toWorldSpace(Vector2f normalisedScreenSpace) {
    normalisedScreenSpace.set(-normalisedScreenSpace.x, normalisedScreenSpace.y);
    normalisedScreenSpace.mul(r.z);
    normalisedScreenSpace.add(-r.x, r.y);
  }

}
