import android.graphics.Color;
import android.media.Image;

public abstract class Mood {
    protected String name;
    protected Image image;
    protected Color color;

    public String getName()
    {
        return this.name;
    }
    public Image getImage()
    {
        return this.image;
    }
    public Color getColor()
    {
        return this.color;
    }
}
