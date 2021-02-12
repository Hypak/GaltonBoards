package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace;

import org.joml.Vector2f;
import org.joml.Vector2i;

public class UserView {

    private Vector2f position;
    private float scale;
    private Vector2i displaySize;
    private Vector2f displayHalfSize;

    public UserView(Vector2f position, float scale, Vector2i displaySize) {
        this.position = position;
        this.scale = scale;
        this.displaySize = displaySize;
        this.displayHalfSize = new Vector2f(displaySize.x / 2.0f, displaySize.y / 2.0f);
    }

    public Vector2f getPosition() {
        return position;
    }

    public Vector2i getDisplaySize() {
        return displaySize;
    }

    public void setPosition(Vector2f position) {
        this.position = position;
    }

    public void setDisplaySize(Vector2i displaySize) {
        this.displaySize = displaySize;
        this.displayHalfSize = new Vector2f(displaySize.x / 2.0f, displaySize.y / 2.0f);
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getScale() {
        return scale;
    }

    public float getWorldPos(Vector2i pixelPos) {
        throw new UnsupportedOperationException();  // Matt said he'd implement this
    }

}
