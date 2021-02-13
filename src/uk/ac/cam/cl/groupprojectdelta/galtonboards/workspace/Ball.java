package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace;

import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class Ball {
    Vector2f position;
    List<LogicalLocation> logLocs; // all pegs/buckets the ball will encounter on its path
    LogicalLocation logLoc; // current peg/bucket
    Simulation simulation; // Simulation object that contains this ball

    public Ball(LogicalLocation startingPoint, Simulation sim) {
        simulation = sim;
        logLoc = startingPoint;
        position = startingPoint.getWorldPos();
        logLocs = getLogicalPath(startingPoint);
    }

    List<LogicalLocation> getLogicalPath(LogicalLocation start) {
        List<LogicalLocation> locs = List.of(start);
        // do stuff
        return locs;
    }
}
