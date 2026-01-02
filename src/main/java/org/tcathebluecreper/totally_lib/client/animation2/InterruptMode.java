package org.tcathebluecreper.totally_lib.client.animation2;

public enum InterruptMode {
    STOP, // Stops the animation immediately
    FINISH, // Stops the animation after it finishes
    REVERSE, // Reverses the animation to the start
    CLOSEST, // Finishes or reverses the animation, whichever is quicker.
}
