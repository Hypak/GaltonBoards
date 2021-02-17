package uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics;

import org.joml.Vector2i;
import org.liquidengine.legui.DefaultInitializer;
import org.liquidengine.legui.animation.AnimatorProvider;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Frame;
import org.liquidengine.legui.listener.processor.EventProcessorProvider;
import org.liquidengine.legui.style.color.ColorConstants;
import org.liquidengine.legui.system.layout.LayoutManager;

import java.util.Vector;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

class WindowControls extends Window {

  private Frame frame;
  private DefaultInitializer initializer;
  private final Vector<Component> components = new Vector<>();
  final float[] CLEAR_COLOUR = {0.5f, 0.5f, 0.5f, 1};
  private boolean initialized = false;

  public WindowControls(int width, int height) {
    super(width, height);
  }

  /**
   * Add legui component to the window
   * Can only be called before starting the main loop
   */
  void addComponent(Component component) {
    if (!initialized) {
      components.add(component);
    }
  }

  @Override
  void initialize(long window) {
    // Create the Frame
    frame = new Frame(getWidth(), getHeight());
    frame.getContainer().getStyle().getBackground().setColor(ColorConstants.transparent());
    frame.getContainer().setFocusable(false);
    for (Component component : components) {
      frame.getContainer().add(component);
    }

    // Create GUI initializer
    initializer = new DefaultInitializer(window, frame);

    // Initialize renderer
    initializer.getRenderer().initialize();

    initialized = true;
  }

  @Override
  void loop(long window) {
    // Setup OpenGL and update components
    glfwSwapInterval(0);
    initializer.getContext().updateGlfwWindow();
    Vector2i windowSize = initializer.getContext().getWindowSize();
    glViewport(0, 0, windowSize.x, windowSize.y);
    glClearColor(CLEAR_COLOUR[0], CLEAR_COLOUR[1], CLEAR_COLOUR[2], CLEAR_COLOUR[3]);
    glClear(GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    glEnable(GL_BLEND);
    glDisable(GL_DEPTH_TEST);

    // Render components
    int vao = glGenVertexArrays();
    glBindVertexArray(vao);
    initializer.getRenderer().render(frame, initializer.getContext());
    glfwSwapBuffers(window);
    glfwPollEvents();

    // Process events
    initializer.getSystemEventProcessor().processEvents(frame, initializer.getContext());
    EventProcessorProvider.getInstance().processEvents();
    LayoutManager.getInstance().layout(frame);
    AnimatorProvider.getAnimator().runAnimations();
  }

  @Override
  void destroy(long window) {
    initializer.getRenderer().destroy();
    glfwFreeCallbacks(window);
    glfwDestroyWindow(window);
    glfwTerminate();
  }
}
