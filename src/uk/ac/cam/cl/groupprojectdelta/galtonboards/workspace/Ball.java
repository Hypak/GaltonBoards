package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace;

import org.joml.Vector2f;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.Drawable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.ColumnBottom;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.ColumnTop;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.Peg;

public class Ball implements Drawable {
    Vector2f position;
    List<LogicalLocation> logLocs; // all pegs/buckets the ball will encounter on its path
    int logLocI; // current index into logLocs
    Simulation simulation; // Simulation object that contains this ball
    String tag = "untagged";

    static final float RADIUS = 0.1f;

    public Ball(LogicalLocation startingPoint, Simulation sim) {
        simulation = sim;
        logLocI = 0;
        position = startingPoint.getWorldPos();
        logLocs = getLogicalPath(startingPoint);
    }

    public String getTag() {
        return tag;
    }

    public Vector2f getPosition() {
        return position;
    }

    public LogicalLocation getLogLoc() {
        return logLocs.get(logLocI);
    }

    public boolean isLiquified() {
        if (logLocs.get(logLocI) instanceof ColumnBottom) return true; // probably not an ideal heuristic
        return false;
    }

    List<LogicalLocation> getLogicalPath(LogicalLocation start) {
        List<LogicalLocation> locs = new ArrayList<>();
        locs.add(start);
        int i = 0;
        while (true) {
            //System.out.println(locs.get(i));
            if (locs.get(i) instanceof ColumnBottom) {
                ColumnBottom cb = (ColumnBottom) locs.get(i);
                // System.out.println(b + ", " + b.getWorldPos());
                if (cb.getBucket().getOutput() == null) { // this is the final bucket
                    return locs; // ONLY EXIT CONDITION - code doesn't get here, you have an infinite loop
                } else {
                    locs.add((LogicalLocation)cb.getBucket().getOutput().getRootPeg());
                }
            } else if (locs.get(i) instanceof ColumnTop) {
                ColumnTop ct = (ColumnTop) locs.get(i);
                locs.add(ct.getColumnBottom());
            } else if (locs.get(i) instanceof Peg) {
                Peg p = (Peg) locs.get(i);
                boolean takeLeft = Math.random() < p.leftProb();
                boolean pegIsNext = p.getLeftColumnIndex() == -1;
                if (pegIsNext) {
                    if (takeLeft) {
                        locs.add(p.getLeft());
                    } else {
                        locs.add(p.getRight());
                    }
                } else { // bucket is next
                    if (takeLeft) {
                        locs.add(p.getLeftColumn());
                    } else {
                        locs.add(p.getRightColumn());
                    }
                }
            }
            ++i;
        }
    }

    static Vector2f copyVec(Vector2f v) {
        return new Vector2f(v.x(), v.y());
    }

    void dummyMoveTowardsNextLoc(float f) {
        position.add(new Vector2f(0f, 0.5f));
    }

    private void switchToNextLogLoc() {
        logLocs.get(logLocI).removeBall(this);
        logLocs.get(logLocI + 1).addBall(this);
        logLocI += 1;
    }

    void moveTowardsNextLoc(float f) {
        // f is the fraction of the distance between the current and next logical location to move now.
        if (logLocI == logLocs.size() - 1) return; // already in its final bucket
        LogicalLocation currentLoc = logLocs.get(logLocI);
        LogicalLocation nextLoc = logLocs.get(logLocI + 1);

        Vector2f toMove = new Vector2f(0,0);
        toMove.add(nextLoc.getWorldPos());
        toMove.sub(currentLoc.getWorldPos());
        Vector2f currentToNext = copyVec(toMove);
        float currentToNextDistance = toMove.length();
        toMove.mul(f);
        Vector2f remainingTrip = new Vector2f(0,0); // vector from ball position to next log loc
        remainingTrip.add(nextLoc.getWorldPos());
        remainingTrip.sub(position);
        if (remainingTrip.dot(currentToNext) < toMove.dot(currentToNext)) {
            // current leg of the trip takes the ball all the way to the next logical location in its path
            position = copyVec(nextLoc.getWorldPos());
            switchToNextLogLoc();
            moveTowardsNextLoc((toMove.length() - remainingTrip.length())/currentToNextDistance);
        } else {
            // ball can move toMove without reaching the next logical location
            position.add(toMove);
        }
    }

    public void update(float deltaTime) {
        moveTowardsNextLoc(deltaTime * simulation.speed);
    }

    @Override
    public List<Float> getMesh(float time) {
        List<Float> points;
        Vector2f bound = new Vector2f();

        Vector2f dimensions = new Vector2f(RADIUS);
        position.add(dimensions, bound);

        //  +----+
        //  |1 / |
        //  | / 2|
        //  +----+

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
        final float top = 0.75f;
        final float bottom = 1f;
        final float left = 0.75f;
        final float right = 1f;

        return List.of(
                // face 1
                top,left,
                bottom,left,
                bottom,right,
                // face 2
                top,left,
                top,right,
                bottom,right
        );
    }

    @Override
    public List<Float> getColourTemplate() {
        // todo: colour balls based on colour tagging
        final boolean isLow = position.x < 0;
        final float red = isLow? 0 : 1f;
        final float green = 1;
        final float blue = red;

        return List.of(
                // face 1
                red, green, blue,
                red, green, blue,
                red, green, blue,
                // face 2
                red, green, blue,
                red, green, blue,
                red, green, blue
        );
    }
}
