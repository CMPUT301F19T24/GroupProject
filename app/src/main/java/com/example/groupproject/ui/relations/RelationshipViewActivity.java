package com.example.groupproject.ui.relations;

import android.os.Bundle;

import com.example.groupproject.R;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.example.groupproject.ui.relations.SectionsPagerAdapter;

public class RelationshipViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * Initialize the views and the fragments
         **/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v_relationship);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        sectionsPagerAdapter.notifyDataSetChanged();
    }
}

