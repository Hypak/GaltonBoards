import org.joml.Vector2i;
import org.liquidengine.legui.DefaultInitializer;
import org.liquidengine.legui.animation.Animator;
import org.liquidengine.legui.animation.AnimatorProvider;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Frame;
import org.liquidengine.legui.component.Panel;
import org.liquidengine.legui.event.WindowSizeEvent;
import org.liquidengine.legui.listener.WindowSizeEventListener;
import org.liquidengine.legui.system.context.Context;
import org.liquidengine.legui.system.layout.LayoutManager;
import org.liquidengine.legui.system.renderer.Renderer;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.glfw.GLFWWindowCloseCallbackI;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.Objects;
import java.util.Vector;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class UserInterface {

  UserInterface(int width, int height) {
    this.width = width;
    this.height = height;
  }

  void addComponent(Object component) {
    if (!running) {
      try {
        panels.add((Panel) component);
      } catch (ClassCastException ignored) {}
      try {
        drawables.add((Drawable) component);
      } catch (ClassCastException ignored) {}
    }
  }

  public void start() {
    init();
    running = true;

    while (running) {
      assert context != null;
      context.updateGlfwWindow();
      Vector2i windowSize = context.getFramebufferSize();
      glClearColor(0.5f, 1, 0.5f, 0);
      glViewport(0, 0, windowSize.x, windowSize.y);
      glClear(GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);

      LayoutManager.getInstance().layout(frame, context);
      for (Drawable drawable : drawables) { // TODO fix this
        drawable.draw(window);
      }
      renderer.render(frame, context);

      glfwPollEvents();
      glfwSwapBuffers(window);

      animator.runAnimations();
      initializer.getSystemEventProcessor().processEvents(frame, context);
      initializer.getGuiEventProcessor().processEvents();

      Component mouseTargetGui = context.getMouseTargetGui();
      Component focusedGui = context.getFocusedGui();
    }

    // Free the window callbacks and destroy the window
    glfwFreeCallbacks(window);
    glfwDestroyWindow(window);

    // Terminate GLFW and free the error callback
    glfwTerminate();
    Objects.requireNonNull(glfwSetErrorCallback(null)).free();
  }

  private void init() {
    GLFWErrorCallback.createPrint(System.err).set();
    if ( !glfwInit() )
      throw new IllegalStateException("Unable to initialize GLFW");
    glfwDefaultWindowHints(); // optional, the current window hints are already the default
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation

    window = glfwCreateWindow(width, height, "Galton Boards!", NULL, NULL);
    if ( window == NULL )
      throw new RuntimeException("Failed to create the GLFW window");

    try ( MemoryStack stack = stackPush() ) {
      IntBuffer pWidth = stack.mallocInt(1); // int*
      IntBuffer pHeight = stack.mallocInt(1); // int*

      // Get the window size passed to glfwCreateWindow
      glfwGetWindowSize(window, pWidth, pHeight);

      // Get the resolution of the primary monitor
      GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

      // Center the window
      assert vidmode != null;
      glfwSetWindowPos(
          window,
          (vidmode.width() - pWidth.get(0)) / 2,
          (vidmode.height() - pHeight.get(0)) / 2
      );
    }

    glfwMakeContextCurrent(window);
    glfwSwapInterval(1);
    GL.createCapabilities();
    glfwShowWindow(window);

    frame = new Frame(width, height);
    for (Panel panel : panels) {
      panel.setFocusable(false);
      panel.getListenerMap().addListener(WindowSizeEvent.class, (WindowSizeEventListener) event -> panel.setSize(event.getWidth(), event.getHeight()));
      frame.getContainer().add(panel);
    }

    initializer = new DefaultInitializer(window, frame);

    GLFWKeyCallbackI exitOnEscCallback = (w1, key, code, action, mods) -> running = !(key == GLFW_KEY_ESCAPE && action != GLFW_RELEASE);
    GLFWWindowCloseCallbackI glfwWindowCloseCallbackI = w -> running = false;
    initializer.getCallbackKeeper().getChainKeyCallback().add(exitOnEscCallback);
    initializer.getCallbackKeeper().getChainWindowCloseCallback().add(glfwWindowCloseCallbackI);

    renderer = initializer.getRenderer();
    animator = AnimatorProvider.getAnimator();
    renderer.initialize();

    context = initializer.getContext();
  }

  private final Vector<Drawable> drawables = new Vector<>();
  private final Vector<Panel> panels = new Vector<>();

  private long window;
  private boolean running = false;

  private final int height;
  private final int width;

  private Context context;
  private Frame frame;
  private Renderer renderer;
  private DefaultInitializer initializer;
  private Animator animator;
}
