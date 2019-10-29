package com.example.groupproject;

import java.util.Arrays;

public enum SortingMethod
{
    NAME("Name"),
    DATE("Date");

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
