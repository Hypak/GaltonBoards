package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace;

import java.util.ArrayList;
import java.util.List;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.Drawable;

public class Simulation implements Drawable {
    public float speed = 1f;
    private List<Ball> balls;
    Board rootBoard;
    private final float timeBetweenBalls = 4;
    private float timeTillNextBall = 0;

    public Simulation(Board startingBoard) {
        rootBoard = startingBoard;
        balls = new ArrayList<>();
    }

    public List<Ball> getBalls() {
        return balls;
    }

    public List<Ball> getBallsAfter(float dt) {
        for (Ball ball : balls) {
            ball.moveTowardsNextLoc(speed * dt);
            /* The argument to moveTowardsNextLoc is the fraction of the distance
               between the previous and the next logical location that the ball
               should move. So if it's 1, the ball moves from one logical location
               to the next in one time unit.
             */
        }
        return balls;
    }

    public void spawnBall(LogicalLocation startingPosition) {
        Ball ball = new Ball(startingPosition, this);
        balls.add(ball);
    }

    public void spawnBallAtRoot() {
        spawnBall(rootBoard.getRootPeg());
    }

    public Board getRootBoard() {
        return rootBoard;
    }

    public void update(float deltaTime) {
        for (Ball ball : balls) {
            ball.update(deltaTime);
        }
        timeTillNextBall -= deltaTime;
        while (timeTillNextBall < 0) {
            spawnBallAtRoot();
            timeTillNextBall += timeBetweenBalls;
        }
    }

    @Override
    public List<Float> getMesh(float time) {
        List<Float> mesh = new ArrayList<>();
        for (Ball ball : balls) {
            mesh.addAll(ball.getMesh(time));
        }
        return mesh;
    }

    @Override
    public List<Float> getUV() {
        List<Float> uv = new ArrayList<>();
        for (Ball ball : balls) {
            uv.addAll(ball.getUV());
        }
        return uv;
    }
}
