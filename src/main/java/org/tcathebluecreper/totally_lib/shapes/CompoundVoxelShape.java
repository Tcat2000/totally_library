package org.tcathebluecreper.totally_lib.shapes;

import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.joml.Vector3f;

public class CompoundVoxelShape {
    public VoxelShape shape = Shapes.block();

    private static final double EPSILON = 1e-9;

    public CompoundVoxelShape add(Vector3f bottom, Vector3f bottomX, Vector3f bottomY, Vector3f bottomXY, Vector3f top, Vector3f topX, Vector3f topY, Vector3f topXY) {
        Vector3f face0A = bottom;
        Vector3f face0B = bottomY;
        Vector3f face0C = bottomX;

//        Vector3f face1A = bottomXY;
//        Vector3f face1B = bottomY;
//        Vector3f face1C = bottomX;

        Vector3f face1A = top;
        Vector3f face1B = topY;
        Vector3f face1C = topX;

//        Vector3f face3A = topXY;
//        Vector3f face3B = topY;
//        Vector3f face3C = topX;

        Vector3f face2A = bottom;
        Vector3f face2B = bottomX;
        Vector3f face2C = top;

//        Vector3f face5A = bottomX;
//        Vector3f face5B = top;
//        Vector3f face5C = topX;

        Vector3f face3A = bottomX;
        Vector3f face3B = bottomXY;
        Vector3f face3C = topX;

//        Vector3f face7A = bottomX;
//        Vector3f face7B = topX;
//        Vector3f face7C = topXY;

        Vector3f face4A = bottomY;
        Vector3f face4B = bottomXY;
        Vector3f face4C = topY;

//        Vector3f face9A = bottomY;
//        Vector3f face9B = topY;
//        Vector3f face9C = topXY;

        Vector3f face5A = bottom;
        Vector3f face5B = bottomY;
        Vector3f face5C = top;

//        Vector3f face11A = bottomY;
//        Vector3f face11B = top;
//        Vector3f face11C = topY;

        Vector3f face0normal = face0B.sub(face0A).cross(face0C.set(face0A));
        float face0d = -(face0normal.x * face0A.x + face0normal.y * face0A.y + face0normal.z * face0A.z);

        Vector3f face1normal = face1B.sub(face1A).cross(face1C.set(face1A));
        float face1d = -(face1normal.x * face1A.x + face1normal.y * face1A.y + face1normal.z * face1A.z);

        Vector3f face2normal = face2B.sub(face2A).cross(face2C.set(face2A));
        float face2d = -(face2normal.x * face2A.x + face2normal.y * face2A.y + face2normal.z * face2A.z);

        Vector3f face3normal = face3B.sub(face3A).cross(face3C.set(face3A));
        float face3d = -(face3normal.x * face3A.x + face3normal.y * face3A.y + face3normal.z * face3A.z);

        Vector3f face4normal = face4B.sub(face4A).cross(face4C.set(face4A));
        float face4d = -(face4normal.x * face4A.x + face4normal.y * face4A.y + face4normal.z * face4A.z);

        Vector3f face5normal = face5B.sub(face5A).cross(face5C.set(face5A));
        float face5d = -(face5normal.x * face5A.x + face5normal.y * face5A.y + face5normal.z * face5A.z);


        for(int x = 0; x < 16; x++) {
            for(int y = 0; y < 16; y++) {
                for(int z = 0; z < 16; z++) {
                    Vector3f checkPoint = new Vector3f(x,y + 16,z);

                    float face0side = face0normal.x * x + face0normal.y * checkPoint.y + face0normal.z * checkPoint.z + face0d;
                    float face1side = face1normal.x * checkPoint.x + face1normal.y * checkPoint.y + face1normal.z * checkPoint.z + face1d;
                    float face2side = face2normal.x * checkPoint.x + face2normal.y * checkPoint.y + face2normal.z * checkPoint.z + face2d;
                    float face3side = face3normal.x * checkPoint.x + face3normal.y * checkPoint.y + face3normal.z * checkPoint.z + face3d;
                    float face4side = face4normal.x * checkPoint.x + face4normal.y * checkPoint.y + face4normal.z * checkPoint.z + face4d;
                    float face5side = face5normal.x * checkPoint.x + face5normal.y * checkPoint.y + face5normal.z * checkPoint.z + face5d;

                    if(face0side >= -EPSILON &&
                        face1side >= -EPSILON &&
                        face2side >= -EPSILON &&
                        face3side >= -EPSILON &&
                        face4side >= -EPSILON &&
                        face5side >= -EPSILON) {
                        shape = Shapes.or(shape, Shapes.box(checkPoint.x/16f, checkPoint.y/16f, checkPoint.z/16f, (checkPoint.x + 1)/16f, (checkPoint.y + 1)/16f, (checkPoint.z + 1)/16f));
                    }
                }
            }
        }

        return this;
    }
}
