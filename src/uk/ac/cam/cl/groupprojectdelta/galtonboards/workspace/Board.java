package uk.ac.cam.cl.groupprojectdelta.galtonboards.workspace;

import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private Vector2f worldPos;
    private int isoGridWidth;
    private List<Peg> pegs;
    private List<Bucket> buckets;
    private List<Integer> bucketWidths;

    public Board() {
        this.worldPos = new Vector2f(0,0);
        this.isoGridWidth = 0;
        this.pegs = new ArrayList<>();
        this.buckets = List.of(new Bucket(this));
        this.bucketWidths = List.of(1);
    }

    public Peg getRootPeg() {
        return pegs.get(0);
    }

    public Bucket getBucket(int x) {
        if(x > isoGridWidth + 1 || x < 0) {
            // Error
            return null;
        }
        return buckets.get(0);
    }

    public Vector2f getWorldPos() {
        return worldPos;
    }

}
