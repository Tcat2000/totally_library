package org.tcathebluecreper.totally_lib.client.animation2;

public enum AnimationTrigger {
    MACHINE_STARTS, // Triggers when the machine starts running.
    MACHINE_STOPS, // Triggers when it stops.
    MACHINE_STUCK, // Triggers when the stuck flag is changed to true.
    MACHINE_UNSTUCK, // Triggers when the stuck flag is changed to false.
}
