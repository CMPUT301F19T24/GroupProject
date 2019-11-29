package com.example.groupproject.ui.moodlists;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.groupproject.data.moodevents.MoodEvent;
import com.example.groupproject.R;
import com.example.groupproject.data.relations.Relationship;
import com.example.groupproject.data.relations.SocialSituation;
import com.example.groupproject.data.user.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import static android.R.layout.simple_spinner_item;
import static com.example.groupproject.MainActivity.FSH_INSTANCE;
import static com.example.groupproject.MainActivity.USER_INSTANCE;


public class MoodEventListActivity extends AppCompatActivity {

    private ListView moodEventList;
    private ListMoodEventsAdapter moodEventAdapter;
    private ArrayList<MoodEvent> moodEventDataList;

    // Defines
    private static final SortingMethod DEFAULT_SORTING_METHOD = SortingMethod.DATE_NTOO;

    private static final int PICK_IMAGE = 0;
    private static final int CAMERA_PIC_REQUEST = 1;


    Uri imageUri;
    Bitmap bitmap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("MoodEventListActivity");
        setContentView(R.layout.v_list_mood_events);

        TextView tv_currentUserName = findViewById(R.id.tv_user_name);
        tv_currentUserName.setText(USER_INSTANCE.getUserName());

        initialize();
    }

    private void initialize()
    {
//        ImageView iv_desc = findViewById(R.id.iv_img_desc);
        /**
         * Initializes the private variables of this class & View elements
         */
        moodEventList = findViewById(R.id.moodEventList);
        moodEventDataList = new ArrayList<>();
        moodEventAdapter = new ListMoodEventsAdapter(this, moodEventDataList, DEFAULT_SORTING_METHOD);
        moodEventList.setAdapter(moodEventAdapter);

        setupPopUpMenu();
        setupSorting();
        setupSearching();

        moodEventAdapter.addAll(populateFromRemote());
        moodEventAdapter.notifyDataSetChanged();

        for(MoodEvent a: moodEventDataList){
            System.out.println(a.getReasonText());
            System.out.println(a.getTimeStamp().toString());
            System.out.println("");

        }

    }

    private void setupSorting()
    {
        /**
         * Initializes the spinner object responsible for ordering the moodevents
         */
        final Spinner s_sortBy = findViewById(R.id.s_sortby);
        s_sortBy.setAdapter(new ArrayAdapter<>(MoodEventListActivity.this, simple_spinner_item, SortingMethod.getNames()));
        s_sortBy.setSelection(Arrays.asList(SortingMethod.values()).indexOf(DEFAULT_SORTING_METHOD)); // Default
        moodEventAdapter.setSortingMethod(DEFAULT_SORTING_METHOD);

        s_sortBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                moodEventAdapter.setSortingMethod(SortingMethod.values()[i]);
                moodEventList.clearChoices();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Do nothing
            }
        });
    }

    private void setupSearching()
    {
        /**
         * Initializes the search box responsible for filtering the mood events
         */
        final Spinner s_sortBy = findViewById(R.id.s_sortby);
        EditText ed_searchFor = findViewById(R.id.et_searchFor);

        ed_searchFor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                /**
                 * Hide the header to provide more room
                 *
                 * @param - See base method for details
                 */
                LinearLayout ll = findViewById(R.id.ll_header);
                ll.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                /**
                 * Update the listview with every keystroke
                 *
                 * @param - See base method for details
                 */
                String query = charSequence.toString();

                moodEventAdapter.clear();
                if (query.isEmpty())
                {
                    moodEventAdapter.addAll(populateFromRemote());
                    moodEventAdapter.setSortingMethod(SortingMethod.values()[s_sortBy.getSelectedItemPosition()]);

                }
                else
                {
                    for(MoodEvent me : populateFromRemote())
                    {
                        if(me.contains((query)))
                        {
                            moodEventAdapter.add(me);
                        }
                    }
                }

                moodEventAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                /**
                 * Renable the header after keyboard closes
                 *
                 * @param - See base method for details
                 */
                LinearLayout ll = findViewById(R.id.ll_header);
                ll.setVisibility(View.VISIBLE);
            }
        });

    }

    private void setupPopUpMenu()
    {
        /**
         * Initializes the popup view that displays a moodevent's detailed information
         */
        moodEventList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Initialize Accessors
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                final View popupView = inflater.inflate(R.layout.v_list_mood_events_details, null);
                final PopupWindow popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                final MoodEvent curMoodEvent = (MoodEvent) moodEventList.getItemAtPosition(i);

                popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                popupWindow.setOutsideTouchable(true);

                LinearLayout ll_header = popupView.findViewById(R.id.ll_detail_header);
                TextView tv_moodName = popupView.findViewById(R.id.tv_mood_name_details);
                TextView tv_timeStamp = popupView.findViewById(R.id.tv_time_stamp_details);
                final Spinner s_socialSituation = popupView.findViewById(R.id.s_details_social_situation);
                final EditText et_desc = popupView.findViewById(R.id.tv_desc);
                ImageView iv_desc = popupView.findViewById(R.id.iv_img_desc);

                // TODO: Renable me
//                LinearLayout ll_detailedMap = popupView.findViewById(R.id.ll_detailed_map);
//                MapView mv_map = popupView.findViewById(R.id.mv_detail_map_view);

                if(curMoodEvent.getReasonImage() != null)
                {
                    iv_desc.setImageBitmap(curMoodEvent.getReasonImage());
                }

                Button b_apply = popupView.findViewById(R.id.b_apply);
                Button b_delete = popupView.findViewById(R.id.b_delete);
                Button b_image_from_camera = popupView.findViewById(R.id.b_add_from_camera);
                Button b_image_from_photos = popupView.findViewById(R.id.b_add_from_photo);

                if(curMoodEvent.getOwner().getUserName().compareTo(USER_INSTANCE.getUserName()) != 0)
                {
                    // Disable features if this mood event is not owned by the current user
                    s_socialSituation.setEnabled(false);
                    b_apply.setVisibility(View.GONE);
                    b_delete.setVisibility(View.GONE);
                    b_image_from_camera.setVisibility(View.GONE);
                    b_image_from_photos.setVisibility(View.GONE);
                }

                // Populate display
                ll_header.setBackgroundColor(curMoodEvent.getMood().getColor());
                tv_moodName.setText(curMoodEvent.getMood().getName());
                tv_timeStamp.setText(String.format("%d-%d-%d",
                        curMoodEvent.getTimeStamp().get(Calendar.DATE),
                        curMoodEvent.getTimeStamp().get(Calendar.MONTH)+1,
                        curMoodEvent.getTimeStamp().get(Calendar.YEAR)));

                // Setup spinner
                s_socialSituation.setAdapter(new ArrayAdapter<String>(MoodEventListActivity.this, simple_spinner_item, SocialSituation.getNames()));
                s_socialSituation.setSelection(Arrays.asList(SocialSituation.values()).indexOf(curMoodEvent.getSocialSituation()));


                if(curMoodEvent.getLatLng() == null)
                {
//                    ll_detailedMap.setVisibility(View.GONE);
                }

                et_desc.setText(curMoodEvent.getReasonText());


                b_image_from_camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openCamera();

                    }
                });

                b_image_from_photos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        openGallery();

                    }
                });

                b_apply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /**
                         * Replace the moodevent in the remote with the updated copy
                         * @@param - See base method for details
                         */
                        curMoodEvent.setReasonText(et_desc.getText().toString());

                        // TODO: Add me
                        curMoodEvent.setReasonImage(bitmap);
//                        curMoodEvent.setLocation();
                        curMoodEvent.setSocialSituation(SocialSituation.values()[s_socialSituation.getSelectedItemPosition()]);

                        FSH_INSTANCE.getInstance().fsh.editMoodEvent(curMoodEvent);
                        popupWindow.dismiss();
                        moodEventAdapter.notifyDataSetChanged();

                    }
                });

                b_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /**
                         * Delete moodevent from adapter, and from remote
                         * @param - See base method for details
                         */
                        moodEventAdapter.remove(curMoodEvent);
                        FSH_INSTANCE.getInstance().fsh.deleteMoodEvent(curMoodEvent);
                        moodEventAdapter.notifyDataSetChanged();
                        popupWindow.dismiss();

                    }
                });
            }
        });
    }

    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    private void openCamera(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case PICK_IMAGE:
                if(resultCode == RESULT_OK){
                    imageUri = data.getData();
                    ImageView iv_desc_view = findViewById(R.id.iv_img_desc);
                    iv_desc_view.setImageURI(imageUri);

                    int curPosition = moodEventList.getSelectedItemPosition();
                    if (moodEventDataList.get(curPosition) != null) {
                        MoodEvent moodEvent = moodEventDataList.get(curPosition);
                        if (moodEvent != null) {
                            // Convert image view's image to bitmap
                            try {
                                BitmapDrawable drawable = (BitmapDrawable) iv_desc_view.getDrawable();
                                bitmap = drawable.getBitmap();
                                moodEvent.setReasonImage(bitmap);
                                moodEvent.setWasImageChanged(true);

                            } catch (Exception e) {
                                Log.d("bitmapping", "successful");
                            }
                        }
                    }
                }

                break;
            case CAMERA_PIC_REQUEST:
                if(resultCode == RESULT_OK){
                    imageUri = data.getData();
//                    imageView.setImageURI(imageUri);
                    Bundle extras = data.getExtras();
                    bitmap = (Bitmap) extras.get("data");

                    ImageView iv_desc_view = findViewById(R.id.iv_img_desc);
                    iv_desc_view.setImageURI(imageUri);

                    int curPosition = moodEventList.getSelectedItemPosition();
                    if (moodEventDataList.get(curPosition) != null) {
                        MoodEvent moodEvent = moodEventDataList.get(curPosition);
                        if (moodEvent != null) {
                            // Convert image view's image to bitmap
                            try {
                                BitmapDrawable drawable = (BitmapDrawable) iv_desc_view.getDrawable();
                                bitmap = drawable.getBitmap();
                                moodEvent.setReasonImage(bitmap);
                                moodEvent.setWasImageChanged(true);

                            } catch (Exception e) {
                                Log.d("bitmapping", "successful");


                            }
                        }
                    }
                }
                break;
        }
    }

    private ArrayList<MoodEvent> populateFromRemote()
    {
        /**
         * Fetches all mood events the current user is allowed to see from the remote.
         */
        ArrayList<MoodEvent > me = FSH_INSTANCE.getInstance().fsh.getAllCachedMoodEvents();
        ArrayList<Relationship> rs = FSH_INSTANCE.getInstance().fsh.getAllCachedRelationships();
        ArrayList<String> user = new ArrayList<>();
        ArrayList<MoodEvent > rc = new ArrayList<>();

        Log.d("pfr debug mood event:", "list of cachedMoodEvents. size: " + me.size());
        for (MoodEvent i: me){
            Log.d("pfr debug mood event: ", i.toString());
        }

        Log.d("pfr debug relationship:", "list of cachedRelationships. size: " + rs.size());
        for (Relationship i: rs){
            Log.d("pfr debug mood event: ", "sender :" + i.getSender().getUserName() + " recipient: " +  i.getRecipiant().getUserName() + " status: " + i.getStatus().toString());
        }

        Log.d("pfr debug mood event:", "user's name is: " + USER_INSTANCE.getUserName());
        user.add(USER_INSTANCE.getUserName()); // Add myself to list of users.
        for(Relationship i : rs)
        {
            if(i.getSender().getUserName().compareTo(USER_INSTANCE.getUserName()) == 0 && i.isVisible())
            {
                user.add(i.getRecipiant().getUserName());
            }
        }

        for(MoodEvent i : me)
        {
            System.out.println("Debugger " + i.toString());

            if(user.contains(i.getOwner().getUserName()))
            {
                rc.add(i);
            }
        }
        return rc;

    }

}
