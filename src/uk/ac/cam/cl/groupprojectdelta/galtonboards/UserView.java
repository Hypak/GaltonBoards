package uk.ac.cam.cl.groupprojectdelta.galtonboards;

public class UserView {

    private float x;
    private float y;
    private float zoom;
    private int displayWidth;
    private int displayHeight;

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
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

    public float getZoom() {
        return zoom;
    }

    public int getDisplayWidth() {
        return displayWidth;
    }

    public int getDisplayHeight() {
        return displayHeight;
    }

    public UserView(float x, float y, float zoom, int displayWidth, int displayHeight) {
        this.x = x;
        this.y = y;
        this.zoom = zoom;
        this.displayWidth = displayWidth;
        this.displayHeight = displayHeight;
    }

    public void moveView(float deltaX, float deltaY) {
        this.x += deltaX;
        this.y += deltaY;
    }

    public float getWorldX(int pixelX) {
        return x + (pixelX - displayWidth / 2) / zoom;
    }

    public float getWorldY(int pixelY) {
        return y + (pixelY - displayHeight / 2) / zoom;
    }
}
