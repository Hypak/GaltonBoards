package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace;

import org.joml.Vector2f;
import org.joml.Vector3f;
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
    // List of same length as lgoLocs with -1 if left direction taken from peg, 0 if not peg, 1 if right direction taken:
    List<Integer> pegChoices = new ArrayList<>();
    int logLocI; // current index into logLocs
    Simulation simulation; // Simulation object that contains this ball
    String tag = "untagged";

    static final float RADIUS = 0.1f;

    public Ball(LogicalLocation startingPoint, Simulation sim) {
        simulation = sim;
        logLocI = 0;
        position = startingPoint.getWorldPos();
        setLogicalPath(startingPoint);
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String newTag) {
        this.tag = newTag;
    }

    public Vector2f getPosition() {
        return position;
    }

    public LogicalLocation getLogLoc() {
        return logLocs.get(logLocI);
    }

    public boolean isLiquified() {
        //if (logLocs.get(logLocI) instanceof ColumnBottom) return true; // probably not an ideal heuristic
        if (logLocs.get(logLocI) instanceof ColumnBottom) {
            if (position.y < logLocs.get(logLocI).getWorldPos().y) return false;
            return true;
        }
        return false;
    }

    void setLogicalPath(LogicalLocation start) {
        //System.out.println("Starting path calculation ... ");
        logLocs = new ArrayList<>();
        logLocs.add(start);
        int i = 0;
        while (true) {
            //System.out.println(locs.get(i));
            if (logLocs.get(i) instanceof ColumnBottom) {
                pegChoices.add(0);
                ColumnBottom cb = (ColumnBottom) logLocs.get(i);
                // System.out.println(b + ", " + b.getWorldPos());
                if (cb.getBucket().getOutput() == null) { // this is the final bucket
                    //System.out.println("PATH FOUND");
                    return; // ONLY EXIT CONDITION - code doesn't get here, you have an infinite loop
                } else {
                    logLocs.add((LogicalLocation)cb.getBucket().getOutput().getRootPeg());
                }
            } else if (logLocs.get(i) instanceof ColumnTop) {
                pegChoices.add(0);
                ColumnTop ct = (ColumnTop) logLocs.get(i);
                logLocs.add(ct.getColumnBottom());
            } else if (logLocs.get(i) instanceof Peg) {
                Peg p = (Peg) logLocs.get(i);
                boolean takeLeft = Math.random() < p.leftProb();
                if (takeLeft) {
                    pegChoices.add(-1);
                } else {
                    pegChoices.add(1);
                }
                if (i == 0) updateTag();
                boolean pegIsNext = p.getLeftColumnIndex() == -1;
                if (pegIsNext) {
                    if (takeLeft) {
                        logLocs.add(p.getLeft());
                    } else {
                        logLocs.add(p.getRight());
                    }
                } else { // bucket is next
                    if (takeLeft) {
                        logLocs.add(p.getLeftColumn());
                    } else {
                        logLocs.add(p.getRightColumn());
                    }
                }
            }
            ++i;
        }
    }

    static Vector2f copyVec(Vector2f v) {
        return new Vector2f(v.x(), v.y());
    }

    private void updateTag() {
        LogicalLocation logLoc = logLocs.get(logLocI);
        if (logLoc.getGivenTags().size() != 0) {
            if (logLoc instanceof Peg) {
                if (pegChoices.get(logLocI) == -1) { // left path was taken from this peg
                    tag = logLoc.getGivenTags().get(0);
                } else { // right path was taken from this peg
                    tag = logLoc.getGivenTags().get(1);
                }
            } else {
                tag = logLoc.getGivenTags().get(0);
            }
        }
    }

    private void switchToNextLogLoc() {
        /*if (simulation.rewinding()) {
            logLocs.get(logLocI + 1).removeBall(this);
            logLocs.get(logLocI).addBall(this);
            updateTag();
            logLocI -= 1;
            return;
        }*/
        logLocs.get(logLocI).removeBall(this);
        logLocs.get(logLocI + 1).addBall(this);
        updateTag();
        logLocI += 1;
    }

    void moveTowardsNextLoc(float f) {
        // f is the fraction of the distance between the current and next logical location to move now.
        //if (!simulation.rewinding() && logLocI == logLocs.size() - 1) return; // already in its final bucket
        //if (simulation.rewinding() && logLocI == 0) return;
        if (logLocI == logLocs.size() - 1) return; // already in its final bucket
        int currI = logLocI;
        int nextI = logLocI + 1;
        /*if (simulation.rewinding()) {
            nextI = logLocI;
            currI = logLocI - 1;
        }
        if (nextI < 0) return;*/
        LogicalLocation currentLoc = logLocs.get(currI);
        LogicalLocation nextLoc = logLocs.get(nextI);

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
        if (isLiquified()) return new ArrayList<>();
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
        if (isLiquified()) return new ArrayList<>();
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
        if (isLiquified()) return new ArrayList<>();

        Vector3f rgb = simulation.getTagColour(tag);
        final float red = rgb.x;
        final float green = rgb.y;
        final float blue = rgb.z;

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
