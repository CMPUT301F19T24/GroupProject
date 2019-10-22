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
    private Image reasonPhoto;
    private Location location;
    private Integer moodEventId;

    MoodEvent(Mood currentMood,
              Date timeStamp,
              SocialSituation socialSituation,
              String reasonText,
              Image reasonPhoto,
              Location location,
              Integer moodId)
    {
        this.mood = currentMood;
        this.timeStamp = timeStamp;
        this.socialSituation = socialSituation; // Optional
        this.reasonText = reasonText; // Optional
        this.reasonPhoto = reasonPhoto; // Optional
        this.location = location; // Optional
        this.moodEventId = moodId;
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
        return this.reasonPhoto;
    }

    public Location getLocation()
    {
        return this.location;
    }

    public Integer getMoodEventId()
    {
        return this.moodEventId;
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

    public void setReasonPhoto(Image reasonPhoto)
    {
        this.reasonPhoto = reasonPhoto;
    }

    public void setLocation(Location location)
    {
        this.location = location;
    }
}
