package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace;

import java.util.List;

public class Simulation {
    public float speed = 1f;
    private List<Ball> balls;
    Board rootBoard;

    public Simulation(Board startingBoard) {
        rootBoard = startingBoard;
    }

    public List<Ball> getBalls() {
        return balls;
    }

    public List<Ball> getBallsAfter(float dt) {
        // do stuff
        return balls;
    }

    public void spawnBall(LogicalLocation startingPosition) {
        Ball ball = new Ball(startingPosition, this);
        balls.add(ball);
    }

    public void spawnBallAtRoot() {
        //spawnBall(rootBoard.getRootPeg());
    }
}
