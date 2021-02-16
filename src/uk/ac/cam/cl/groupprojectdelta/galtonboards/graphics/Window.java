package uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics;

abstract class Window {

  private final int width;
  private final int height;

  public Window(int width, int height) {
    this.width = width;
    this.height = height;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  /**
   * Window specific code that should run BEFORE the main loop
   */
  abstract void initialize(long window);

  /**
   * Window specific code that should run IN the main loop
   */
  abstract void loop(long window);

  /**
   * Window specific code that should run AFTER the main loop
   */
  abstract void destroy(long window);

}
