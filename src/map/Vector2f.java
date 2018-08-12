package map;

public class Vector2f {

    public float x;
    public float y;

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float calculateLength() {
        return (float)Math.sqrt(x*x + y*y);
    }

}
