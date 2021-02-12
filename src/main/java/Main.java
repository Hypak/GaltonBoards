import static java.lang.Math.max;
import static java.lang.Math.min;

class TestButton extends ClickableDrawableRectangle {

  TestButton(float posX, float posY, float sizeX, float sizeY) {
    super(posX, posY, sizeX, sizeY);
    setColor(0.0f, green, blue);
  }

  @Override
  public void onClick() {
    // A few random actions for quick testing
    green = 1.0f - green;
    blue = 1.0f - blue;
    setColor(0.0f, green, blue);
    setPosX(getPosX() * 0.75f + 0.5f * 0.25f);
    setPosY(getPosY() * 0.75f + 0.5f * 0.25f);
    setSizeX(getSizeX() * 0.9f);
    setSizeY(getSizeY() * 0.9f);
  }

  private float green = 0.0f;
  private float blue = 1.0f;

}

public class Main {
  public static void main(String[] args) {
    UserInterface UI = new UserInterface();
    UI.addComponent(new TestButton(0.1f, 0.1f, 0.3f, 0.3f));
    UI.addComponent(new TestButton(0.6f, 0.6f, 0.3f, 0.3f));
    UI.start();
  }

}