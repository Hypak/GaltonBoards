package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace;

import org.joml.Vector2f;

public interface LogicalLocation {
    // peg & bucket should implement this
    abstract Vector2f getWorldPos();
}
