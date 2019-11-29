package com.example.groupproject.data.relations;

import java.util.Arrays;

public enum RelationshipStatus {
    INVISIBLE("Invisible", "You can not see the posts of this user"),                                           //Visible
    PENDING("Pending", "You are waiting on a response from this user"),
    FOLLOWING("Following", "You can see the posts of this user");

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

    public static RelationshipStatus fromString(String text){
        for (RelationshipStatus i : RelationshipStatus.values()){
            if (i.str.equalsIgnoreCase(text)){
                return i;
            }
        }
        return RelationshipStatus.INVISIBLE;
    }
}
