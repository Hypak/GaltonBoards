import org.liquidengine.legui.component.*;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.listener.MouseClickEventListener;

class TestRectangle extends DrawableRectangle {
  TestRectangle(float posX, float posY, float sizeX, float sizeY) {
    super(posX, posY, sizeX, sizeY);
    setColor(0.0f, green, blue);
  }
  private float green = 0.0f;
  private float blue = 1.0f;
}

public class Main {
  public static void main(String[] args) {
    UserInterface UI = new UserInterface(800, 800);

    // These aren't working right now...
    UI.addComponent(new TestRectangle(0.4f, 0.4f, 0.1f, 0.1f));
    UI.addComponent(new TestRectangle(0.2f, 0.6f, 0.1f, 0.1f));

    Panel topPanel = new Panel(0, 0, 800, 200);
    Panel sidePanel = new Panel(600, 200, 200, 600);
    topPanel.add(new CheckBox("Galton Boards", 50, 50, 200, 20));
    topPanel.add(new CheckBox("Galton Boards", 50, 100, 200, 20));

    Button clickMe = new Button("click me", 50, 50, 50, 50);
    clickMe.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) event -> {
      if (event.getAction().equals(MouseClickEvent.MouseClickAction.CLICK)) {
        System.out.println("Galton Boards");
      }
    });
    sidePanel.add(clickMe);

    UI.addComponent(topPanel);
    UI.addComponent(sidePanel);

    UI.start();
  }
}
