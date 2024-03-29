package com.example.groupproject.data.firestorehandler;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class FireStoreHandler {
    // Testing
    private FirestoreTester fst;
    private FirebaseAuth fbAuth = FirebaseAuth.getInstance();

    protected ArrayList<MoodEvent> cachedMoodEvents;
    protected ArrayList<User> cachedUsers;
    protected ArrayList<Relationship> cachedRelationship;

    //Firebase
    FirebaseStorage storage;
    StorageReference storageReference;

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

        pushMoodEventListToRemote();
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
            if(i.getTimeStamp().compareTo(me.getTimeStamp()) == 0 && i.getOwner().getUserName().compareTo(me.getOwner().getUserName()) == 0)
            {
                cachedMoodEvents.remove(cachedMoodEvents.indexOf(i));

                pushMoodEventListToRemote();
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
        final String user = username + "@cmput301-c6741.web.app";
        final String currentUser = username;
        fbAuth.signInWithEmailAndPassword(user, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override

                    public void onComplete(@NonNull Task<AuthResult> task) {
                        TextView errorMessage = view.getRootView().findViewById(R.id.credentialMessage);
                        if (task.isSuccessful()) {
                            // If login was successful print statement to the log and change view
                            Log.d(TAG, "loginUserWithEmail:successful");
                            Intent intent = new Intent(view.getRootView().getContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            MainActivity.USER_INSTANCE = new User(currentUser);
                            view.getRootView().getContext().startActivity(intent);
                        } else {
                            // If login fails print statement to the log, and catch exceptions
                            Log.w(TAG, "loginUserWithEmail:failed");

                            // Catches specific exceptions as well as a generic catch all
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthInvalidUserException e) {
                                //editText.setError("User does not exist");
                                errorMessage.setVisibility(view.VISIBLE);
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                //passwordText.setError("Incorrect Password");
                                errorMessage.setVisibility(view.VISIBLE);
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
                                Log.w(TAG, "createUserWithEmail:usernameUnavailable");
                            } catch (FirebaseAuthWeakPasswordException e) {
                                passwordText.setError("Password must be at least 6 characters");
                                Log.w(TAG, "createUserWithEmail:weakPassword");
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                editText.setError("Username cannot contain spaces or any of the following: "+
                                                  ". [ ] ( ) \" @ : ; \" < > \\ or ,");
                                Log.w(TAG, "createUserWithEmail:illegalCharacter");
                            } catch (Exception e) {
                                Log.w(TAG, "createUserWithEmail:failed");
                                Toast.makeText(view.getContext(), "An unexpected error occurred while creating account", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }


    public void deleteUser(String username) {
        username = username + "@cmput301-c6741.web.app";
        fbAuth.getCurrentUser().delete();
    }

    public String getCurrentUser(){
        String fetchedUser = fbAuth.getCurrentUser().getDisplayName().toString();
        fetchedUser = fetchedUser.substring(0, fetchedUser.length()-21);
        return  (fetchedUser);
    }

    public void uploadImage(Uri filePath){
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        if(filePath != null)
        {
//            final ProgressDialog progressDialog = new ProgressDialog(this);
//            progressDialog.setTitle("Uploading...");
//            progressDialog.show();

            StorageReference ref = storageReference.child(UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            progressDialog.dismiss();
//                            Toast.makeText(FireStoreHandler.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
//                            progressDialog.dismiss();
//                            Toast.makeText(MainActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
//                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

    public void uploadImageFromCamera(Bitmap bitmap){
        storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();


        StorageReference mountainsRef = storageRef.child(UUID.randomUUID().toString());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainsRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });
    }

    public void getDownloadUrl() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://cmput301-c6741.appspot.com").child("android.jpg");

        try {
            final File localFile = File.createTempFile("images", "jpg");
            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
//                    mImageView.setImageBitmap(bitmap);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });
        } catch (IOException e) {
        }
    }
}
