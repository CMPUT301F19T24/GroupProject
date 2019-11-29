package com.example.groupproject.data.firestorehandler;

import android.app.Dialog;
import android.content.Intent;
import android.media.Image;
import android.util.EventLog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.groupproject.data.moods.Angry;
import com.example.groupproject.data.moods.Anxious;
import com.example.groupproject.data.moods.Disgusted;
import com.example.groupproject.data.moods.Happy;
import com.example.groupproject.data.moods.Mood;
import com.example.groupproject.data.moods.Sad;
import com.example.groupproject.data.relations.SocialSituation;
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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Document;

import java.lang.reflect.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
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
    protected HashMap<String, ListenerRegistration> userMoodEventsUpdateListeners;
    protected HashMap<String, ListenerRegistration> relationshipsUpdateListeners;
    protected ListenerRegistration usersListListener;
    protected FirebaseAuth.AuthStateListener authStateListener;
    protected Boolean cachesInitialized;


    public interface CustomFirebaseDocumentListener {
        void onSuccess(DocumentReference documentReference);
        void onFailure(Exception e);
    }

    static MoodEvent createBlankMoodEvent(){
        return new MoodEvent(new Happy(),new GregorianCalendar(), new User(""), SocialSituation.NONE, "", null, null);
    }

    public FireStoreHandler()
    {
        cachedMoodEvents = new ArrayList<>();
        cachedUsers = new ArrayList<>();
        cachedRelationship = new ArrayList<>();
        fst = new FirestoreTester();
        userMoodEventsUpdateListeners = new HashMap<>();
        cachesInitialized = false;
        updateAllCachedLists();

        // Set up on auth listener
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // Determine when a user logs in.
                FirebaseUser user = fbAuth.getCurrentUser();
                Log.d(TAG, "Listener is registered and fired now");
                if (user != null){// New user has logged in. Verify database existence.
                    final String currentUserName = truncateEmailFromUsername(user.getEmail());
                    Log.d(TAG,"Listener detected login of user: " + currentUserName);
                    DocumentReference userRef =  fbFireStore.collection("users").document(currentUserName);
                    userRef.get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Log.d(TAG, "Verified successful existence on database" + documentSnapshot);
                                    if (documentSnapshot.getData() == null){
                                        HashMap<String, Object> newUserData = new HashMap<>();
                                        newUserData.put("t", new Timestamp(new Date()));
                                        fbFireStore.collection("users").document(currentUserName)
                                                .set(newUserData);
                                    }
                                }
                            });
                }
            }
        };


        fbAuth.addAuthStateListener(authStateListener);
    }
    // Communicates with Remote
    protected ListenerRegistration registerUsersListUpdateListener(){
        // Listen to updates on the users collection
        try {
            Query query = fbFireStore.collection("users");
            ListenerRegistration registration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (e != null){Log.w(TAG, "Error while listening to users collection :error", e);}
                    for (DocumentChange documentChange: queryDocumentSnapshots.getDocumentChanges()){
                        if (documentChange.getType() == DocumentChange.Type.ADDED){
                            // New user was added -- Directly append to local cache
                            cachedUsers.add(new User(documentChange.getDocument().getId()));
                        } else if (documentChange.getType() == DocumentChange.Type.MODIFIED){
                            String userName = documentChange.getDocument().getId();
                            User userInCache = findUserInCacheWithUserName(userName);
                            cachedUsers.remove(userInCache);
                            cachedUsers.add(new User(userName));
                        } else if (documentChange.getType() == DocumentChange.Type.REMOVED){
                            String userName = documentChange.getDocument().getId();
                            User userInCache = findUserInCacheWithUserName(userName); cachedUsers.remove(userInCache);
                        }
                    }
                }
            });

            return registration;
        } catch (Exception e){
            Log.w(TAG, "Failed to register listener for users collection");
        }
        return null;
    }

    protected User findUserInCacheWithUserName(String userName){
        for (User currentUser: cachedUsers){
            if (currentUser.getUserName().compareTo(userName) == 0){
                return currentUser;
            }
        }
        return null;
    }

    protected void registerMoodEventsUpdateListenerForUser(String userName, Query query){
        // Set a SnapshotListener for this query so all mood events in cache are updated if this event happens again.
        ListenerRegistration registration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                try {
                    if (e != null){Log.w(TAG, "Error while listening : ", e);}

                    for (DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()){
                        if (documentChange.getType() == DocumentChange.Type.ADDED){
                            MoodEvent newMoodEvent = createBlankMoodEvent();// A new document was added = this user created a new mood. Add mood to cache
                            updateMoodEventFromDocument(newMoodEvent, documentChange.getDocument());
                            cachedMoodEvents.add(newMoodEvent);
                            Log.d(TAG, "live Listener: new mood" + documentChange.getDocument().getData());
                        } else if (documentChange.getType() == DocumentChange.Type.MODIFIED){ // Update user's mood event if in cache. otherwise create new one and add into cache.
                            // A document from this user was modified. Update the mood event.
                            MoodEvent moodEventInCache = findMoodEventInCacheWithDocumentId(documentChange.getDocument().getId());
                            if (moodEventInCache != null){
                                updateMoodEventFromDocument(moodEventInCache, documentChange.getDocument());
                            } else {
                                // doesn't exist exist in cache. create new mood event, update it on document and add to cache.
                                MoodEvent newMoodEvent = createBlankMoodEvent();
                                updateMoodEventFromDocument(newMoodEvent, documentChange.getDocument());
                                cachedMoodEvents.add(newMoodEvent);
                            }
                        } else if (documentChange.getType() == DocumentChange.Type.REMOVED){
                            // User deleted a mood of their's. No need to keep it in cache.
                            MoodEvent moodEventInCache = findMoodEventInCacheWithDocumentId(documentChange.getDocument().getId());
                            if (moodEventInCache != null){ // If it exists in local cache, delete it!
                                cachedMoodEvents.remove(moodEventInCache);
                            }
                        }
                    }
                } catch (Exception ex){
                    Log.w(TAG, "listen:error", e);
                }
            }
        });
        userMoodEventsUpdateListeners.put(userName, registration);
    }

    protected  Query pullMoodEventsForUserIntoCache(String userName){
        /**
         * Fetch all mood events form a single user
         * Set cache to auto update // Implemented.. Remove on relationship severed.
         */
        CollectionReference moodEventsRef = fbFireStore.collection("moodEvents");
        Query query = moodEventsRef.whereEqualTo("owner", userName);

        // Immediately get results of query and populate mood events into cache.
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (QueryDocumentSnapshot document: task.getResult()){
                                MoodEvent newMoodEvent = createBlankMoodEvent();
                                updateMoodEventFromDocument(createBlankMoodEvent(), document);
                                // Scan cached mood events to see if this already exists.
                                MoodEvent me = null;
                                for (MoodEvent moodEvent: cachedMoodEvents){
                                    if (moodEvent.getDocumentReference() != null){
                                        if (moodEvent.getDocumentReference().getId().compareTo(document.getId()) == 0){
                                            me = moodEvent;
                                            break;
                                        }
                                    }
                                }
                                if(me != null)
                                {
                                    cachedMoodEvents.remove(me); // Remove duplicate mood event form cache
                                }

                                cachedMoodEvents.add(newMoodEvent);
                            }
                        }
                    }
                });
        return query;
    }

    protected MoodEvent findMoodEventInCacheWithDocumentId(String id){
        // Iterate through all cached mood events and return mood event which matches
        for (MoodEvent currentMoodEvent: cachedMoodEvents){
            if (currentMoodEvent.getDocumentReference().getId().compareTo(id) == 0){// This is the mood in question.
                return currentMoodEvent;
            }
        }
        return null;
    }

    protected void pullMoodEventListFromRemote()
    {
        /**
         * Looks at relationships in cachedRelations and gets all mood events accessible to.
         * Determining which users' mood events to pull
         * me, me -> FOLLOWING -> another user, me -> VISIBLE -> another user
         */
        // Clear the current cache
        try {
            cachedMoodEvents = new ArrayList<MoodEvent>();
            String currentUsername = truncateEmailFromUsername(fbAuth.getCurrentUser().getEmail()); // Currently authenticated user.
            HashMap<String, RelationshipStatus> usersToGetMoodEventsFrom = new HashMap<>();
            // Relationship status with self.. is visible
            usersToGetMoodEventsFrom.put(currentUsername, RelationshipStatus.FOLLOWING);
            // Populate cache with mood events from all users related to this user.
            for (Relationship relationship : cachedRelationship) {
                if (relationship.getSender().getUserName().compareTo(currentUsername) == 0){ // User a is involved in this relationship
                    RelationshipStatus relationshipStatus = relationship.getStatus();
                    if (relationshipStatus == RelationshipStatus.FOLLOWING){
                        // Add this user to the list of users whose mood events I can view.
                        // Prevent duplicate queries. Like a dictionary whose key is user
                        if (usersToGetMoodEventsFrom.get(relationship.getRecipiant().getUserName()) != null ){
                            usersToGetMoodEventsFrom.put(relationship.getRecipiant().getUserName(), relationshipStatus);
                        }
                    }
                }
            }
            // Iterate through hashmap to begin querying firebase for mood events form specified users.
            Iterator hashMapIterator = usersToGetMoodEventsFrom.entrySet().iterator();
            // Iterate through hashmap
            while (hashMapIterator.hasNext()){
                Map.Entry mapElement = (Map.Entry) hashMapIterator.next();
                String userName = (String)mapElement.getKey();
                RelationshipStatus relationshipToUser = (RelationshipStatus)mapElement.getValue();

                Query query = pullMoodEventsForUserIntoCache(userName);
                registerMoodEventsUpdateListenerForUser(userName, query); // Start listening for mood event updates by this user.
            }
        } catch (Exception e){
            Log.d(TAG, "pulling mood events list form Remote: failed" + e);
        }
    }

    protected void pullUserListFromRemote()
    {
        /**
         * Populate the local cache with values from remote
         * Initial population
         */
        // TODO
        try{
            fbFireStore.collection("users").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                for (QueryDocumentSnapshot document: task.getResult()){
                                    if (document.getId() != null){
                                        cachedUsers.add(new User(document.getId()));
                                    }
                                }
                            }
                        }
                    });
            // Start listening for further updates to user collection
            usersListListener = registerUsersListUpdateListener();
        } catch (Exception e){
            Log.w(TAG,"Fatal error: failed to pull user list from remote" + e);
        }
    }

    private Relationship convertDocumentToRelationship(QueryDocumentSnapshot document){
        try {
            Map<String, Object> data = document.getData(); // FireStore data is in key,value format.
            String currentUserName = truncateEmailFromUsername(fbAuth.getCurrentUser().getEmail()); // Currently authenticated user.
            // Get values, otherwise set defaults
            String statusString = (data.get("status") == null) ? "INVISIBLE" : data.get("status").toString();
            String user_a_String = (data.get("a") == null) ? "Unknown_user" : data.get("a").toString();
            String user_b_String = (data.get("b") == null) ? "Unknown_user" : data.get("b").toString();
            Log.d(TAG, "STATUS IS: " + statusString);
            if (statusString.compareTo("request") == 0) {
                if (user_a_String.compareTo(currentUserName) == 0) { // Current user sent the request
                    statusString = "PENDING_VISIBLE";
                } else if (user_b_String.compareTo(currentUserName) == 0) { // Current user is the one receiving request
                    statusString = "PENDING_FOLLOWING";
                } else {
                    Log.w(TAG, "fatal error: unknown relationship status" + data);
                }
            }
            RelationshipStatus relationshipStatus = RelationshipStatus.valueOf(statusString.toString());
            Relationship newRelationship = new Relationship(new User(user_a_String), new User(user_b_String), relationshipStatus);
            newRelationship.setDocument(document);
            return newRelationship;
        } catch (Exception e) { Log.w(TAG, "failed to convert document into relationship" + e);}
        return null;

    }

    private void parseRelationshipDocumentIntoCache(QueryDocumentSnapshot document){
        /**
         * Convert a FireStore document into relationship object and populates the local cache
         * Does duplicate protection
         * @param document - Successfully queried data object from FireStore
         */
        try {
            Relationship newRelationship = convertDocumentToRelationship(document);
            // Duplicate protect
            for (Relationship relationship: cachedRelationship){
                if (relationship.getDocumentId().compareTo(newRelationship.getDocumentId()) == 0){
                    // Remove from cache
                    cachedRelationship.remove(relationship);
                    break;
                }
            }
            cachedRelationship.add(newRelationship); // No duplicates in cache are guaranteed.
            Log.d(TAG, "relation document parse: successfully parsed" + document.getData());
        } catch(Exception e) {
            Log.d(TAG, "relationship document parse: failed", e);
        }
    }

    private void onCompleteRelationshipDocumentPull(@NonNull Task<QuerySnapshot> task){
        if (task.isSuccessful()){
            for (QueryDocumentSnapshot document: task.getResult()){
                parseRelationshipDocumentIntoCache(document);
                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
            }
        } else {
            Log.d(TAG, "DocumentSnapshot data: failed to fetch");
        }
    }

    private void removeAllCachedMoodEventsFromUser(String userName){
        for (MoodEvent moodEvent: cachedMoodEvents){
            if (moodEvent.getOwner().getUserName().compareTo(userName) == 0){
                cachedMoodEvents.remove(moodEvent);
            }
        }
    }

    private void startTrackingMoodEventsForUser(String userName){
        /**
         * If a user's moodevents are not tracked, load into local cache and setup listeners.
         */
        // If already tracked.
        ListenerRegistration listeningToUsersMoodEvents = userMoodEventsUpdateListeners.get(userName);
        if (listeningToUsersMoodEvents == null){ // Not listening for this user's mood events
            Query query = pullMoodEventsForUserIntoCache(userName); // Does duplicate protection
            registerMoodEventsUpdateListenerForUser(userName, query);
        }

    }

    private void stopTrackingMoodEventsForUser(String userName){
        removeAllCachedMoodEventsFromUser(userName);
        ListenerRegistration listeningToUsersMoodEvents = userMoodEventsUpdateListeners.get(userName);
        if (listeningToUsersMoodEvents != null){ // Un-register mood event listener.
            listeningToUsersMoodEvents.remove();
        }
    }

    private void updateMoodEventsListenersFromDocument(QueryDocumentSnapshot documentSnapshot){
        /**
         * Determines if this updated relationship needs to be tracked for mood events from this user or not.
         * If a relationship is updated, we don't want to see mood events from that user.
         */
        try {
            Relationship newRelationship = convertDocumentToRelationship(documentSnapshot);
            String currentUserName = truncateEmailFromUsername(fbAuth.getCurrentUser().getEmail());

            if (newRelationship.getSender().getUserName().compareTo(currentUserName) == 0 || newRelationship.getRecipiant().getUserName().compareTo(currentUserName) == 0){
                // Do we need to track new mood events from this relationship?'
                if (newRelationship.getSender().getUserName().compareTo(currentUserName) == 0){ // I am the sender..
                    if (newRelationship.getStatus() == RelationshipStatus.FOLLOWING){
                        // me -> VISIBLE -> another, me -> FOLLOWING -> another. Load mood events from another user.
                        String anotherUser = newRelationship.getRecipiant().getUserName();
                        startTrackingMoodEventsForUser(anotherUser);
                    }
                } else if (newRelationship.getRecipiant().getUserName().compareTo(currentUserName) == 0){// I am the recipient
                    // Somebody else said I can't see their mood events any more
                    if (newRelationship.getStatus() != RelationshipStatus.FOLLOWING){
                        String anotherUser = newRelationship.getSender().getUserName();
                        stopTrackingMoodEventsForUser(anotherUser);
                    }
                }
            }

        } catch (Exception e){Log.w(TAG,"update mood events update listeners: Failed " + e);}

    }
    protected  void registerRelationshipsUpdateListener(String key, Query query){
        /**
         * Listen to updates on any relationship changes by this user
         */
        try{
            if (relationshipsUpdateListeners.get(key) != null){
                relationshipsUpdateListeners.remove(key);
            }
            ListenerRegistration registration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    try{
                        if (e != null){Log.w(TAG, "Error while listening to relationships : ", e);}

                        for (DocumentChange documentChange: queryDocumentSnapshots.getDocumentChanges()){
                            if (documentChange.getType() == DocumentChange.Type.ADDED){
                                // New relationship with user was added
                                parseRelationshipDocumentIntoCache(documentChange.getDocument());
                                updateMoodEventsListenersFromDocument(documentChange.getDocument());
                            } else if(documentChange.getType() == DocumentChange.Type.MODIFIED){
                                // Find relationship in relationsCache and update it.
                                for (Relationship relationship: cachedRelationship){
                                    if (relationship.getDocument() != null){
                                        if (relationship.getDocument().getId().compareTo(documentChange.getDocument().getId()) == 0){
                                            Relationship newRelationship = convertDocumentToRelationship(documentChange.getDocument());
                                            relationship.setSender(newRelationship.getSender());
                                            relationship.setRecipiant(newRelationship.getRecipiant());
                                            relationship.setStatus(newRelationship.getStatus());
                                        }
                                    }
                                }
                                updateMoodEventsListenersFromDocument(documentChange.getDocument());
                            } else if (documentChange.getType() == DocumentChange.Type.REMOVED){
                                // Find relationship in relationsCache and remove it.
                                for (Relationship relationship: cachedRelationship){
                                    if (relationship.getDocument() != null){
                                        if (relationship.getDocument().getId().compareTo(documentChange.getDocument().getId()) == 0){
                                            cachedRelationship.remove(relationship);
                                        }
                                    }
                                }
                                updateMoodEventsListenersFromDocument(documentChange.getDocument());
                            }
                        }
                    } catch(Exception er){Log.w(TAG,"Registration update processing error" + er);}
                }
            });
            relationshipsUpdateListeners.put(key, registration);

        } catch (Exception e){
            Log.d(TAG, "Failed to register relationships listener "+key);
        }

    }

    public void editRelationship(Relationship relationship) {
        // TODO
    }

    protected void pullRelationshipsFromRemotes()
    {
        /**
         * Populate the local cache with relationships of current user
         */
        try {
            CollectionReference relationsRef = fbFireStore.collection("relationships");
            String currentUserName = truncateEmailFromUsername(fbAuth.getCurrentUser().getEmail());
            Log.d(TAG, "DocumentSnapshot: attempting pull for " + currentUserName);
            // Clear current relationship cache because db is queried TODO
            Query currentUserIsSender = relationsRef.whereEqualTo("a", currentUserName);
            Query currentUserIsRecipiant = relationsRef.whereEqualTo("b", currentUserName);
            // Get all users where a is current user.
            currentUserIsSender.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            onCompleteRelationshipDocumentPull(task);
                        }
                    });
            // Get all users where b is current user.
            currentUserIsRecipiant.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            onCompleteRelationshipDocumentPull(task);
                        }
                    });
            // Start listening for future updates on both queries.
            registerRelationshipsUpdateListener("a", currentUserIsSender);
            registerRelationshipsUpdateListener("b", currentUserIsRecipiant);

//        cachedRelationship = (ArrayList<Relationship>) fst.cachedRelationship.clone();
        } catch(Exception e){
            Log.d(TAG, "relationship pull: failed to pull" + e);
        }
    }
    private Map<String, Object> convertMoodEventToHashMap(MoodEvent moodEvent){
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
        moodData.put("timeStamp", moodEvent.getTimeStamp().getTime());
        moodData.put("socialSituation", moodEvent.getSocialSituation().toString());
        return moodData;
    }

    private void updateMoodEventFromDocument(MoodEvent moodEvent, QueryDocumentSnapshot document){
        // Loads a data from firestore document into a mood object
        // Used to create a new mood event or update existing one.
        try{
            Map<String, Object> moodData = document.getData();
            String owner = (moodData.get("owner") == null)? "" : moodData.get("owner").toString();
            Calendar dateTime = new GregorianCalendar();
            dateTime.setTime((moodData.get("timeStamp") == null) ? new Date(): document.getTimestamp("timeStamp").toDate());
            Mood mood;
            String moodString = (moodData.get("mood") == null) ? "Happy": moodData.get("mood").toString(); // By default happy
            if (moodString.compareTo("Happy") == 0){mood = new Happy();}
            else if (moodString.compareTo("Sad") == 0) {mood = new Sad();}
            else if (moodString.compareTo("Disgusted") == 0) { mood = new Disgusted();}
            else if (moodString.compareTo("Anxious") == 0) { mood = new Anxious();}
            else if (moodString.compareTo("Angry") == 0) { mood = new Angry();}
            else {mood = new Happy();}
            LatLng latlng = null;
            if (moodData.get("location") != null){
                GeoPoint savedLocation = (GeoPoint)moodData.get("location");
                double lat = savedLocation.getLatitude();
                double lng = savedLocation.getLongitude();
                latlng = new LatLng(lat, lng);
            }
            String reasonText = null;
            if (moodData.get("reasonText") != null){
                reasonText = moodData.get("reasonText").toString();
            }
            SocialSituation socialSituation = SocialSituation.NONE;
            if (moodData.get("socialSituation") != null){
                String sitString = moodData.get("socialSituation").toString();
                socialSituation = SocialSituation.fromString(sitString);
            }
            // TODO image load
            Image reasonImage = null;

            // Update all data fields in mood event
            moodEvent.setMood(mood);
            moodEvent.setTimeStamp(dateTime);
            moodEvent.setOwner(new User(owner));
            moodEvent.setSocialSituation(socialSituation);
            moodEvent.setReasonText(reasonText);
            moodEvent.setReasonImage(reasonImage);
            moodEvent.setDocumentReference(document.getReference());

        } catch (Exception e){
            Log.d(TAG, "pull Mood Event from firebase: failed to convert hash map to mood event" + e);
        }
    }

    private void pushAttachedImageToRemote(MoodEvent moodEvent){
        // TODO
    }

    // Communicates with Remote
    public void pushNewMoodEventToRemote(final MoodEvent moodEvent){
        /**
         * Push a local mood event to remote
         */
        Map<String, Object> moodData = convertMoodEventToHashMap(moodEvent);
        // Image upload not implemented.
        // When uploading - attach metadata of document reference to image.

        Task<DocumentReference> task = fbFireStore.collection("moodEvents")
                .add(moodData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        moodEvent.onSuccess(documentReference);
                        // Add this to cache

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

    private void removeListenersFromHashMap(HashMap<String, ListenerRegistration> map){
        Iterator hashMapIterator = map.entrySet().iterator();
        // Iterate through hashmap and stop listening for updates
        while (hashMapIterator.hasNext()) {
            Map.Entry mapElement = (Map.Entry) hashMapIterator.next();
            ListenerRegistration listener = (ListenerRegistration) mapElement.getValue();
            listener.remove();
        }
    }

    private void clearAllCachedLists(){
        cachedMoodEvents.clear();
        cachedUsers.clear();
        cachedRelationship.clear();
        try {// Remove listeners that update cachedMoodEvents
            removeListenersFromHashMap(userMoodEventsUpdateListeners);
        } catch (Exception e) {Log.w(TAG, "Could not de-register update listeners from cachedMoodEvents", e);}

        try {// Remove listener that updates cachedUsers
            if (usersListListener != null){
                usersListListener.remove();
            }
        } catch (Exception e) {Log.w(TAG, "Could not de-register update listeners from cachedUsers", e);}

        try {// Remove listeners that update cachedMoodEvents
            removeListenersFromHashMap(relationshipsUpdateListeners);
        } catch (Exception e) {Log.w(TAG, "Could not de-register update listeners from cachedRelationships", e);}

        userMoodEventsUpdateListeners.clear();
    }

    public void initializeAllCachedLists(){
        /**
         * Pull fresh data from remote into all caches.
         */
        clearAllCachedLists();

        pullUserListFromRemote();
        pullRelationshipsFromRemotes();
        pullMoodEventListFromRemote();
    }

    private void updateAllCachedLists()
    {
        /**
         * If any of the caches are empty, pull new data from remote.
         * Just clear, don't push/pull from remote
         */
        if (cachedMoodEvents.size() == 0 || cachedMoodEvents.size() == 0){ // TODO add cachedUsers.size() == 0 ||
            initializeAllCachedLists();
        }
    }

    public ArrayList<MoodEvent> getMoodEventsByUsername(String username)
    {
        /**
         * Get a list of moodevents owned by [username]
         *
         * @param username - Exact string representing a unique User object
         * @return - Arraylist of MoodEvents fitting the criteria
         */
//        updateAllCachedLists();
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

    public ArrayList<Relationship> getRelationShipOfSender(String username){
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
        pullRelationshipsFromRemotes();

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

    public ArrayList<Relationship> getRelationShipOfReceiver(String username){
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

    public ArrayList<Relationship> getPendingResponsesOfUser(String username){
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

    public void setRelationship(String sender, String receiver, RelationshipStatus rs){
        for(Relationship i : cachedRelationship)
        {
            if(i.getSender().getUserName().compareTo(sender) == 0 && i.getRecipiant().getUserName().compareTo(receiver) == 0)
            {
                i.setStatus(rs);
            }
        }
        pushRelationshipsToRemotes();
    }

    public User getUserObjWIthUsername(String username){
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

    public void addMoodEvent(MoodEvent me){
        /**
         * Add mood event to cache
         *
         * @param me - MoodEvent object to be added
         *
         */
        cachedMoodEvents.add(me);
    }

    private String getMoodEventUpdateId(MoodEvent moodEvent, String userName){
        /**
         * Determines if a mood Event can be updated
         * Only owner's of moods can update it if it was pulled from the database.
         */
        if (moodEvent.getOwner().getUserName().compareTo(userName) == 0){ // verify ownership and permission to modify.
            if (moodEvent.getDocumentReference() != null){// Verify this moodEvent was pulled from the database. DocumentReference will exist
                DocumentReference documentReference = moodEvent.getDocumentReference();
                if (documentReference.getId() != null){
                    return documentReference.getId();
                }
            }
        }
        return null;
    }
    public void editMoodEvent(MoodEvent moodEvent){
        /**
         * Update the MoodEvent that has the same key on the remote
         * @param moodEvent - Updated mood event
         *
         */
        try {
            String currentUserName = truncateEmailFromUsername(fbAuth.getCurrentUser().getEmail());
            String documentUpdateId = getMoodEventUpdateId(moodEvent, currentUserName);

            if (documentUpdateId != null) {
                fbFireStore.collection("moodEvents").document(documentUpdateId)
                        .update(convertMoodEventToHashMap(moodEvent));
            }
        } catch (Exception e){
            Log.w(TAG, "Mood event update: failed" , e);
        }
    }
    public void deleteMoodEvent(MoodEvent moodEvent){
        /**
         * Delete the Moodevent that has the same key from the remote
         * Verify user can make changes and mood event can be updated on database.
         * @param moodEvent - MoodEvent object to be deleted
         */
        try{
            String currentUserName = truncateEmailFromUsername(fbAuth.getCurrentUser().getEmail());
            String documentUpdateId = getMoodEventUpdateId(moodEvent, currentUserName);
            if (documentUpdateId != null){
                fbFireStore.collection("moodEvents").document(documentUpdateId)
                        .delete(); // Registered Listener will catch deletion and remove this mood from local cache.
            }
        } catch (Exception e){
            Log.d(TAG, "delete mood event: failed " + e);
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
        // TODO clear caches on logout.
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
        Log.d(TAG, "current loogged in user is: " + fetchedUser);
        fetchedUser = fetchedUser.substring(0, fetchedUser.length()-21);
        return  (fetchedUser);
    }
}
