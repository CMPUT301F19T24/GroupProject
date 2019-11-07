package com.example.groupproject;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

class FSHConstructor
{
    // static variable single_instance of type Singleton
    private static FSHConstructor single_instance = null;

    // variable of type String
    public FireStoreHandler fsh;

    // private constructor restricted to this class itself
    private FSHConstructor()
    {
    fsh = new FireStoreHandler();
    }

    // static method to create instance of Singleton class
    public static FSHConstructor getInstance()
    {
        if (single_instance == null)
            single_instance = new FSHConstructor();

        return single_instance;
    }
}

class FireStoreHandler {
    // Testing
    private FirestoreTester fst;
    private FirebaseAuth fbAuth = FirebaseAuth.getInstance();

    protected ArrayList<MoodEvent> cachedMoodEvents;
    protected ArrayList<User> cachedUsers;
    protected ArrayList<Relationship> cachedRelationship;
    public FireStoreHandler()
    {
        cachedMoodEvents = new ArrayList<>();
        cachedUsers = new ArrayList<>();
        cachedRelationship = new ArrayList<>();
        fst = new FirestoreTester();
        updateAllCachedLists();
    }
    // Communicates with Remote
    protected void pullMoodEventListFromRemote()
    {
        /**
         * Populate the local cache with values from remote
         */
        // TODO
        cachedMoodEvents = (ArrayList<MoodEvent>) fst.cachedMoodEvents.clone();
    }

    protected void pullUserListFromRemote()
    {
        /**
         * Populate the local cache with values from remote
         */
        // TODO
        cachedUsers = (ArrayList<User>) fst.cachedUsers.clone();
    }

    protected void pullRelationshipsFromRemotes()
    {
        /**
         * Populate the local cache with values from remote
         */
        // TODO
        cachedRelationship = (ArrayList<Relationship>) fst.cachedRelationship.clone();

    }

    // Communicates with Remote
    private void pushMoodEventListToRemote()
    {
        /**
         * Pushes local cached values to remote
         */
        // TODO
        fst.cachedMoodEvents = (ArrayList<MoodEvent>) cachedMoodEvents.clone();

    }

    private void pushUserListToRemote()
    {
        /**
         * Pushes local cached values to remote
         */
        // TODO
        fst.cachedUsers = (ArrayList<User>) cachedUsers.clone();

    }

    private void pushRelationshipsToRemotes()
    {
        /**
         * Pushes local cached values to remote
         */
        // TODO
        fst.cachedRelationship = (ArrayList<Relationship>) cachedRelationship.clone();
    }

    private void updateAllCachedLists()
    {
        /**
         * Clear, and update everything from remote
         */
        cachedMoodEvents.clear();
        cachedUsers.clear();
        cachedRelationship.clear();

        pullMoodEventListFromRemote();
        pullUserListFromRemote();
        pullRelationshipsFromRemotes();
    }

    public ArrayList<MoodEvent> getMoodEventsByUsername(String username)
    {
        /**
         * Get a list of moodevents owned by [username]
         *
         * @param username - Exact string representing a unique User object
         * @return - Arraylist of MoodEvents fitting the criteria
         */
        updateAllCachedLists();
        ArrayList<MoodEvent> arr = new ArrayList<>();
        for(MoodEvent i : cachedMoodEvents)
        {
            if(i.getOwner().getUserName() == username)
            {
                arr.add(i);
            }
        }
        return arr;
    }

    public ArrayList<MoodEvent> getMoodEventsByMoodName(String moodName)
    {
        /**
         * Get a list of moodevents sharing a Mood
         *
         * @param moodName - Exact string representing a unique Mood object
         * @return - Arraylist of MoodEvents fitting the criteria
         */
        updateAllCachedLists();
        ArrayList<MoodEvent> arr = new ArrayList<>();
        for(MoodEvent i : cachedMoodEvents)
        {
            if(i.getMood().getName() == moodName)
            {
                arr.add(i);
            }
        }
        return arr;
    }

    public ArrayList<MoodEvent> getMoodEventsByMoodNameAndUserName(String moodName, String username)
    {
        /**
         * Get a list of moodevents owned by [username]
         *
         * @param moodName - Exact string representing a unique Mood object
         * @param username - Exact string representing a unique User object
         * @return - Arraylist of MoodEvents fitting the criteria
         */
        updateAllCachedLists();
        ArrayList<MoodEvent> arr = new ArrayList<>();
        for(MoodEvent i : cachedMoodEvents)
        {
            if(i.getMood().getName() == moodName && i.getOwner().getUserName() == username)
            {
                arr.add(i);
            }
        }
        return arr;
    }

    public ArrayList<MoodEvent> getVisibleMoodEvents(String username)
    {
        /**
         * Get a list of moodevents visible to [username]
         * See RelationshipStatus for valid values
         * See Relationship for what is defined as visible
         *
         * @param username - Exact string representing a unique User object
         * @return - Arraylist of MoodEvents fitting the criteria
         */
        updateAllCachedLists();
        ArrayList<MoodEvent> arr_me = new ArrayList<>();
        ArrayList<Relationship> arr_rs = getRelationShipOfSender(username);

        ArrayList<String> visibleUsers = new ArrayList<>();
        visibleUsers.add(username);

        for (Relationship i : arr_rs)
        {
            if(i.isVisible())
            {
                visibleUsers.add(i.getRecipiant().getUserName());
            }
        }

        for(MoodEvent j : cachedMoodEvents)
        {
            if(visibleUsers.contains(j.getOwner().getUserName() ))
            {
                arr_me.add(j);
            }
        }
        return arr_me;
    }

    public ArrayList<Relationship> getRelationShipOfSender(String username)
    {
        /**
         * Get a list of Relationship objects that [username] is the sender of.
         *
         * @param username - Exact string representing a unique User object
         * @return - Arraylist of Relationships fitting the criteria
         */
        ArrayList<Relationship> arr_rs = new ArrayList<>();
        for(Relationship i : cachedRelationship)
        {
            if(i.getSender().getUserName() == username)
            {
                arr_rs.add(i);
            }
        }
        return arr_rs;
    }

    public User getUserObjWIthUsername(String username)
    {
        /**
         * Get the user object that has an exact unique username
         *
         * @param username - Exact string representing a unique username
         * @return - User object fitting the criteria
         */

        User rc = null;
        updateAllCachedLists();

        for(User i : cachedUsers)
        {
            if(i.getUserName() == username)
            {
                rc = i;
                break;
            }
        }
        return rc;
    }

    public void editMoodEvent(MoodEvent me)
    {
        /**
         * Update the Moodevent that has the same key on the remote
         * The key is the combination of username, and timestamp associated with the moodevent
         *
         * @param me - MoodEvent object to be deleted
         *
         */
        for(MoodEvent i : cachedMoodEvents)
        {
            if(i.getTimeStamp().compareTo(me.getTimeStamp()) == 0 && i.getOwner().getUserName() == me.getOwner().getUserName())
            {
                cachedMoodEvents.remove(cachedMoodEvents.indexOf(i));
                cachedMoodEvents.add(me);

                pushMoodEventListToRemote();
                break;
            }
        }
    }
    public void deleteMoodEvent(MoodEvent me)
    {
        /**
         * Delete the Moodevent that has the same key from the remote
         * The key is the combination of username, and timestamp associated with the moodevent
         *
         * @param me - MoodEvent object to be deleted
         *
         */
        for(MoodEvent i : cachedMoodEvents)
        {
            if(i.getTimeStamp().compareTo(me.getTimeStamp()) == 0 && i.getOwner().getUserName() == me.getOwner().getUserName())
            {
                cachedMoodEvents.remove(cachedMoodEvents.indexOf(i));

                pushMoodEventListToRemote();
                break;
            }
        }
    }

    public void login(String username, final String password, final View view) {
        username = username + "@cmput301-c6741.web.app";
        Exception exception = null;
        fbAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override

                    public void onComplete(@NonNull Task<AuthResult> task) {
                        EditText passwordText = view.getRootView().findViewById(R.id.password_field);
                        EditText editText = view.getRootView().findViewById(R.id.username_field);
                        if (task.isSuccessful()) {
                            Log.d(TAG, "loginUserWithEmail:successful");
                            Intent intent = new Intent(view.getRootView().getContext(), MainActivity.class);
                            view.getRootView().getContext().startActivity(intent);
                        } else {
                            Log.w(TAG, "loginUserWithEmail:failed");
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e) {
                                editText.setError("User does not exist");
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                passwordText.setError("Incorrect Password");
                            } catch (Exception e) {
                                Toast.makeText(view.getContext(), "An error occured while logging in", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

}
