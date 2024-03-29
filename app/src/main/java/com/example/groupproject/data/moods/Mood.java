package com.example.groupproject.data.moods;

public abstract class Mood implements Comparable{
    protected String name;
    protected int image; // Use following format: R.drawable.emot_sad, place PNGs in res/drawable
    protected int image_small;
    protected int color; // Use hexadecimal in format :0x<alpha><red><green><blue>

    public String getName()
    {
        return this.name;
    }
    public int getImage()
    {
        return this.image;
    }
    public int getColor()
    {
        return this.color;
    }
    public int getImageSmall() { return this.image_small;}

    @Override
    public int compareTo(Object o) {
        return this.name.compareTo(((Mood) o).getName());
    }

}
