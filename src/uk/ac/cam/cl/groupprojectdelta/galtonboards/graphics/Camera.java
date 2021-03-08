package uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics;

import org.joml.*;
import org.liquidengine.legui.input.Mouse;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.Workspace;

import java.lang.Math;

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
    Vector3f target = new Vector3f(
            -Workspace.workspace.mouseHandler.getCurrentPos().x,
            Workspace.workspace.mouseHandler.getCurrentPos().y, 0);
    Vector3f fromTarget = new Vector3f(r).sub(target);
    fromTarget.mul((float) Math.exp(offset));
    r = new Vector3f();
    r.add(target);
    r.add(fromTarget);
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
