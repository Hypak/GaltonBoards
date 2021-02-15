import static org.lwjgl.opengl.GL11.*;

abstract class DrawableRectangle implements Drawable {
  DrawableRectangle(float posX, float posY, float sizeX, float sizeY) {
    this.posX = posX;
    this.posY = posY;
    this.sizeX = sizeX;
    this.sizeY = sizeY;
  }

  protected void setColor(float r, float g, float b) {
    this.r = r;
    this.g = g;
    this.b = b;
  }

  @Override
  public void draw(long window) {
    glColor3f(r, g, b);
    glBegin(GL_QUADS);
    glVertex2f(transformX(getPosX()), transformY(getPosY()));
    glVertex2f(transformX(getPosX()), transformY(getPosY() + getSizeY()));
    glVertex2f(transformX(getPosX() + getSizeX()), transformY(getPosY() + getSizeY()));
    glVertex2f(transformX(getPosX() + getSizeX()), transformY(getPosY()));
    glEnd();
  }

  private float transformX(float k) {
    return  2 * k - 1;
  }

  private float transformY(float k) {
    return  1 - 2 * k;
  }

  public boolean inside(float mouseX, float mouseY) {
    return (posX <= mouseX && mouseX <= posX + sizeX) &&
           (posY <= mouseY && mouseY <= posY + sizeY);
  }

  public float getPosX() {
    return posX;
  }

  protected void setPosX(float posX) {
    this.posX = posX;
  }

  public float getPosY() {
    return posY;
  }

  protected void setPosY(float posY) {
    this.posY = posY;
  }

  public float getSizeX() {
    return sizeX;
  }

  protected void setSizeX(float sizeX) {
    this.sizeX = sizeX;
  }

  public float getSizeY() {
    return sizeY;
  }

  protected void setSizeY(float sizeY) {
    this.sizeY = sizeY;
  }

  private float posX, posY, sizeX, sizeY;
  private float r = 0, g = 0, b = 0;
}