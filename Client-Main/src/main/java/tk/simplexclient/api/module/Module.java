package tk.simplexclient.api.module;

import lombok.Getter;

public abstract class Module {

    @Getter
    protected final String name;

    @Getter
    protected final float x, y;

    @Getter
    protected final boolean enabled;

    public Module(String name) {
        this.name = name;
        this.x = 0;
        this.y = 0;
        this.enabled = true;
    }

    /**
     * The width of the module background
     * <p>
     * It's needed for the Module Dragging UI
     *
     * @return The width of the Module Background
     */
    public abstract float getWidth();

    /**
     * The height of the module background
     * <p>
     * It's needed for the Module Dragging UI
     *
     * @return The height of the Module Background
     */
    public abstract float getHeight();
}
