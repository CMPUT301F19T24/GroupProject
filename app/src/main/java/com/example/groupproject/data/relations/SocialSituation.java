package com.example.groupproject.data.relations;

import java.util.Arrays;

public enum SocialSituation
{
    NONE("N/A"),
    ALONE("Alone"),
    WITH_SOMEONE("With Someone"),
    WITH_SEVERAL("With Several Others"),
    CROWD("With a Crowd");

    private String str;

    SocialSituation(String str) {
        this.str = str;
    }

    @Override public String toString() {
        return str;
    }

    public static String[] getNames() {
        return Arrays.toString(values()).replaceAll("^.|.$", "").split(", ");
    }

    public static SocialSituation fromString(String text){
        for (SocialSituation socialSituation : SocialSituation.values()){
            if (socialSituation.str.equalsIgnoreCase(text)){
                return socialSituation;
            }
        }
        return SocialSituation.NONE;
    }
}
