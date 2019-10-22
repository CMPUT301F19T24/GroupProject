import java.util.ArrayList;

public class User {
    private String userName;
    private ArrayList<MoodEvent> ownedMoodEvents;

    public User(String userName)
    {
        this.userName = userName;
        this.ownedMoodEvents = new ArrayList<MoodEvent>();
    }

    public void addMoodEvent(MoodEvent moodEvent)
    {
        this.ownedMoodEvents.add(moodEvent);
    }

    public void removeMoodEvent(/* TBD */)
    {
        /* TBD */
    }

    public String getUserName()
    {
        return this.userName;
    }

    public ArrayList<MoodEvent> getOwnedMoodEvents()
    {
        return this.ownedMoodEvents;
    }
}
