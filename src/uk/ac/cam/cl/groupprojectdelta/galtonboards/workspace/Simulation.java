package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace;

import java.util.ArrayList;
import java.util.List;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.Drawable;

public class Simulation implements Drawable {
    public float speed = 4f;
    private List<Ball> balls;
    Board rootBoard;
    private final float timeBetweenBalls = 0.005f;
    private float timeTillNextBall = 0;

    public Simulation(Board startingBoard) {
        rootBoard = startingBoard;
        balls = new ArrayList<>();
    }

    public List<Ball> getBalls() {
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
