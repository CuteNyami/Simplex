package tk.simplexclient.api;

import java.awt.*;

public abstract class RenderObject {

    protected float x, y, width, height;

    protected Color color;

    protected Object[] args;

    protected RenderObject(float x, float y, float width, float height, Color color, Object... args) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.args = args;
    }

    public abstract void render();

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Color getColor() {
        return color;
    }

    public Object[] getArgs() {
        return args;
    }
}
