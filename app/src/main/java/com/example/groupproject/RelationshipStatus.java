package com.example.groupproject;

import java.util.Arrays;

enum RelationshipStatus {
    INVISIBLE("Invisible"),
    PENDING_VISIBLE("Pending response to enable viewing"),
    VISIBLE("Visible"),
    PENDING_FOLLOWING("Pending response to enable following"),
    FOLLOWING("Following");

    private String str;

    RelationshipStatus(String str) {
        this.str = str;
    }

    @Override public String toString() {
        return str;
    }

    public static String[] getNames() {
        return Arrays.toString(values()).replaceAll("^.|.$", "").split(", ");
    }
}
