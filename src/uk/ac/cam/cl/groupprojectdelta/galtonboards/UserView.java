package uk.ac.cam.cl.groupprojectdelta.galtonboards;

public class UserView {

    private float x;
    private float y;
    private float zoom;
    private int displayWidth;

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

    private int displayHeight;

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

}
