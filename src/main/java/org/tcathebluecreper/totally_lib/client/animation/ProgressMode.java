package org.tcathebluecreper.totally_lib.client.animation;

import net.minecraft.util.StringRepresentable;

public enum ProgressMode implements StringRepresentable {
    ALWAYS("ALWAYS"),
    NEVER("NEVER"),
    WHEN_RUNNING("WHEN_RUNNING"),
    WHEN_STOPPED("WHEN_STOPPED"),
    WHEN_STUCK("WHEN_STUCK"),
    WHEN_NOT_RUNNING("WHEN_NOT_RUNNING"),
    SYNC_TO_PROGRESS("SYNC_TO_PROGRESS");

    final String name;
    ProgressMode(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
