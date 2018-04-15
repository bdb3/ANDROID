package com.example.edwin.photoarchive.Activities;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Spinner;

import com.example.edwin.photoarchive.Adapters.PagerAdapter;
import com.example.edwin.photoarchive.AzureClasses.Field;
import com.example.edwin.photoarchive.AzureClasses.Category;
import com.example.edwin.photoarchive.R;
import com.example.edwin.photoarchive.TabFragment2;
import com.example.edwin.photoarchive.TabFragment3;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements TabFragment2.SendFields {

    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Spinner categorySpinner = (Spinner) findViewById(R.id.categorySpinner);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Dash"));
        tabLayout.addTab(tabLayout.newTab().setText("Tags"));
        tabLayout.addTab(tabLayout.newTab().setText("Camera"));
        tabLayout.addTab(tabLayout.newTab().setText("History"));
        tabLayout.addTab(tabLayout.newTab().setText("Settings"));

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_home);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_upload);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_camera);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_history);
        tabLayout.getTabAt(4).setIcon(R.drawable.ic_action_settings);

        tabLayout.getTabAt(0).getIcon().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).getIcon().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(2).getIcon().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(3).getIcon().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(4).getIcon().setColorFilter(Color.parseColor("#FFFFFF"), PorterDuff.Mode.SRC_IN);

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(5);

        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int position = extras.getInt("viewpager_position");
            viewPager.setCurrentItem(position);
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                if (tab.getPosition() == 0) {
                    setTitle("Dashboard");
                } else if (tab.getPosition() == 1) {
                    setTitle("Tags");
                } else if (tab.getPosition() == 2) {
                    setTitle("Camera");
                } else if (tab.getPosition() == 3) {
                    setTitle("History");
                } else {
                    setTitle("Application Settings");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    // Data Transit method implemented from interface
    @Override
    public void sendData(Category targetCat, ArrayList<Field> fields, ArrayList<String> values) {
        TabFragment3 tabFrag3 = null;
        for (Fragment frag : getSupportFragmentManager().getFragments()) {
            if (frag instanceof TabFragment3) {
                tabFrag3 = (TabFragment3) frag;
            }
        }
        tabFrag3.recData(targetCat, fields, values);
    }
}