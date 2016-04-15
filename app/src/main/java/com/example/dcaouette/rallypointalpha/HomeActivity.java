package com.example.dcaouette.rallypointalpha;

import android.app.ActionBar;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Switch;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class HomeActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private FloatingActionButton addMemberFab;
    private FloatingActionButton addTeamFab;
    private MembersFragment memFrag;
    private TeamsFragment teamsFrag;

    private Firebase mRef;

    private static final int MEMBERS_PAGE = 0;
    private static final int TEAMS_PAGE = 1;
    private int currentPage = MEMBERS_PAGE;

    private String pageTitle = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        setContentView(R.layout.activity_home);

        if (savedInstanceState == null) {
            Firebase.setAndroidContext(this);
        }

        mRef = new Firebase(QuickRefs.ROOT_URL);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        AppBarLayout.LayoutParams toolbarParams = (AppBarLayout.LayoutParams) toolbar.getLayoutParams(); // Fix scrolling issue with recycler view
        toolbarParams.setScrollFlags(0);
        setSupportActionBar(toolbar);

        // Retrieve the correct starting page values
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        currentPage = sharedPreferences.getInt("STARTING_POS", 0);
        //pageTitle = sharedPreferences.getString("HOME_TITLE", "");

        // Change title to be user's email
        if (getSupportActionBar() != null) {
            //getSupportActionBar().setTitle(pageTitle);
            final String userKey = mRef.getAuth().getUid();
            mRef.child("users").child(userKey).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    user.setKey(dataSnapshot.getKey());
                    String userEmail = user.getEmail();
                    if (!pageTitle.equals(userEmail)) {
                        pageTitle = user.getEmail();
                        getSupportActionBar().setTitle(pageTitle);
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }

        addMemberFab = (FloatingActionButton) findViewById(R.id.add_member_fab);
        final Intent memberSearchIntent = new Intent(this, SearchMemberActivity.class);
        addMemberFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //memFrag.addMember();
                startActivity(memberSearchIntent);
            }
        });

        addTeamFab = (FloatingActionButton) findViewById(R.id.add_team_fab);
        final Intent intent = new Intent(this, CreateTeamActivity.class);
        addTeamFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent);
            }
        });

        // Make sure the focus goes back to the teams page when the create team finishes
        Bundle myBundle = getIntent().getExtras();
        if (myBundle != null) {
            currentPage = myBundle.getInt("START_POS", 1);
        }

        determineFabVisibility();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(currentPage);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);



        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

                switch (position) {
                    case MEMBERS_PAGE:
                        currentPage = MEMBERS_PAGE;
                        determineFabVisibility();
                        break;
                    case TEAMS_PAGE:
                        currentPage = TEAMS_PAGE;
                        determineFabVisibility();
                        break;

                    default:
                        currentPage = MEMBERS_PAGE;
                        determineFabVisibility();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            mRef.unauth();
            startActivity(new Intent(this, MainActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }


        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case MEMBERS_PAGE:
                    return memFrag = MembersFragment.newInstance();
                case TEAMS_PAGE:
                    return teamsFrag = TeamsFragment.newInstance();
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Members";
                case 1:
                    return "Teams";
            }
            return null;
        }
    }

    public void determineFabVisibility() {
        if (currentPage == MEMBERS_PAGE) {
            addTeamFab.hide();
            addMemberFab.show();
        } else {
            addMemberFab.hide();
            addTeamFab.show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        determineFabVisibility();
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("STARTING_POS", currentPage);
        //editor.putString("HOME_TITLE", pageTitle);
        editor.apply();
    }

}
