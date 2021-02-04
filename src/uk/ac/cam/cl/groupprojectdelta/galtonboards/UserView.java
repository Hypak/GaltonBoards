package uk.ac.cam.cl.groupprojectdelta.galtonboards;

public class UserView {

    private float x;
    private float y;
    private float scale;
    private int displayWidth;
    private int displayHeight;
    private float displayHalfWidth;
    private float displayHalfHeight;

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void setDisplayWidth(int displayWidth) {
        this.displayWidth = displayWidth;
    }

    public void setDisplayHeight(int displayHeight) {
        this.displayHeight = displayHeight;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getScale() {
        return scale;
    }

    public int getDisplayWidth() {
        return displayWidth;
    }

    public int getDisplayHeight() {
        return displayHeight;
    }

    public UserView(float x, float y, float scale, int displayWidth, int displayHeight) {
        this.x = x;
        this.y = y;
        this.scale = scale;
        this.displayWidth = displayWidth;
        this.displayHeight = displayHeight;
        this.displayHalfWidth = (float)displayWidth / 2.0f;
        this.displayHalfHeight = (float)displayHeight / 2.0f;
    }

    public void moveView(float deltaX, float deltaY) {
        this.x += deltaX;
        this.y += deltaY;
    }

    public float getWorldX(int pixelX) {
        return x + (pixelX - displayHalfWidth) * scale;
    }

    public float getWorldY(int pixelY) {
        return y + (pixelY - displayHalfHeight) * scale;
    }
}
