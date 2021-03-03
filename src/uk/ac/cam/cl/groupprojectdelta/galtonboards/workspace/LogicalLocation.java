package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace;

import org.joml.Vector2f;

import java.util.List;
import java.util.Set;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.Board;

public interface LogicalLocation {
    // Represents a logical node (peg or bucket) in the path that a ball takes.
    // Implemented by Peg and Bucket.
    abstract Vector2f getWorldPos();

    abstract Set<Ball> balls();

    abstract void addBall(Ball ball);

    abstract void removeBall(Ball ball);

    abstract Board getBoard();

    abstract List<String> getGivenTags();

    abstract void setGivenTags(List<String> tags);

    abstract void clearGivenTags();
}
