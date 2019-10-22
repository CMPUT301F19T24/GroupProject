import android.location.Location;
import android.media.Image;

import java.util.Date;

enum SocialSituation
{
    NONE,
    ALONE,
    WITH_SOMEONE,
    WITH_SEVERAL,
    CROWD
}
public class MoodEvent {
    private Mood mood;
    private Date timeStamp;
    private SocialSituation socialSituation;
    private String reasonText;
    private Image reasonImage;
    private Location location;
    private User owner;

    MoodEvent(Mood currentMood,
              Date timeStamp,
              SocialSituation socialSituation,
              String reasonText,
              Image reasonImage,
              Location location,
              Integer moodId)
    {
        this.mood = currentMood;
        this.timeStamp = timeStamp;
        this.socialSituation = socialSituation; // Optional
        this.reasonText = reasonText; // Optional
        this.reasonImage = reasonImage; // Optional
        this.location = location; // Optional
    }

    public Mood getMood()
    {
        return this.mood;
    }

    public Date getTimeStamp()
    {
        return this.timeStamp;
    }

    public SocialSituation getSocialSituation()
    {
        return this.socialSituation;
    }

    public String getReasonText()
    {
        return this.reasonText;
    }

    public Image getReasonImage()
    {
        return this.reasonImage;
    }

    public Location getLocation()
    {
        return this.location;
    }

    public User getOwner()
    {
        return this.owner;
    }

    public void setMood(Mood mood)
    {
        this.mood = mood;
    }

    public void setTimeStamp(Date timeStamp)
    {
        this.timeStamp = timeStamp;
    }

    public void setSocialSituation(SocialSituation socialSituation)
    {
        this.socialSituation = socialSituation;
    }

    public void setReasonText(String reasonText)
    {
        this.reasonText = reasonText;
    }

    public void setReasonImage(Image reasonImage)
    {
        this.reasonImage = reasonImage;
    }

    public void setLocation(Location location)
    {
        this.location = location;
    }

    public void setOwner(User owner)
    {
        this.owner = owner;
    }
}
