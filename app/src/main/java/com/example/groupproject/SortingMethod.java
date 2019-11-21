package com.example.groupproject;

import java.util.Arrays;

public enum SortingMethod
{
    NAME("Mood Type"),
    DATE_NTOO("Date (New to Old)"),
    DATE_OTON("Date (Old to New)"),
    OWNER("Owner");

    private String str;

    SortingMethod(String str) {
        this.str = str;
    }

    @Override public String toString() {
        return str;
    }

    public static String[] getNames() {
        return Arrays.toString(values()).replaceAll("^.|.$", "").split(", ");
    }
}
