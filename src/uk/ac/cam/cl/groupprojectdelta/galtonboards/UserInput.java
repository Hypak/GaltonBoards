package uk.ac.cam.cl.groupprojectdelta.galtonboards;

import java.util.EventListener;
import org.joml.Matrix3f;
import org.joml.Vector3f;
import org.liquidengine.legui.event.ScrollEvent;
import org.liquidengine.legui.listener.ScrollEventListener;
import org.lwjgl.glfw.GLFW;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.Camera;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.mouse.WorkspaceMouseHandler;


public class UserInput {

    float speed = 10;
    float scrollSpeed = -0.15f;
    long window;
    Camera camera;

    public UserInput(long window, Camera camera) {
        this.window = window;
        this.camera = camera;
    }

    public void update(float deltaT) {
        float right = 0;
        float up = 0;
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_D) == 1) {
            right = 1;
        } else if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_A) == 1) {
            right = -1;
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_W) == 1) {
            up = -1;
        } else if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_S) == 1) {
            up = 1;
        }
        if (right != 0 || up != 0) {
            Vector3f move = new Vector3f(right, up, 0).normalize();
            move.mul(speed * deltaT);
            camera.setPosition(camera.getPosition().sub(move));
        }
        if (GLFW.glfwGetKey(window, GLFW.GLFW_KEY_SPACE) == 1) {
            camera.Reset();
        }
    }

    public void scroll(ScrollEvent scrollEvent) {
        camera.zoom((float) scrollEvent.getYoffset() * scrollSpeed);
    }

}
