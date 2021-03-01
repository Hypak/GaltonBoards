package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace;

import java.util.ArrayList;
import java.util.List;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.Drawable;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.Board;

public class Simulation implements Drawable {
    private Configuration configuration;
    private enum SimulationState {Running, Paused, Stopped};

    public float speed = 4f;
    private List<Ball> balls;
    private final float timeBetweenBalls = 0.005f;
    private float timeTillNextBall = 0;
    private SimulationState simulationState;

    public void run() {
        simulationState = SimulationState.Running;
    }

    public void pause() {
        simulationState = SimulationState.Paused;
    }

    public void stop() {
        simulationState = SimulationState.Stopped;
        balls.clear();
    }

    public Simulation(Configuration configuration) {
        this.configuration = configuration;
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
        spawnBall(getRootBoard().getRootPeg());
    }

    public Board getRootBoard() {
        return configuration.getStartBoard();
    }

    public void update(float deltaTime) {
        if (simulationState == SimulationState.Running) {
            for (Ball ball : balls) {
                ball.update(deltaTime);
            }
            timeTillNextBall -= deltaTime;
            while (timeTillNextBall < 0) {
                spawnBallAtRoot();
                timeTillNextBall += timeBetweenBalls;
            }
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

    @Override
    public List<Float> getColourTemplate() {
        List<Float> ct = new ArrayList<>();
        for (Ball ball : balls) {
            ct.addAll(ball.getColourTemplate());
        }
        return ct;
    }
}
