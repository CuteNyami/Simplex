package tk.simplexclient.render;

import tk.simplexclient.api.IRenderHandler;
import tk.simplexclient.api.RenderObject;

import static org.lwjgl.opengl.GL11.*;

public class RenderManager implements IRenderHandler {

    @Override
    public void renderElement(RenderObject object) {
        object.render();
    }

    @Override
    public void renderRoundedTexture(float x, float y, float width, float height, float radius) {
        glPushAttrib(0);
        glScaled(0.5D, 0.5D, 0.5D);
        x *= 2.0D;
        y *= 2.0D;
        width *= 2.0D;
        height *= 2.0D;
        glEnable(GL_BLEND);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_LINE_SMOOTH);
        glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        glBegin(GL_POLYGON);
        int i;
        for (i = 0; i <= 90; i += 3) {

            double vx = x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D;
            double vy = y + radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D;

            glTexCoord2d(vx / width, vy / height);
            glVertex2d(vx, vy);
        }
        for (i = 90; i <= 180; i += 3) {

            double vx = x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D;
            double vy = height - radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D;

            glTexCoord2d(vx / width, vy / height);
            glVertex2d(vx, vy);
        }
        for (i = 0; i <= 90; i += 3) {

            double vx = width - radius + Math.sin(i * Math.PI / 180.0D) * radius;
            double vy = height - radius + Math.cos(i * Math.PI / 180.0D) * radius;

            glTexCoord2d(vx / width, vy / height);
            glVertex2d(vx, vy);
        }
        for (i = 90; i <= 180; i += 3) {
            double vx = width - radius + Math.sin(i * Math.PI / 180.0D) * radius;
            double vy = y + radius + Math.cos(i * Math.PI / 180.0D) * radius;

            glTexCoord2d(vx / width, vy / height);
            glVertex2d(vx, vy);
        }
        glEnd();
        glBegin(GL_LINE_LOOP);
        for (i = 0; i <= 90; i += 3) {

            double vx = x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D;
            double vy = y + radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D;

            glTexCoord2d(vx / width, vy / height);
            glVertex2d(vx, vy);
        }
        for (i = 90; i <= 180; i += 3) {

            double vx = x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D;
            double vy = height - radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D;

            glTexCoord2d(vx / width, vy / height);
            glVertex2d(vx, vy);
        }
        for (i = 0; i <= 90; i += 3) {

            double vx = width - radius + Math.sin(i * Math.PI / 180.0D) * radius;
            double vy = height - radius + Math.cos(i * Math.PI / 180.0D) * radius;

            glTexCoord2d(vx / width, vy / height);
            glVertex2d(vx, vy);
        }
        for (i = 90; i <= 180; i += 3) {
            double vx = width - radius + Math.sin(i * Math.PI / 180.0D) * radius;
            double vy = y + radius + Math.cos(i * Math.PI / 180.0D) * radius;

            glTexCoord2d(vx / width, vy / height);
            glVertex2d(vx, vy);
        }
        glEnd();
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glDisable(GL_LINE_SMOOTH);
        glEnable(GL_TEXTURE_2D);
        glScaled(2.0D, 2.0D, 2.0D);
        glPopAttrib();
        glLineWidth(1);
    }
}
