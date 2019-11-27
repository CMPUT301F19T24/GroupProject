package com.example.groupproject.data.relations;

import java.util.Arrays;

public enum RelationshipStatus {
    INVISIBLE("Invisible", "You can not see the posts of this user"),                                     //Not Visible
    PENDING_VISIBLE("Pending Visible", "You are waiting on a response from this user, inorder to see their posts"),      //Not Visible
    VISIBLE("Visible", "You can see their posts"),                                         //Visible
    PENDING_FOLLOWING("Pending Following", "You are waiting on a response from this user, inorder to be subscribed to their posts"),  //Visible
    FOLLOWING("Following", "You are subscribed to their posts");                                     //Visible

    private String str;
    private String desc;

    RelationshipStatus(String str, String desc) {
        this.str = str;
        this.desc = desc;
    }

    @Override public String toString() {
        return str;
    }

    public String getDesc() {
        return desc;
    }

    public static String[] getNames() {
        return Arrays.toString(values()).replaceAll("^.|.$", "").split(", ");
    }
}
