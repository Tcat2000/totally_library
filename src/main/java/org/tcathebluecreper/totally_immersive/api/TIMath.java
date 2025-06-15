package org.tcathebluecreper.totally_immersive.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
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
}
