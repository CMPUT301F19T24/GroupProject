package com.example.groupproject.ui.moodlists;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.groupproject.data.moodevents.MoodEvent;
import com.example.groupproject.R;
import com.example.groupproject.data.relations.Relationship;
import com.example.groupproject.data.relations.SocialSituation;

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

    @Override
    protected void onStart() {
        super.onStart();
        initialize();

    }

    private void initialize()
    {
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
                    moodEventAdapter.notifyDataSetChanged();
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
//                TextView tv_timeStamp = popupView.findViewById(R.id.tv_time_stamp_details);
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
//                tv_timeStamp.setText(String.format("%d-%d-%d",
//                        curMoodEvent.getTimeStamp().get(Calendar.DATE),
//                        curMoodEvent.getTimeStamp().get(Calendar.MONTH)+1,
//                        curMoodEvent.getTimeStamp().get(Calendar.YEAR)));

                final Button dateButton = popupView.findViewById(R.id.dateButton);
                final Button timeButton = popupView.findViewById(R.id.timeButton);

//                dateButton.setClickable(false);
//                timeButton.setClickable(false);
//                if(USER_INSTANCE.getUserName() != (curMoodEvent.getOwner().getUserName())){
//                    dateButton.setClickable(false);
//                    timeButton.setClickable(false);
//                }

                initializeTextViews(dateButton, timeButton, curMoodEvent.getTimeStamp());

//                System.out.println("123123123")
//                System.out.println(USER_INSTANCE.getUserName());
//                System.out.println((curMoodEvent.getOwner().getUserName()));
//                System.out.println(USER_INSTANCE.getUserName() == (curMoodEvent.getOwner().getUserName()));

                dateButton.setOnClickListener(new View.OnClickListener() {
                    Calendar calendar = Calendar.getInstance();
                    @Override
                    public void onClick(View view) {
                        if(USER_INSTANCE.getUserName().compareTo(curMoodEvent.getOwner().getUserName()) == 0) {
                            DatePickerDialog datePickerDialog = new DatePickerDialog(MoodEventListActivity.this, new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                    // Month from 0 - 11 so add 1
                                    dateButton.setText(String.format("%04d-%02d-%02d", year, month + 1, day));
                                }
                            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                            datePickerDialog.show();
                        }
                    }
                });

                timeButton.setOnClickListener(new View.OnClickListener() {
                    Calendar calendar = Calendar.getInstance();
                    @Override
                    public void onClick(View view) {
                        if(USER_INSTANCE.getUserName().compareTo(curMoodEvent.getOwner().getUserName()) == 0) {
                            TimePickerDialog timePickerDialog = new TimePickerDialog(MoodEventListActivity.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker timePicker, int hour, int minutes) {
                                    timeButton.setText(String.format("%02d:%02d", hour, minutes));
                                }
                            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                            timePickerDialog.show();
                        }
                    }
                });

                // Setup spinner
                s_socialSituation.setAdapter(new ArrayAdapter<String>(MoodEventListActivity.this, simple_spinner_item, SocialSituation.getNames()));
                s_socialSituation.setSelection(Arrays.asList(SocialSituation.values()).indexOf(curMoodEvent.getSocialSituation()));

                et_desc.setText(curMoodEvent.getReasonText());


                b_image_from_camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO
                        openCamera();

                    }
                });

                b_image_from_photos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // TODO
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

                        try
                        {
                            String desc = et_desc.getText().toString();
                            if(desc.length() >= 20)
                            {
                                throw new Exception("Description must be 20 chars or less");
                            }

                            if(desc.length() - desc.replaceAll(" ", "").length() > 2)
                            {
                                throw new Exception("Description must be 3 words or less");
                            }

                            curMoodEvent.setReasonText(desc);
                            curMoodEvent.setReasonImage(bitmap);
                            curMoodEvent.setSocialSituation(SocialSituation.values()[s_socialSituation.getSelectedItemPosition()]);

                            FSH_INSTANCE.getInstance().fsh.editMoodEvent(curMoodEvent);
                            popupWindow.dismiss();
                            moodEventAdapter.notifyDataSetChanged();

                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed to modify: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
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


//                    imageView.setImageURI(imageUri);
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

        user.add(USER_INSTANCE.getUserName()); // Add myself to list of users.
        for(Relationship i : rs)
        {
            if(i.getSender().getUserName().compareTo(USER_INSTANCE.getUserName()) == 0 && i.isVisible())
            {
                System.out.println("Adding " + i.toString());

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

    private void initializeTextViews(Button dateButton, Button timeButton, Calendar currTime) {
        String year = Integer.toString(currTime.getTime().getYear() + 1900);
        int monthInt = currTime.getTime().getMonth();
        String month = (monthInt >= 10) ? Integer.toString(monthInt) : String.format("0%s",Integer.toString(monthInt));
        int dayInt = currTime.getTime().getDate();
        String day = (dayInt >= 10) ? Integer.toString(dayInt) : String.format("0%s",Integer.toString(dayInt));

        dateButton.setText(year + "-" + month + "-" + day);

        int hoursInt = currTime.getTime().getHours();
        String hours = (hoursInt >= 10) ? Integer.toString(hoursInt) : String.format("0%s",Integer.toString(hoursInt));

        int minutesInt = currTime.getTime().getMinutes();
        String minutes = (minutesInt >=10) ? Integer.toString(minutesInt) : String.format("0%s",Integer.toString(minutesInt));

        timeButton.setText(hours + ":" + minutes);
    }

}
