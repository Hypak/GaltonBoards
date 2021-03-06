package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Vector3f;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.graphics.Drawable;
import uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace.board.Board;

public class Simulation implements Drawable {
    public static Simulation simulation;

    private Configuration configuration;
    //private enum SimulationState {Running, Rewind, Paused, Stopped};
    private enum SimulationState {Running, Rewind, Paused, Stopped};

    public float speed = 4f;
    private List<Ball> balls;
    public float timeBetweenBalls = 0.005f;
    public float timeTillNextBall = 0;
    private SimulationState simulationState;
    private float bucketScale = 1f; // the number of balls that will fill a bucket

    public Map<String, Vector3f> tagColours = new HashMap<>();

    public void run() {
        /*if (simulationState == SimulationState.Running) {
            simulationState = SimulationState.Rewind;
        } else simulationState = SimulationState.Running;*/
        simulationState = SimulationState.Running;
    }

    public boolean rewinding() {
        //if (simulationState == SimulationState.Rewind) return true;
        return false;
    }

    public void pause() {
        simulationState = SimulationState.Paused;
    }

    public void stop() {
        simulationState = SimulationState.Stopped;
        for (Ball ball : balls) {
            ball.getLogLoc().removeBall(ball);
        }
        bucketScale = 1f;
        balls.clear();
    }

    public Simulation(Configuration configuration) {
        this.configuration = configuration;
        configuration.setSimulation(this);
        balls = new ArrayList<>();
        tagColours.put("untagged", new Vector3f(0.4f, 0f, 0.4f));
        tagColours.put("testtag1", new Vector3f(1f,0f,0f));
        tagColours.put("testtag2", new Vector3f(0f, 1f, 0f));
        tagColours.put("factory1", new Vector3f(0f, 0f, 1f));
        tagColours.put("factory2", new Vector3f(1f, 0.5f, 0f));
    }

    public void addBallTag(String tag, Vector3f colour) {
        tagColours.put(tag, colour);
    }

    public void addBallTag(String tag) {
        addBallTag(tag, new Vector3f((float)Math.random(), (float)Math.random(), (float)Math.random()));
    }

    public Vector3f getTagColour(String tag) {
        return tagColours.getOrDefault(tag, new Vector3f(1f,0f,1f));
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

    public void deleteBall(Ball ball) {
        this.balls.remove(ball);
    }

    public Board getRootBoard() {
        return configuration.getStartBoard();
    }

    public float getBucketScale() {
        return bucketScale;
    }

    public void enlargeBuckets() {
        bucketScale *= 2; // what to do to all bucket scales in the simulation when a bucket fills up
    }

    public void update(float deltaTime) {
        /*if (simulationState == SimulationState.Running || simulationState == SimulationState.Rewind) {
            if (simulationState == SimulationState.Rewind) deltaTime *= -1;
            for (Ball ball : balls) {
                ball.update(deltaTime);
            }
            if (simulationState == SimulationState.Running) {
                timeTillNextBall -= deltaTime;
                while (timeTillNextBall < 0) {
                    spawnBallAtRoot();
                    timeTillNextBall += timeBetweenBalls;
                }
            }
        }*/
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
