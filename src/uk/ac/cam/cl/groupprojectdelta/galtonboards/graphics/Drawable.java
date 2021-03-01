package uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics;

import java.util.List;

public interface Drawable {

  List<Float> getMesh(float time);
  List<Float> getUV();
  List<Float> getColourTemplate();

}