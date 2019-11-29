package com.example.groupproject.data.firestorehandler;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
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
import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
    private FirebaseStorage fbStorage = FirebaseStorage.getInstance();

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
        return new MoodEvent(new Happy(),new GregorianCalendar(), new User("blankUser"), SocialSituation.NONE, "", null, null);
    }

    public FireStoreHandler()
    {
        cachedMoodEvents = new ArrayList<>();
        cachedUsers = new ArrayList<>();
        cachedRelationship = new ArrayList<>();
        userMoodEventsUpdateListeners = new HashMap<>();
        relationshipsUpdateListeners = new HashMap<>();
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
                        Log.d(TAG, "Users collection listener: " + documentChange.getType().toString() + " detected!");
                        if (documentChange.getType() == DocumentChange.Type.ADDED){
                            // New user was added -- Directly append to local cache
                            String userName = documentChange.getDocument().getId();
                            User userInCache = findUserInCacheWithUserName(userName);
                            if (userInCache != null){
                                cachedUsers.remove(userInCache);
                            }
                            cachedUsers.add(new User(documentChange.getDocument().getId()));
                        } else if (documentChange.getType() == DocumentChange.Type.MODIFIED){
                            String userName = documentChange.getDocument().getId();
                            User userInCache = findUserInCacheWithUserName(userName); // Duplicate protected
                            cachedUsers.remove(userInCache);
                            cachedUsers.add(new User(userName));
                        } else if (documentChange.getType() == DocumentChange.Type.REMOVED){
                            String userName = documentChange.getDocument().getId();
                            User userInCache = findUserInCacheWithUserName(userName); // Duplicate protected.
                            cachedUsers.remove(userInCache);
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
                        Log.d(TAG, "MoodEvents collection listener: " + documentChange.getType().toString() + " detected!");
                        if (documentChange.getType() == DocumentChange.Type.ADDED){
                            parseMoodEventDocumentIntoCache(documentChange.getDocument()); // Duplicate protected
                        } else if (documentChange.getType() == DocumentChange.Type.MODIFIED){ // Update user's mood event if in cache. otherwise create new one and add into cache.
                            parseMoodEventDocumentIntoCache(documentChange.getDocument()); // Duplicate protected
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

    protected MoodEvent findMoodEventInCacheWithDocumentId(String id){
        MoodEvent foundMoodEvent = null;
        // Iterate through all cached mood events and return mood event which matches
        for (MoodEvent currentMoodEvent: cachedMoodEvents){
            if (currentMoodEvent.getDocumentReference().getId().compareTo(id) == 0){// This is the mood in question.
                foundMoodEvent = currentMoodEvent;
                break;
            }
        }
        return foundMoodEvent;
    }

    protected void pullMoodEventListFromRemote()
    {
        /**
         * Looks at relationships in cachedRelations and gets all mood events accessible to.
         * Determining which users' mood events to pull
         * me, me -> FOLLOWING -> another user
         */
        // Clear the current cache
        try {

            String currentUsername = truncateEmailFromUsername(fbAuth.getCurrentUser().getEmail()); // Currently authenticated user.
            HashMap<String, RelationshipStatus> usersToGetMoodEventsFrom = new HashMap<>();
            // Relationship status with self.. is visible
            startTrackingMoodEventsForUser(currentUsername);

            // Go through relationships and determine which users' mood events to follow
            for (Relationship relationship: cachedRelationship){
                if (relationship.getSender().getUserName().compareTo(currentUsername) == 0){ // I'm the sender of this relationship
                    RelationshipStatus rs = relationship.getStatus();
                    if (rs == RelationshipStatus.FOLLOWING){ // I'm following this user
                        String anotherUser = relationship.getRecipiant().getUserName();
                        if (anotherUser.compareTo(currentUsername) != 0){ // I'm not the other user
                            startTrackingMoodEventsForUser(anotherUser); // Listen to this user's mood events!
                            Log.d(TAG,"new user's mood tracking, a: " + relationship.getSender().getUserName() + " b: " +
                                    relationship.getRecipiant() + " status: " + relationship.getStatus() + " justifies listening for their mood");
                        }
                    }
                }
            }
        } catch (Exception e){
            Log.d(TAG, "pulling mood events list form Remote: failed", e);
        }
    }

    protected void pullUserListFromRemote()
    {
        /**
         * Populate the local cache with values from remote
         * Initial population
         */
        try{
            // Start listening for further updates to user collection
            usersListListener = registerUsersListUpdateListener();
        } catch (Exception e){
            Log.w(TAG,"Fatal error: failed to pull user list from remote" + e);
        }
    }

    private Relationship convertDocumentToRelationship(QueryDocumentSnapshot document){
        try {
            Map<String, Object> data = document.getData(); // FireStore data is in key,value format.
            // Get values, otherwise set defaults
            String statusString = (data.get("status") == null) ? "Invisible" : data.get("status").toString();
            String user_a_String = (data.get("a") == null) ? "Unknown_user" : data.get("a").toString();
            String user_b_String = (data.get("b") == null) ? "Unknown_user" : data.get("b").toString();
            // Set appropriate relationship status
            RelationshipStatus relationshipStatus = RelationshipStatus.INVISIBLE;
            if (statusString.compareTo("Pending") == 0){
                relationshipStatus = RelationshipStatus.PENDING;
            } else if (statusString.compareTo("Following") == 0){
                relationshipStatus = RelationshipStatus.FOLLOWING;
            }
            Relationship newRelationship = new Relationship(new User(user_a_String), new User(user_b_String), relationshipStatus);
            newRelationship.setDocument(document);
            return newRelationship;
        } catch (Exception e) { Log.w(TAG, "failed to convert document into relationship", e);}
        return null;
    }

    public void downloadReasonImageURLForDocumentId(String imageURL) {
        // Reason image url is document id
        // Downloads image given a url and attempts it to load onto its mood event in cache.
        // Image has metadata which says what documentID of mood it belongs to.
        // Try to find that moodEvent in cache and loadImage
        String reasonImageURL = imageURL;
        if (reasonImageURL != null) {
            try {
                if (!reasonImageURL.isEmpty()) {
                    // Download the image and put it as bitmap
                    Log.d(TAG, "Attempting download from imageURL: " + reasonImageURL);
                    final StorageReference imageReference = fbStorage.getReferenceFromUrl(reasonImageURL);
                    imageReference.getMetadata().addOnCompleteListener(new OnCompleteListener<StorageMetadata>() {
                        @Override
                        public void onComplete(@NonNull Task<StorageMetadata> task) {
                            Log.d(TAG, "metadata task completed");
                            try {
                                final Long FILE_SIZE = task.getResult().getSizeBytes();
                                final String DOCUMENT_ID = task.getResult().getCustomMetadata("documentId");
                                Log.d(TAG, "We got a good doc reference on document: " + DOCUMENT_ID);
                                imageReference.getBytes(FILE_SIZE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    // Successfully downloaded image
                                    public void onSuccess(byte[] bytes) {
                                        // Try to find mood event
                                        MoodEvent foundMoodEvent = findMoodEventInCacheWithDocumentId(DOCUMENT_ID);
                                        if (foundMoodEvent != null) {
                                            // Load bytes into this mood event as a bitmap.
                                            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                            foundMoodEvent.setReasonImage(bitmap);
                                            Log.d(TAG, "successfully loaded image!");
                                        }
                                    }
                                });
                            } catch (Exception e) {
                                Log.d(TAG, " could not load bytes", e);
                            }

                        }
                    });
                }
            } catch (Exception e) {
                Log.w(TAG, "failed: couldn't download reasonImage", e);
            }
        }
    }

    private MoodEvent convertDocumentToMoodEvent(final QueryDocumentSnapshot document) {
        try {
            MoodEvent newMoodEvent = createBlankMoodEvent();
            Map<String, Object> moodData = document.getData();
            String owner = (((String) moodData.get("owner")).isEmpty()) ? "unknown" : (String) moodData.get("owner");
            Calendar dateTime = new GregorianCalendar();
            dateTime.setTime((moodData.get("timeStamp") == null) ? new Date() : document.getTimestamp("timeStamp").toDate());
            Mood mood;
            String moodString = (moodData.get("mood") == null) ? "Happy" : moodData.get("mood").toString(); // By default happy
            if (moodString.compareTo("Happy") == 0) {
                mood = new Happy();
            } else if (moodString.compareTo("Sad") == 0) {
                mood = new Sad();
            } else if (moodString.compareTo("Disgusted") == 0) {
                mood = new Disgusted();
            } else if (moodString.compareTo("Anxious") == 0) {
                mood = new Anxious();
            } else if (moodString.compareTo("Angry") == 0) {
                mood = new Angry();
            } else {
                mood = new Happy();
            }
            LatLng latlng = null;
            if (moodData.get("location") != null) {
                GeoPoint savedLocation = (GeoPoint) moodData.get("location");
                double lat = savedLocation.getLatitude();
                double lng = savedLocation.getLongitude();
                latlng = new LatLng(lat, lng);
            }
            String reasonText = null;
            if (moodData.get("reasonText") != null) {
                reasonText = moodData.get("reasonText").toString();
            }
            SocialSituation socialSituation = SocialSituation.NONE;
            if (moodData.get("socialSituation") != null) {
                String sitString = moodData.get("socialSituation").toString();
                socialSituation = SocialSituation.fromString(sitString);
            }
            // TODO image load

            // Update all data fields in mood event
            newMoodEvent.setMood(mood);
            newMoodEvent.setTimeStamp(dateTime);
            newMoodEvent.setOwner(new User(owner));
            newMoodEvent.setSocialSituation(socialSituation);
            newMoodEvent.setReasonText(reasonText);
//            newMoodEvent.setReasonImage(reasonImage); // TODO
            newMoodEvent.setDocumentReference(document.getReference());
            newMoodEvent.setLatLng(latlng);

            Log.d(TAG, "MoodEvent created by user: " + newMoodEvent.getOwner().getUserName() + " with documentID: " + document.getReference().getId());
            return newMoodEvent;

        } catch(Exception e){ Log.w(TAG, "Failed to convert document into MoodEvent", e);}
        return null;
    }

    private void parseMoodEventDocumentIntoCache(QueryDocumentSnapshot document){ // Duplicate protection
        /**
         * Convert a FireStore document into mood event object and populates the local cache
         */
        try{
            MoodEvent newMoodEvent = convertDocumentToMoodEvent(document);

            // Duplicate protect
            MoodEvent foundMoodEvent = findMoodEventInCacheWithDocumentId(newMoodEvent.getDocumentReference().getId());
            if (foundMoodEvent != null){
                cachedMoodEvents.remove(foundMoodEvent);
            }
            cachedMoodEvents.add(newMoodEvent);
            // Download any images if existing
            try{
                if (document.getData() != null){
                    if (document.getData().get("reasonImage") != null){
                        Log.d(TAG, "This document has an image, trying to download mood event image");
                        Log.d(TAG, "Trying to download from url: " + document.getData().get("reasonImage"));
                        downloadReasonImageURLForDocumentId((String)document.getData().get("reasonImage"));
//                        downloadReasonImageURLForDocumentId(document.getId());
                    }
                }
            } catch (Exception e){Log.d(TAG, "Failed to download mood event image", e);}
            Log.d(TAG, "mood event parse: successfully parsed" + document.getData());

        } catch (Exception e){Log.d(TAG, "mood event document parse: failed", e);}
    }

    public void updateReasonImageForDocument(String documentId, Bitmap imageBitmap){
        // Put the document id into image metadata
        try{

            final StorageReference storageReference = fbStorage.getReference();
            final StorageReference imageReference = storageReference.child(documentId + ".jpeg");

            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/jpeg")
                    .setCustomMetadata("documentId", documentId)
                    .build();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            final UploadTask uploadTask = imageReference.putBytes(data, metadata);
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return imageReference.getDownloadUrl();

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        UploadTask.TaskSnapshot taskSnapshot = uploadTask.getSnapshot();
                        StorageMetadata successfulMetadata = taskSnapshot.getMetadata();
                        successfulMetadata.getPath();
                        String relevantDocumentID = successfulMetadata.getCustomMetadata("documentId");
                        Log.d(TAG, "successful image upload: " + relevantDocumentID + successfulMetadata.getPath());
                        Log.d(TAG, "download image from: " + downloadUri.toString());

                        HashMap <String, Object> updateField = new HashMap<>();
                        updateField.put("reasonImage", downloadUri.toString());
                        fbFireStore.collection("moodEvents").document(relevantDocumentID)
                                .update(updateField);
                    } else {
                        // Failed to get download uri. Handle failures
                        Log.d(TAG, "failed to get download uri");
                    }
                }
            });
        } catch (Exception e){Log.w(TAG, "failed to upload image to FireStorage", e);}
    }

    private void parseRelationshipDocumentIntoCache(QueryDocumentSnapshot document){
        /**
         * Convert a FireStore document into relationship object and populates the local cache
         * Does duplicate protection
         * @param document - Successfully queried data object from FireStore
         */
        try {
            Relationship newRelationship = convertDocumentToRelationship(document);
            Relationship foundRelationship = null;
            // Duplicate protect
            for (Relationship relationship: cachedRelationship){
                if (relationship.getDocumentId().compareTo(newRelationship.getDocumentId()) == 0){
                    foundRelationship = relationship; // Found in cache - mark to delete
                    break;
                }
            }
            if (foundRelationship != null){
                cachedRelationship.remove(foundRelationship);
            }
            cachedRelationship.add(newRelationship); // No duplicates in cache are guaranteed.
            Log.d(TAG, "relation document parse: successfully parsed" + document.getData());
        } catch(Exception e) {
            Log.d(TAG, "relationship document parse: failed", e);
        }
    }

    private void removeAllCachedMoodEventsFromUser(String userName){
        ArrayList<MoodEvent> moodEventsFromUser = new ArrayList<>();
        for (MoodEvent moodEvent: cachedMoodEvents){
            if (moodEvent.getOwner().getUserName().compareTo(userName) == 0){
                moodEventsFromUser.add(moodEvent);
            }
        }
        if (moodEventsFromUser.size() > 0){
            cachedMoodEvents.removeAll(moodEventsFromUser);
        }
    }

    private void startTrackingMoodEventsForUser(String userName){
        /**
         * If a user's moodevents are not tracked, load into local cache and setup listeners.
         */
        // If already tracked.
        ListenerRegistration listeningToUsersMoodEvents = userMoodEventsUpdateListeners.get(userName);
        if (listeningToUsersMoodEvents == null){ // Not listening for this user's mood events
            Query query = fbFireStore.collection("moodEvents").whereEqualTo("owner", userName); // Create query for user
            registerMoodEventsUpdateListenerForUser(userName, query); // Start listening to this user's mood events.
            Log.d(TAG, "Now tracking mood events for "+ userName);
        }

    }

    private void stopTrackingMoodEventsForUser(String userName){
        ListenerRegistration listeningToUsersMoodEvents = userMoodEventsUpdateListeners.get(userName);
        if (listeningToUsersMoodEvents != null){ // Un-register mood event listener.
            removeAllCachedMoodEventsFromUser(userName);// Since listening is the only way to fetch new mood events.
            listeningToUsersMoodEvents.remove();
            Log.d(TAG, "Stopped tracking mood events for " + userName);
        }

    }

    private void updateMoodEventsListenersFromDocument(QueryDocumentSnapshot documentSnapshot){
        /**
         * Determines if this updated relationship needs to be tracked for mood events from this user or not.
         * If a relationship is updated, we don't want to see mood events from that user.
         *  a -> b : FOLLOWING, Am I sender (a)? YES: Track (b)
         *  a -> b : INVISIBLE, Am I sender (a)? YES: Stop tracking (b)
         *  a -> b : PENDING, Am I sender (a)? YES: Stop tracking (b)
         */
        try {
            Relationship newRelationship = convertDocumentToRelationship(documentSnapshot);
            String myUserName = truncateEmailFromUsername(fbAuth.getCurrentUser().getEmail());
            String sender = newRelationship.getSender().getUserName();
            // Am I involved in this relationship?
            if (newRelationship.getSender().getUserName().compareTo(myUserName) == 0 || newRelationship.getRecipiant().getUserName().compareTo(myUserName) == 0){
                if (newRelationship.getStatus().toString().compareTo(RelationshipStatus.FOLLOWING.toString()) == 0) {
                    // This is a following relationship
                    if (sender.compareTo(myUserName) == 0) {
                        // I am the person following somebody, start tracking their mood events
                        // donald -> mustafa -> FOLLOWING => means donald tracks mustafa's mood events
                        startTrackingMoodEventsForUser(newRelationship.getRecipiant().getUserName());
                    } else if (newRelationship.getStatus().toString().compareTo(RelationshipStatus.INVISIBLE.toString()) == 0){
                        // I was the person sending this request but now its invisible! Don't track this user.
                        if (sender.compareTo(myUserName) == 0) {
                            stopTrackingMoodEventsForUser(newRelationship.getRecipiant().getUserName());
                        }
                    } else if (newRelationship.getStatus().toString().compareTo(RelationshipStatus.PENDING.toString()) == 0){
                        if (sender.compareTo(myUserName) == 0){
                            stopTrackingMoodEventsForUser(newRelationship.getRecipiant().getUserName());
                        }
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
                            Log.d(TAG, "Relationships collection listener: " + documentChange.getType().toString() + " detected!");
                            if (documentChange.getType() == DocumentChange.Type.ADDED){
                                // New relationship with user was added
                                parseRelationshipDocumentIntoCache(documentChange.getDocument());
                                updateMoodEventsListenersFromDocument(documentChange.getDocument());
                            } else if(documentChange.getType() == DocumentChange.Type.MODIFIED){
                                // Find relationship in relationsCache and update it.
                                parseRelationshipDocumentIntoCache(documentChange.getDocument());
                            } else if (documentChange.getType() == DocumentChange.Type.REMOVED){
                                Relationship foundRelationship = null;
                                // Find relationship in relationsCache and remove it.
                                for (Relationship relationship: cachedRelationship){
                                    if (relationship.getDocument() != null){
                                        if (relationship.getDocument().getId().compareTo(documentChange.getDocument().getId()) == 0){
                                            foundRelationship = relationship;
                                            break;
                                        }
                                    }
                                }
                                if (foundRelationship != null){
                                    cachedRelationship.remove(foundRelationship);
                                }
                                updateMoodEventsListenersFromDocument(documentChange.getDocument());
                            }
                        }
                    } catch(Exception er){Log.w(TAG,"Registration update processing error" + er);}
                }
            });
            relationshipsUpdateListeners.put(key, registration);
            Log.d(TAG, "registration listeners: registered listener where user is :" + key + registration);

        } catch (Exception e){
            Log.d(TAG, "Failed to register relationships listener "+key, e);
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
        moodData.put("reasonImage", ""); // TODO
        moodData.put("timeStamp", moodEvent.getTimeStamp().getTime());
        moodData.put("socialSituation", moodEvent.getSocialSituation().toString());
        return moodData;
    }

    private void updateMoodEventFromDocument(MoodEvent moodEvent, QueryDocumentSnapshot document){
        // Loads a data from firestore document into a mood object
        // Used to create a new mood event or update existing one.
        try{
            Map<String, Object> moodData = document.getData();
            String owner = (((String) moodData.get("owner")).isEmpty()) ? "unknown" : (String) moodData.get("owner");
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
            Bitmap reasonImage = null;
            try {
                if (moodData.get("reasonImage") != null) {// TODO
                    // Download the image into bitmap so it can be put into moodEvent
                    // Create a reference to a file from a Google Cloud Storage URI
                    StorageReference gsReference = fbStorage.getReferenceFromUrl((String) moodData.get("reasonImage"));
                    final long FIVE_MEGABYTES = 5120*5120;
                    gsReference.getBytes(FIVE_MEGABYTES).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            return; // TODO
                        }
                    });

                }
            } catch (Exception e) {Log.d(TAG, "Failed to download image from storage: ", e);}


            // Update all data fields in mood event
            moodEvent.setMood(mood);
            moodEvent.setTimeStamp(dateTime);
            moodEvent.setOwner(new User(owner));
            moodEvent.setSocialSituation(socialSituation);
            moodEvent.setReasonText(reasonText);
            moodEvent.setReasonImage(reasonImage);
            moodEvent.setDocumentReference(document.getReference());

            Log.d(TAG, "mood event loaded by user: " + moodEvent.getOwner().getUserName());

        } catch (Exception e){
            Log.d(TAG, "pull Mood Event from firebase: failed to convert hash map to mood event" + e);
        }
    }

    // Communicates with Remote
    public void pushNewMoodEventToRemote(final MoodEvent moodEvent){
        /**
         * Push a local mood event to remote
         */
        Log.d(TAG, "Requesting new mood upload to remote: " + "moodData built");

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

    private void pushRelationshipsToRemotes()
    {
        /**
         * Pushes local cached values to remote
         */
        // TODO
        fst.cachedRelationship = (ArrayList<Relationship>) cachedRelationship.clone();
    }

    private void removeListenersFromHashMap(HashMap<String, ListenerRegistration> map){
        try {
            if (map.entrySet() != null){
                Iterator hashMapIterator = map.entrySet().iterator();
                // Iterate through hashmap and stop listening for updates
                while (hashMapIterator.hasNext()) {
                    Map.Entry mapElement = (Map.Entry) hashMapIterator.next();
                    ListenerRegistration listener = (ListenerRegistration) mapElement.getValue();
                    listener.remove();
                    Log.d(TAG, "removed listener: " + mapElement.getKey());
                }
            }

        } catch (Exception e){Log.w(TAG,"Failed to clear hash map", e);}
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
                // Try to upload image if it was changed
                try{
                    if(moodEvent.getWasImageChanged()){
                        if (moodEvent.getReasonImage() != null){
                            if (moodEvent.getDocumentReference() != null){
                                if (moodEvent.getDocumentReference().getId() != null){
                                    updateReasonImageForDocument(
                                            moodEvent.getDocumentReference().getId(),
                                            moodEvent.getReasonImage()
                                    );
                                }
                            }
                        }
                    }

                } catch (Exception e){Log.d(TAG, "Failed to update reason image of mood event", e);}

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
        clearAllCachedLists();
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

    public void updateRelationshipStatusOnRemote(Relationship relationship){
        try {
            if (relationship.getDocument() != null) {
                String documentId = relationship.getDocument().getId();
                HashMap<String, Object> newRelationshipStatus = new HashMap<>();
                newRelationshipStatus.put("status", relationship.getStatus().toString());
                if (documentId != null) {
                    fbFireStore.collection("relationships").document(documentId)
                            .update(newRelationshipStatus);
                }
            } else { // else document doesn't exist - this is a new relation. update.
                // This is a new relationship, update accordingly
                final HashMap<String, Object> newRelationship = new HashMap<>(); // Construct hash map from relation
                newRelationship.put("a", relationship.getSender().getUserName());
                newRelationship.put("b", relationship.getRecipiant().getUserName());
                newRelationship.put("status", relationship.getStatus().toString());

                fbFireStore.collection("relationships")
                        .whereEqualTo("a", relationship.getSender().getUserName())
                        .whereEqualTo("b", relationship.getRecipiant().getUserName())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                Boolean existingAndUpdated = false;
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (document.getId() != null) {
                                        // Existing relation on the database. UPDATE IT
                                        existingAndUpdated = true;
                                        fbFireStore.collection("relationships").document(document.getId())
                                                .update(newRelationship);
                                    }
                                }
                                if (!existingAndUpdated) {
                                    fbFireStore.collection("relationships").add(newRelationship);
                                }
                            }
                        });
            }
        }catch(Exception e){Log.w(TAG, "Failed to update relationship on database", e);}
    }

    public ArrayList<Relationship> getAllCachedRelationships(){
        Log.d(TAG, "ggias Requesting all cached relationships: priting out of cached Relationships");

        for (Relationship i: cachedRelationship){
            Log.d(TAG, "SendingRelationships: a:" + i.getSender().getUserName() + " b: " + i.getRecipiant().getUserName() +
                    " status: " + i.getStatus().toString());
        }
        return cachedRelationship;
    }

    public ArrayList<MoodEvent> getAllCachedMoodEvents(){
        Log.d(TAG, "qqias Requesting all mood events: printing out cache mood events");
        for (MoodEvent i: cachedMoodEvents){
            Log.d(TAG, "Cached moodEvent: " + i.getInfo() + i.getDocumentReference().get());
        }
        return cachedMoodEvents;
    }

    public ArrayList<User> getAllUsers(){
        Log.d(TAG, "qqias Requesting all users: printing out cache users");
        for (User i: cachedUsers){
            Log.d(TAG, "Cached user: " + i.getUserName());
        }
        return (ArrayList<User>)cachedUsers.clone();
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
