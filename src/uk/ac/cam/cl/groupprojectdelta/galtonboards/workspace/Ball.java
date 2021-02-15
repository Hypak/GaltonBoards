package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace;

import org.joml.Vector2f;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.Drawable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Ball implements Drawable {
    Vector2f position;
    List<LogicalLocation> logLocs; // all pegs/buckets the ball will encounter on its path
    int logLocI; // current index into logLocs
    Simulation simulation; // Simulation object that contains this ball

    public Ball(LogicalLocation startingPoint, Simulation sim) {
        simulation = sim;
        logLocI = 0;
        position = startingPoint.getWorldPos();
        logLocs = getLogicalPath(startingPoint);
    }

    public Vector2f getPosition() {
        return position;
    }

    public LogicalLocation getLogLoc() {
        return logLocs.get(logLocI);
    }

    List<LogicalLocation> getLogicalPath(LogicalLocation start) {
        List<LogicalLocation> locs = new ArrayList<>();
        locs.add(start);
        int i = 0;
        while (true) {
            if (locs.get(i) instanceof Bucket) {
                Bucket b = (Bucket) locs.get(i);
                if (b.getOutput() == null) { // this is the final bucket
                    return locs; // ONLY EXIT CONDITION - code doesn't get here, you have an infinite loop
                } else {
                    locs.add((LogicalLocation)b.getOutput());
                }
            } else if (locs.get(i) instanceof Peg) {
                Peg p = (Peg) locs.get(i);
                boolean takeLeft = Math.random() < p.leftProb();
                boolean pegIsNext = p.getLeftBucketIndex() == -1;
                if (pegIsNext) {
                    if (takeLeft) {
                        locs.add(p.getLeft());
                    } else {
                        locs.add(p.getRight());
                    }
                } else { // bucket is next
                    if (takeLeft) {
                        locs.add(p.getLeftBucket());
                    } else {
                        locs.add(p.getRightBucket());
                    }
                }
            }
            ++i;
        }
    }

    static Vector2f copyVec(Vector2f v) {
        return new Vector2f(v.x(), v.y());
    }

    void moveTowardsNextLoc(float f) {
        // f is the fraction of the distance between the current and next logical location to move now.
        if (logLocI == logLocs.size() - 1) return; // already in its final bucket
        LogicalLocation currentLoc = logLocs.get(logLocI);
        LogicalLocation nextLoc = logLocs.get(logLocI + 1);
        Vector2f toNextLogLoc = new Vector2f(0,0); // IMPORTANT: Vector2f not immutable, need to do all this initialisation and copying
        nextLoc.getWorldPos().sub(currentLoc.getWorldPos(), toNextLogLoc);
        float currentToNextDistance = toNextLogLoc.length();
        float distToMove = f * currentToNextDistance; // distance between current and next logical location
        Vector2f remainingTrip = new Vector2f(0,0);
        nextLoc.getWorldPos().sub(position, remainingTrip);
        float tripLeft = 1 - remainingTrip.length() / distToMove; // fraction of trip to next logLoc that's left
        Vector2f dir = new Vector2f(0,0);
        remainingTrip.normalize(dir);
        Vector2f moved = new Vector2f(0,0);
        dir.mul(Math.max(tripLeft, f) * currentToNextDistance, moved);
        position.add(moved);
        if (tripLeft < f) { // then we've reached the next logical location
            //System.out.println("Ball has reached next logical location: " + nextLoc + ", " + nextLoc.getWorldPos());
            if (nextLoc instanceof Bucket && !nextLoc.getBoard().isOpen()) {
                return; // we've reached the end of a bucket that's closed
            }
            position = nextLoc.getWorldPos();
            currentLoc.balls().remove(this);
            nextLoc.balls().add(this);
            logLocI += 1;
            moveTowardsNextLoc(f - tripLeft);
        }
    }

    public void update(float deltaTime) {
        moveTowardsNextLoc(deltaTime * simulation.speed);
        System.out.println(position);
    }

    @Override
    public List<Float> getMesh(float time) {
        List<Float> points = new ArrayList<>();
        Vector2f bound = new Vector2f();

        Vector2f dimensions = new Vector2f(0.1f, 0.1f);
        position.add(dimensions, bound);

        //  +----+
        //  |1 / |
        //  | / 2|
        //  +----+

        float z = 5;

        points = new ArrayList<>(Arrays.asList(
                // Face 1
                position.x, position.y, z,
                bound.x, position.y, z,
                bound.x, bound.y, z,

                // Face 2
                position.x, position.y, z,
                position.x, bound.y, z,
                bound.x, bound.y, z
        ));

        return points;
    }

    @Override
    public List<Float> getUV() {
        List<Float> UVs = List.of(
                // face 1
                0f,0f,
                1f,0f,
                1f,1f,
                // face 2
                0f,0f,
                0f,1f,
                1f,1f
        );
        return UVs;
    }


}
