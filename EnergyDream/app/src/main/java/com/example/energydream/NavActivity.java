package com.example.energydream;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.energydream.Model.Business;
import com.example.energydream.Model.CompanyMember;
import com.example.energydream.Model.Member;
import com.example.energydream.Model.StandyPower;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Stack;

public class NavActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    // fragment number
    public final static int FRAGMENT_MAIN = 1000;
    public final static int FRAGMENT_LOGIN = 1001;
    public final static int FRAGMENT_ADD_CONCENT = 1004;
    public final static int FRAGMENT_STATISTICS = 1005;
    public final static int FRAGMENT_DONATION_LIST = 1006;
    public final static int FRAGMENT_VENTURE_LIST = 1007;
    public final static int FRAGMENT_DONATION = 1008;
    public final static int FRAGMENT_MYPAGE = 1009;
    public final static int FRAGMENT_MYCOMPPAGE = 1010;
    public final static int FRAGMENT_DETAIL = 1011;
    public final static int FRAGMENT_APPROVE = 1012;
    public final static int FRAGMENT_APPROVE_DETAIL = 1013;

    // fragment
    private static MainActivity mainActivity;
    private static DonationActivity donationFragment;
    private static DonationListActivity donationListFragment;
    private static VentureListActivity ventureListFragment;
    private static DetailActivity detailFragment;
    private static AddConcentActivity addConcentFragment;
    private static StatisticsActivity statisticsFragment;
    private static HistoryActivity historyFragment;
    private static MypageActivity mypageFragment;
    private static MyCompPageActivity myCompPageFragment;
    private static ApproveBusinessActivity approveBusinessFragment;
    private static ApproveDetailActivity approveDetailFragment;


    public static Member login_user;
    public static CompanyMember login_comp_user;
    public static ArrayList<CompanyMember> company_list;
    public static ArrayList<Business> venture_list;
    public static ArrayList<Business> donation_list;
    public static ArrayList<Business> myCompBusi_list; // ?????? ???????????? ???????????? ???????????????
    public static ArrayList<Business> notApprove_list;
    public static ArrayList<Member.Donation_user> history_list;
    public static StandyPower standy_power;
    public static Stack<Fragment> stack_fragment;

    public static DatabaseReference m_reference;
    public static StorageReference s_reference;
    public static FirebaseUser m_user;       // ????????? ????????? ?????? ?????? ??????
    public static FragmentManager manager;
    public static FragmentTransaction transaction;


    static TextView nav_login;
    static TextView nav_logout;
    static TextView nav_email;
    static MenuItem nav_add_concent;        // ????????? ?????? ??????
    static MenuItem nav_donation_list;      // ????????? ????????????
    static MenuItem nav_venture_list;       // ???????????? ????????????
    static MenuItem nav_history;            // ?????? ??????
    static MenuItem nav_statics;            // ??????
    static MenuItem nav_add_business;       // ?????? ????????????
    static MenuItem nav_approve_business;   // ?????? ????????????
    static MenuItem nav_mypage;             // ???????????????
    static MenuItem nav_add_donation;       // ?????????????????????
    static ImageView nav_setting;
    static Toolbar toolbar;          // ????????????

    NavigationView navigationView;
    DrawerLayout drawer;
    View headerView;
    //boolean isCompany;  // ?????????????????? ??????-true, ??????-false


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);

        // LoginActivity ????????? ??????
        Intent intent = getIntent();
        login_user = (Member)intent.getSerializableExtra("Member");
        login_comp_user = (CompanyMember) intent.getSerializableExtra("CompanyMember");
        myCompBusi_list = (ArrayList<Business>)intent.getSerializableExtra("BusinessList");

        mainActivity = new MainActivity();

        manager = getSupportFragmentManager();
        transaction = manager.beginTransaction();
        transaction.replace(R.id.content_fragment_layout, mainActivity).commit();
        stack_fragment = new Stack<>();
        stack_fragment.push(mainActivity);  // ?????? ?????????(MainFragment ????????? ??????)

        m_reference = FirebaseDatabase.getInstance().getReference();
        s_reference = FirebaseStorage.getInstance().getReference();
        m_user = FirebaseAuth.getInstance().getCurrentUser();

        donationFragment = new DonationActivity();
        donationListFragment = new DonationListActivity();
        ventureListFragment = new VentureListActivity();
        detailFragment = new DetailActivity();
        addConcentFragment = new AddConcentActivity();
        statisticsFragment = new StatisticsActivity();
        mypageFragment = new MypageActivity();
        myCompPageFragment = new MyCompPageActivity();
        approveBusinessFragment = new ApproveBusinessActivity();
        approveDetailFragment = new ApproveDetailActivity();
        historyFragment = new HistoryActivity();

        venture_list = new ArrayList<>();
        donation_list = new ArrayList<>();
        notApprove_list = new ArrayList<>();
        company_list = new ArrayList<>();
        history_list = new ArrayList<>();

       // history_list = login_user.getDonationList();
        initView();

        //getHistoryList();
        getBusinessList();
        updateUI(login_user, login_comp_user);
        if(login_user!=null )
            FB_user_listening(); // Firebase ????????? Listening  // ????????????

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackground(new ColorDrawable(0xffffffff)); // ???????????? ??? ??????


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        Log.d("???????????????","?????????");
        /////????????? ??????//////
        onNewIntent(getIntent());
        ////////////////


        // ????????? ?????? ?????? ????????? ?????? ???????????? ????????? ????????? ????????? ??????


        // ????????? / ????????????
        nav_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stack_fragment.push(mainActivity);
                changeFragment(FRAGMENT_LOGIN);
                drawer.closeDrawers();
            }
        });
        nav_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(login_user != null || login_comp_user != null) {
                    finish();
                    //logout();
                }else {  //testing (???????????? ???????????? ????????? ?????? ?????? ??????)

                }
            }
        });
        nav_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(login_comp_user != null){
                   changeFragment(FRAGMENT_MYCOMPPAGE);

                    drawer.closeDrawers();
                }
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nav, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            changeFragment(FRAGMENT_LOGIN);
            drawer.closeDrawers();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;
        String title = "";
        if (id == R.id.nav_camera) {
            fragment = new AddConcentActivity();
            title = "????????? ?????? ??????";
        } else if (id == R.id.nav_gallery) {
            fragment = new HistoryActivity();
            title = "?????? ??????";
        } else if (id == R.id.nav_slideshow) {
            fragment = new StatisticsActivity();
            title ="??????";
        } else if (id == R.id.nav_share) {
            fragment = new DonationListActivity();
            title = "????????? ????????????";
        } else if (id == R.id.nav_send) {
            fragment = new VentureListActivity();
            title = "???????????? ????????????";
        }else if(id == R.id.nav_mypage){
            if(login_user != null)
                fragment = new MypageActivity();
            else if(login_comp_user != null)
                fragment = new MyCompPageActivity();
            title = "???????????????";
        }else if(id == R.id.nav_add_business){
            fragment = new EnrollBusinessActivity();
            title = "????????????";
        }else if(id == R.id.nav_approve_business){
            fragment = new ApproveBusinessActivity();
            title = "????????????";
        }else if(id == R.id.nav_add_donation){
            fragment = new EnrollDonationActivity();
            title = "??????????????? ??????";
        }else if(id == R.id.get_all_business) {
            fragment = new AdminStatisticsActivity();
            title = "DB ??????";
        }

        if (fragment != null) {
            // ???????????? ?????? ?????? ??????????????? ??????
            stack_fragment.push(mainActivity);

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_fragment_layout, fragment);
            ft.commit();
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // ????????? ?????? ?????????????????? ??????????????? ??? ?????????????????? ??????
            if(!stack_fragment.isEmpty()){
                Fragment next_fragment = stack_fragment.pop();
                manager.beginTransaction().replace(R.id.content_fragment_layout, next_fragment).commit();
            }else {
                super.onBackPressed();
            }
        }
    }


    private void initView(){
        navigationView = (NavigationView)findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerView = navigationView.getHeaderView(0);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        nav_login = (TextView)headerView.findViewById(R.id.nav_login);
        nav_logout = (TextView)headerView.findViewById(R.id.nav_logout);
        nav_email = (TextView)headerView.findViewById(R.id.nav_email);
        nav_setting = (ImageView)headerView.findViewById(R.id.image_setting);


        Log.v("??????"," " + navigationView.getMenu().getItem(6));

        nav_add_concent = navigationView.getMenu().getItem(0);
        nav_donation_list = navigationView.getMenu().getItem(1);
        nav_venture_list = navigationView.getMenu().getItem(2);
        nav_history = navigationView.getMenu().getItem(3);
        nav_statics = navigationView.getMenu().getItem(4);
        nav_add_business = navigationView.getMenu().getItem(5);
        nav_approve_business = navigationView.getMenu().getItem(6);
        nav_mypage = navigationView.getMenu().getItem(7);


        nav_email.setVisibility(View.INVISIBLE);
        nav_history.setVisible(false);
        nav_statics.setVisible(false);
        nav_add_business.setVisible(false);
        nav_approve_business.setVisible(false);
        nav_add_concent.setVisible(true);
        Log.v("reference", ""+(m_reference.child("StandyPower")==null));

        //nav_add_donation.setVisible(false);
        nav_mypage.setVisible(false);
        nav_setting.setVisibility(View.INVISIBLE);
    }
    public boolean getStandyPower(){
        if(standy_power==null){
            System.out.println("[TESTING] standyPower is null!");
            return true;
        }
        else {
            System.out.println("[TESTING] >>>standyPower is not null!");
            return false;
        }

    }

    public void getBusinessList(){
                            m_reference.child("CompanyMembers").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    donation_list.clear();
                                    venture_list.clear();
                                    notApprove_list.clear();
                                    company_list.clear();

                                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                        CompanyMember companyMember = snapshot.getValue(CompanyMember.class);
                                        company_list.add(companyMember);
                                        ArrayList<Business> tmp_list = companyMember.getBusinessList();
                                        if(tmp_list != null){
                                            for(int i=0; i<tmp_list.size(); i++){
                            // ????????????, ????????????????????? ????????? ???????????? ??????
                            if(!tmp_list.get(i).isVenture())
                                donation_list.add(tmp_list.get(i));
                            else{
                                if(tmp_list.get(i).getState()==1)  // ????????? ?????? ?????????
                                    venture_list.add(tmp_list.get(i));
                                else
                                    notApprove_list.add(tmp_list.get(i));
                            }
                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public static void updateUser(){
        // ??????????????? ???????????? ??????
        if(login_user != null)
            m_reference.child("Members").child(login_user.getName()).setValue(login_user);
    }

    // Modify
    public void updateUI(Member user_person, CompanyMember user_comp){

        // ????????????
        if(user_person == null && user_comp == null){

            nav_login.setText("?????????????????????.");
            nav_email.setVisibility(View.INVISIBLE);
            nav_logout.setVisibility(View.INVISIBLE);
            nav_add_concent.setVisible(false);
            nav_history.setVisible(false);
            nav_statics.setVisible(false);
            nav_add_business.setVisible(false);
            nav_approve_business.setVisible(false);
            nav_setting.setVisibility(View.INVISIBLE);
        }
        else{ //?????????


            if(user_person != null){

                // 1) ?????? ?????????
                nav_login.setText(login_user.getName());
                nav_email.setText(login_user.getEmail());
                nav_approve_business.setVisible(false);
                nav_statics.setVisible(true);

                nav_history.setVisible(true);
                nav_mypage.setVisible(false);
                nav_setting.setVisibility(View.INVISIBLE);
            }
            else{
               // 2) ????????? ?????????
                if(login_comp_user.getCor_name().equals("admin")){
                    nav_login.setText("?????????");
                    nav_add_concent.setVisible(false);
                    nav_approve_business.setVisible(true);      // ?????? ????????????
                    nav_mypage.setVisible(false);               // ????????? ???????????????
                }
                // 3) ?????? ?????????
                else{
                    nav_login.setText(login_comp_user.getCor_name());    // ?????????
                    nav_add_business.setVisible(true);// ??????????????????
                    nav_add_concent.setVisible(false);
                    nav_mypage.setVisible(true);            // ????????? ???????????????
                }
                nav_email.setText(login_comp_user.getCor_num());            // ???????????????

                nav_setting.setVisibility(View.VISIBLE);
            }

            nav_logout.setVisibility(View.VISIBLE);
            nav_email.setVisibility(View.VISIBLE);
        }

    }

    public void logout(){
        m_user = null;
        login_user = null;
        login_comp_user = null;
        myCompBusi_list = null;

        // ???????????? ??? updateUI??? isCompnay??? ?????? ??????
        updateUI(null, null);
        changeFragment(FRAGMENT_MAIN);
        Toast.makeText(getApplicationContext(), "???????????? ???????????????.", Toast.LENGTH_LONG).show();

        drawer.closeDrawers();
    }

    public static void changeFragment(int index){
        switch(index){
            case FRAGMENT_MAIN:
                manager.beginTransaction().replace(R.id.content_fragment_layout, mainActivity).commit();
                toolbar.setBackground(new ColorDrawable(0xffffffff));  // ???????????? ??? ??????
                break;
            case FRAGMENT_ADD_CONCENT:
                manager.beginTransaction().replace(R.id.content_fragment_layout, addConcentFragment).commit();
                toolbar.setBackground(new ColorDrawable(0xffffffff));
                break;
            case FRAGMENT_STATISTICS:
                manager.beginTransaction().replace(R.id.content_fragment_layout, statisticsFragment).commit();
                toolbar.setBackground(new ColorDrawable(0xffffffff));
                break;
            case FRAGMENT_DONATION_LIST:
                manager.beginTransaction().replace(R.id.content_fragment_layout, donationListFragment).commit();
                toolbar.setBackground(new ColorDrawable(0xffffffff));
                break;
            case FRAGMENT_VENTURE_LIST:
                manager.beginTransaction().replace(R.id.content_fragment_layout, ventureListFragment).commit();
                toolbar.setBackground(new ColorDrawable(0xffffffff));
                break;
            case FRAGMENT_DONATION:
                manager.beginTransaction().replace(R.id.content_fragment_layout, donationFragment).commit();
                toolbar.setBackground(new ColorDrawable(0xffffffff));
                break;
            case FRAGMENT_MYPAGE:
                manager.beginTransaction().replace(R.id.content_fragment_layout, mypageFragment).commit();
                toolbar.setBackground(new ColorDrawable(0xffffffff));
                break;
            case FRAGMENT_MYCOMPPAGE:
                manager.beginTransaction().replace(R.id.content_fragment_layout, myCompPageFragment).commit();
                toolbar.setBackground(new ColorDrawable(0xffffffff));
                break;
            case FRAGMENT_DETAIL:
                manager.beginTransaction().replace(R.id.content_fragment_layout, detailFragment).commit();
                toolbar.setBackground(new ColorDrawable(0xffffffff));
                break;
            case FRAGMENT_APPROVE:
                manager.beginTransaction().replace(R.id.content_fragment_layout, approveBusinessFragment).commit();
                toolbar.setBackground(new ColorDrawable(0xffffffff));
                break;
            case FRAGMENT_APPROVE_DETAIL:
                manager.beginTransaction().replace(R.id.content_fragment_layout, approveDetailFragment).commit();
                toolbar.setBackground(new ColorDrawable(0xffffffff));
                break;
        }
    }

    public static DonationActivity getDonationFragment(){
        return donationFragment;
    }
    public static DetailActivity getDetailFragment(){
        return detailFragment;
    }
    public static ApproveDetailActivity getApproveDtailFragment(){
        return approveDetailFragment;
    }

    //??????///
    //???????????? ???????????? AR???????????? ??????
    @Override
    protected void onNewIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        if(extras != null){

            if(extras.containsKey("StandyPowerMessage"))
            {
                String msg = extras.getString("StandyPowerMessage");

                Intent gointent = new Intent(NavActivity.this,ArActionActivity.class);
                startActivityForResult(gointent,3000);

            }

        }
    }
    // ?????? ??????
    // Firebase User data ?????? ??????
    public void FB_user_listening() {
        if (login_user != null) {
            m_reference.child("Members").child(login_user.getName()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    login_user = dataSnapshot.getValue(Member.class);
                    MainActivity.tx_watt.setText(login_user.getMileage() + "");
                    MainActivity.tx_save.setText(login_user.getPower() + "");
                }

                @Override

                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
