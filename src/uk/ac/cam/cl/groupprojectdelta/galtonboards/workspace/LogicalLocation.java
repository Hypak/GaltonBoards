package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace;

import org.joml.Vector2f;

import java.util.Set;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.Board;

public interface LogicalLocation {
    // Represents a logical node (peg or bucket) in the path that a ball takes.
    // Implemented by Peg and Bucket.
    abstract Vector2f getWorldPos();

    abstract Set<Ball> balls();

    abstract Board getBoard();
}
