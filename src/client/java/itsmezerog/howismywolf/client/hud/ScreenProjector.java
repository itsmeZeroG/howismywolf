package itsmezerog.howismywolf.client.hud;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.phys.Vec3;

public final class ScreenProjector {
    public record Point(float x, float y) {
    }

    private ScreenProjector() {
    }

    public static Point project(Vec3 worldPos) {
        Minecraft mc = Minecraft.getInstance();
        Camera camera = mc.gameRenderer.mainCamera();

        Vec3 relative = worldPos.subtract(camera.position());
        Vector3f relativeVec = new Vector3f((float) relative.x, (float) relative.y, (float) relative.z);

        new Quaternionf(camera.rotation()).conjugate().transform(relativeVec);

        if (relativeVec.z >= -0.05f)
            return null;

        double fovDegrees = mc.options.fov().get();
        float tanHalfFov = (float) Math.tan(Math.toRadians(fovDegrees) / 2.0);
        float aspect = (float) mc.getWindow().getWidth() / mc.getWindow().getHeight();

        float ndcX = relativeVec.x / (-relativeVec.z * tanHalfFov * aspect);
        float ndcY = relativeVec.y / (-relativeVec.z * tanHalfFov);

        float scaledX = (ndcX * 0.5f + 0.5f) * mc.getWindow().getGuiScaledWidth();
        float scaledY = (0.5f - ndcY * 0.5f) * mc.getWindow().getGuiScaledHeight();

        return new Point(scaledX, scaledY);
    }
}
