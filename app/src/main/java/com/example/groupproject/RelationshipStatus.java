package com.example.groupproject;

import java.util.Arrays;

enum RelationshipStatus {
    INVISIBLE("Invisible"),                                     //Not Visible
    PENDING_VISIBLE("Pending response to enable viewing"),      //Not Visible
    VISIBLE("Visible"),                                         //Visible
    PENDING_FOLLOWING("Pending response to enable following"),  //Visible
    FOLLOWING("Following");                                     //Visible

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
