package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace;

import java.util.ArrayList;
import java.util.List;

public class Simulation {
    public float speed = 1f;
    private List<Ball> balls;
    Board rootBoard;

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
}
