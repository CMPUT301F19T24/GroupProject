package com.example.groupproject.data.firestorehandler;

import android.app.Dialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.groupproject.ui.login.Login;
import com.example.groupproject.MainActivity;
import com.example.groupproject.R;
import com.example.groupproject.data.moodevents.MoodEvent;
import com.example.groupproject.data.relations.Relationship;
import com.example.groupproject.data.relations.RelationshipStatus;
import com.example.groupproject.data.user.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static android.content.ContentValues.TAG;

public class FireStoreHandler {
    // Testing
    private FirestoreTester fst;
    private FirebaseAuth fbAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore fbFireStore = FirebaseFirestore.getInstance();

    protected ArrayList<MoodEvent> cachedMoodEvents;
    protected ArrayList<User> cachedUsers;
    protected ArrayList<Relationship> cachedRelationship;

    public interface CustomFirebaseDocumentListener {
        void onSuccess(DocumentReference documentReference);
        void onFailure(Exception e);
    }

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

    private void parseRelationshipDocumentIntoCache(QueryDocumentSnapshot document){
        /**
         * Convert a FireStore document into relationship object and populates the local cache
         * @param document - Successfully queried data object from FireStore
         */
        try {
            Map<String, Object> data = document.getData(); // FireStore data is in key,value format.
            String currentUserName = truncateEmailFromUsername(fbAuth.getCurrentUser().getEmail()); // Currently authenticated user.
            // Get values, otherwise set defaults
            String statusString = (data.get("status") == null) ? "INVISIBLE" : data.get("status").toString();
            String user_a_String = (data.get("a") == null) ? "Unknown_user" : data.get("a").toString();
            String user_b_String = (data.get("b") == null) ? "Unknown_user" : data.get("b").toString();
            switch (statusString){
                case "request":
                    if (user_a_String == currentUserName){ // Current user sent the request
                        statusString = "PENDING_VISIBLE";
                    } else if (user_b_String == currentUserName) { // Current user is the one receiving request
                        statusString = "PENDING_FOLLOWING";
                    }
            }
            RelationshipStatus relationshipStatus = RelationshipStatus.valueOf(statusString);
            Relationship newRelationship = new Relationship(new User(user_a_String), new User(user_b_String), relationshipStatus);
            newRelationship.setDocumentId(document.getId());
            cachedRelationship.add(newRelationship);
            Log.d(TAG, "relation document parse: successfully parsed" + document.getData());
        } catch(Exception e) {
            Log.d(TAG, "relationship document parse: failed", e);
        }
    }

    protected void pullRelationshipsFromRemotes()
    {
        /**
         * Populate the local cache with relationships of current user
         */
        CollectionReference relationsRef = fbFireStore.collection("relationships");
        String currentUserName = truncateEmailFromUsername(fbAuth.getCurrentUser().getEmail());
        Log.d(TAG, "DocumentSnapshot: attempting pull for " + currentUserName);
        // Clear current relationship cache because db is queried TODO
//        cachedRelationship = new ArrayList<Relationship>();
        // Get all users who are following this user.
        relationsRef.whereEqualTo("a", currentUserName).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                parseRelationshipDocumentIntoCache(document);
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "DocumentSnapshot data: failed to fetch");
                        }
                    }
                });

//        cachedRelationship = (ArrayList<Relationship>) fst.cachedRelationship.clone();

    }
    private Map<String, Object> hashMapMoodEvent(MoodEvent moodEvent){
        // Package a mood event
        Map<String, Object> moodData = new HashMap<>();
        moodData.put("mood", moodEvent.getMood().getName());
        moodData.put("owner", moodEvent.getOwner().getUserName());
        if (moodEvent.hasLatLng()){
            GeoPoint geoPoint = new GeoPoint(moodEvent.getLatLng().latitude, moodEvent.getLatLng().longitude);
            moodData.put("location", geoPoint);
        }
        moodData.put("reasonText", moodEvent.getReasonText());
        moodData.put("reasonImage", "");
        moodData.put("timeStamp", new Timestamp(new Date(moodEvent.getTimeStamp())));
        moodData.put("socialSituation", moodEvent.getSocialSituation().toString());
        return moodData;
    }
    private void pushAttachedImageToRemote(MoodEvent moodEvent){
        // TODO
    }

    // Communicates with Remote
    public void pushNewMoodEventToRemote(final MoodEvent moodEvent){
        /**
         * Push a local mood event to remote
         */
        Map<String, Object> moodData = hashMapMoodEvent(moodEvent);
        // Image upload not implemented.
        // When uploading - attach metadata of document reference to image.

        Task<DocumentReference> task = fbFireStore.collection("moodEvents")
                .add(moodData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        moodEvent.onSuccess(documentReference);
                        Log.d(TAG, "Mood Event uploaded to remote");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        moodEvent.onFailure(e);
                        Log.d(TAG, "Failed to upload mood event");
                    }
                });
    }

    private Task<DocumentReference> pushNewUserToRemote(final String userName){
        Map<String, Object> user = new HashMap<>();
        user.put("name", userName);
        Task<DocumentReference> added_user = fbFireStore.collection("users").add(user);
        return added_user;
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
            if(i.getOwner().getUserName().compareTo(username) == 0)
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
            if(i.getMood().getName() == moodName && i.getOwner().getUserName().compareTo(username) == 0)
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
         * Will also populate the remote with relationships between previous unmet users
         *
         * @param username - Exact string representing a unique User object
         * @return - Arraylist of Relationships fitting the criteria
         */
        ArrayList<Relationship> arr_rs = new ArrayList<>();

        Set<String> cachedNames = new HashSet<String>();
        Set<String> foundNames = new HashSet<String>();

        for(User i : cachedUsers)
        {
            if(i.getUserName().compareTo(username) != 0)
            {
                cachedNames.add(i.getUserName());
            }
        }


        for(Relationship j : cachedRelationship)
        {
            if(j.getSender().getUserName().compareTo(username) == 0)
            {
                foundNames.add(j.getRecipiant().getUserName());

                arr_rs.add(j);
            }
        }

        cachedNames.removeAll(foundNames); // Difference between the two

        for(String k : cachedNames)
        {
            Relationship rs = new Relationship(new User(username), new User(k), RelationshipStatus.INVISIBLE);
            arr_rs.add(rs);
            cachedRelationship.add(rs);
        }

        pushRelationshipsToRemotes(); // Add any newly created relationship


        return arr_rs;
    }

    public ArrayList<Relationship> getRelationShipOfReceiver(String username)
    {
        /**
         * Get a list of Relationship objects that [username] is the receiver of.
         *
         * @param username - Exact string representing a unique User object
         * @return - Arraylist of Relationships fitting the criteria
         */
        ArrayList<Relationship> arr_rs = new ArrayList<>();
        for(Relationship i : cachedRelationship)
        {
            if(i.getRecipiant().getUserName().compareTo(username) == 0)
            {
                arr_rs.add(i);
            }
        }
        return arr_rs;
    }

    public ArrayList<Relationship> getPendingResponsesOfUser(String username)
    {
        /**
         * Get a list of Relationship objects that [username] is the receiver of.
         *
         * @param username - Exact string representing a unique User object
         * @return - Arraylist of Relationships fitting the criteria
         */
        ArrayList<Relationship> arr_rs = new ArrayList<>();
        for(Relationship i : cachedRelationship)
        {
            if(i.getRecipiant().getUserName().compareTo(username) == 0 && i.isPending())
            {
                arr_rs.add(i);
            }
        }
        return arr_rs;
    }

    public void setRelationship(String sender, String receiver, RelationshipStatus rs)
    {
        for(Relationship i : cachedRelationship)
        {
            if(i.getSender().getUserName().compareTo(sender) == 0 && i.getRecipiant().getUserName().compareTo(receiver) == 0)
            {
                i.setStatus(rs);
            }
        }
        pushRelationshipsToRemotes();
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
            if(i.getUserName().compareTo(username) == 0)
            {
                rc = i;
                break;
            }
        }
        return rc;
    }

    public ArrayList<User> getCachedUsers() {
        return cachedUsers;}

    public void addMoodEvent(MoodEvent me)
    {
        /**
         * Add mood event to cache
         *
         * @param me - MoodEvent object to be added
         *
         */
        cachedMoodEvents.add(me);
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
            if(i.getTimeStamp().compareTo(me.getTimeStamp()) == 0 && i.getOwner().getUserName().compareTo(me.getOwner().getUserName()) == 0)
            {
                cachedMoodEvents.remove(cachedMoodEvents.indexOf(i));
                cachedMoodEvents.add(me);

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
            if(i.getTimeStamp().compareTo(me.getTimeStamp()) == 0 && i.getOwner().getUserName().compareTo(me.getOwner().getUserName()) == 0)
            {
                cachedMoodEvents.remove(cachedMoodEvents.indexOf(i));

                // del here..
                break;
            }
        }
    }


    public void login(String username, final String password, final View view) {
        /**
         * Function responsible for logging in. Linked to Login class.
         * Checks the username and password, if they are valid and correspond to
         * an existing account, the user is logged in and redirected to the main screen.
         *
         * @author riona
         * @param username String containing the entered username
         * @param password String containing the entered password
         * @param view The view that the program had at the time of this function call.
         */

        // Append "@cmput301-c6741.web.app" to the end to make the username
        // the email format that firebase expects
        username = username + "@cmput301-c6741.web.app";
        fbAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override

                    public void onComplete(@NonNull Task<AuthResult> task) {
                        EditText passwordText = view.getRootView().findViewById(R.id.password_field);
                        EditText editText = view.getRootView().findViewById(R.id.username_field);
                        if (task.isSuccessful()) {
                            // If login was successful print statement to the log and change view
                            Log.d(TAG, "loginUserWithEmail:successful");
                            Intent intent = new Intent(view.getRootView().getContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            view.getRootView().getContext().startActivity(intent);
                        } else {
                            // If login fails print statement to the log, and catch exceptions
                            Log.w(TAG, "loginUserWithEmail:failed");

                            // Catches specific exceptions as well as a generic catch all
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e) {
                                editText.setError("User does not exist");
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                passwordText.setError("Incorrect Password");
                            }catch (Exception e) {
                                Toast.makeText(view.getContext(), "An error occurred while logging in", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }


    public void signOut(View view) {
        /**
         * Function to sign out a user and change the view
         * back to the login screen.
         *
         * @author riona
         * @param view the view at the time of this function call
         */

        fbAuth.signOut();
        Intent intent = new Intent(view.getRootView().getContext(), Login.class);
        view.getRootView().getContext().startActivity(intent);
    }

    public void createNewUser(String username, String password, final View view, final Dialog dialog) {
        /**
         * Function responsible for creating new users.
         * Linked to CreateAccountDialog. Will exit the dialog upon
         * successful account creation, otherwise will state why it
         * couldn't create account.
         *
         * @author riona
         * @param username username entered by the user
         * @param password password entered by the user
         * @param view the view at the time of this function call
         * @param dialog the dialog that was open at when this function was called
         */
        username = username + "@cmput301-c6741.web.app";
        fbAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        EditText passwordText = view.getRootView().findViewById(R.id.new_password);
                        EditText editText = view.getRootView().findViewById(R.id.new_username);
                        if (task.isSuccessful()) {
                            Log.d(TAG, "loginUserWithEmail:successful");
                            dialog.dismiss();
                            Toast.makeText(view.getContext(), "Your account has been created", Toast.LENGTH_SHORT).show();
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthUserCollisionException e) {
                                editText.setError("This username is unavailable");
                                Log.w(TAG, "loginUserWithEmail:usernameUnavailable");
                            } catch (FirebaseAuthWeakPasswordException e) {
                                passwordText.setError("Password must be at least 6 characters");
                                Log.w(TAG, "loginUserWithEmail:weakPassword");
                            } catch (Exception e) {
                                Log.w(TAG, "loginUserWithEmail:failed");
                                Toast.makeText(view.getContext(), "An error occurred while creating account", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }


    public void deleteUser(String username) {
        username = username + "@cmput301-c6741.web.app";
        fbAuth.getCurrentUser().delete();
    }

    public String truncateEmailFromUsername(String email){
        String truncatedName = email;
        if (truncatedName.length() > 23){// We know "@cmput301-c6741.web.app" exists and is 23 char long
            truncatedName = truncatedName.substring(0, truncatedName.length() - 23);
        }
        return truncatedName;

    }
    public String getCurrentUser(){
        String fetchedUser = fbAuth.getCurrentUser().getDisplayName().toString();
        fetchedUser = fetchedUser.substring(0, fetchedUser.length()-21);
        return  (fetchedUser);
    }
}
