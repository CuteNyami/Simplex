package tk.simplexclient.api;

public interface IRenderHandler {

    /**
     * Render an element on the screen
     *
     * @param object The given element
     */
    void renderElement(RenderObject object);

    void renderRoundedTexture(float x, float y, float width, float height, float radius);

}
