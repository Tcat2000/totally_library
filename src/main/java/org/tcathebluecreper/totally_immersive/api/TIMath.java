package org.tcathebluecreper.totally_immersive.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.tcathebluecreper.totally_immersive.lib.AnimationUtils;

public class TIMath {
    public static Vec3 curve(BlockPos pos0, Vec3 pos0vector, BlockPos pos1, Vec3 pos1vector, float amount) {
        return curve(new Vec3(pos0.getX(), pos0.getY(), pos0.getZ()), pos0vector, new Vec3(pos1.getX(), pos1.getY(), pos1.getZ()), pos1vector, amount);
    }
    public static Vec3 curve(Vec3 pos0, Vec3 pos0vector, Vec3 pos1, Vec3 pos1vector, float amount) {
        return lerp3D(lerp3D(pos0, pos0.add(pos0vector), amount), lerp3D(pos1.add(pos1vector), pos1, amount), amount);
    }

    public static Vec3 lerp3D(Vec3 pos0, Vec3 pos1, float value) {
        return new Vec3(AnimationUtils.lerp(pos0.x, pos1.x, value), AnimationUtils.lerp(pos0.y, pos1.y, value), AnimationUtils.lerp(pos0.z, pos1.z, value));
    }

    public static Vec3 subVec(Vec3 a, Vec3 b) {
        return new Vec3(a.x - b.x, a.y - b.y, a.z - b.z);
    }
    public static double calculateAngle(Vec3 vectorA, Vec3 vectorB) {
        double dotProduct = 0;
        dotProduct += vectorA.x * vectorB.x;
        dotProduct += vectorA.y * vectorB.y;
        dotProduct += vectorA.z * vectorB.z;

        double magnitudeA = 0;
        magnitudeA += Math.pow(vectorA.x, 2);
        magnitudeA += Math.pow(vectorA.y, 2);
        magnitudeA += Math.pow(vectorA.z, 2);
        magnitudeA = Math.sqrt(magnitudeA);

        double magnitudeB = 0;
        magnitudeA += Math.pow(vectorB.x, 2);
        magnitudeA += Math.pow(vectorB.y, 2);
        magnitudeA += Math.pow(vectorB.z, 2);
        magnitudeB = Math.sqrt(magnitudeB);


        double cosTheta = dotProduct / (magnitudeA * magnitudeB);

        //Ensure cosTheta is within the range [-1, 1]
        cosTheta = Math.max(-1, Math.min(1, cosTheta));


        return Math.acos(cosTheta);
    }
    public static float getAngle(Vec3 vecA, Vec3 vecB) {
        return (float) Math.toDegrees(Math.atan2(vecA.z - vecB.z, vecA.x - vecB.x));
    }

    public static double vectorDist(Vec3 vec1, Vec3 vec2) {
        return Math.sqrt(Math.pow(vec1.x - vec2.x, 2) + Math.pow(vec1.y - vec2.y, 2) + Math.pow(vec1.z - vec2.z, 2));
    }
    public static double magnitude(Vec3 vec) {
        return Math.max(vec.x, Math.max(vec.y, vec.z));
    }
    private static final double EPSILON = 1e-6;
    public static Quaternionf getRotationQuaternion(Vec3 fromVector, Vec3 toVector) {
        // 1. Normalize the vectors
        Vec3 v1 = fromVector.normalize();
        Vec3 v2 = toVector.normalize();

        // If either vector was a zero vector, return identity as no rotation is meaningful
        if (magnitude(v1) == 0 || magnitude(v2) == 0) {
            return new Quaternionf(1, 0, 0, 0);
        }

        // 2. Calculate dot product and angle
        double dot = v1.dot(v2);

        // Clamp dot product to prevent floating point errors with acos
        if (dot > 1.0) dot = 1.0;
        if (dot < -1.0) dot = -1.0;

        // 3. Handle special cases for parallel and anti-parallel vectors
        if (Math.abs(dot - 1.0) < EPSILON) {
            // Vectors are parallel (already aligned or very close)
            return new Quaternionf(1, 0, 0, 0); // Identity quaternion (no rotation needed)
        }

        if (Math.abs(dot + 1.0) < EPSILON) {
            // Vectors are anti-parallel (180-degree rotation needed)
            // Choose an arbitrary axis perpendicular to v1
            Vec3 axis;
            // If v1 is approximately aligned with X-axis, use Y-axis for cross product
            if (Math.abs(v1.x) > 1.0 - EPSILON || Math.abs(v1.x) < EPSILON && Math.abs(v1.y) < EPSILON) {
                // If v1 is approximately (1,0,0) or (-1,0,0), cross with Y-axis
                // If v1 is (0,0,0) or almost (0,0,1) or (0,0,-1)
                axis = v1.cross(new Vec3(0, 1, 0)); // Cross with Y-axis
            } else {
                // Otherwise, cross with X-axis
                axis = v1.cross(new Vec3(1, 0, 0)); // Cross with X-axis
            }
            axis = axis.normalize(); // Ensure axis is a unit vector

            // For 180-degree rotation (angle = PI), cos(PI/2) = 0, sin(PI/2) = 1
            return new Quaternionf(0, axis.x, axis.y, axis.z);
        }

        // 4. General case: Calculate rotation axis and angle
        Vec3 axis = v1.cross(v2).normalize(); // Rotation axis (normalized)
        double angle = Math.acos(dot);           // Rotation angle

        // 5. Construct the quaternion
        double halfAngle = angle / 2.0;
        double sinHalfAngle = Math.sin(halfAngle);
        double cosHalfAngle = Math.cos(halfAngle);

        return new Quaternionf(
            cosHalfAngle,
            axis.x * sinHalfAngle,
            axis.y * sinHalfAngle,
            axis.z * sinHalfAngle
        ).normalize(); // Normalize final quaternion for safety, though it should be already
    }
}
