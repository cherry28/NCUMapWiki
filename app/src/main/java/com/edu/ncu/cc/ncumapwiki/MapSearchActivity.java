package com.edu.ncu.cc.ncumapwiki;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.multidex.MultiDex;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.wuman.android.auth.OAuthManager;

import java.util.ArrayList;

/**
 * Created by Cherry on 11/17/2016.
 */
public class MapSearchActivity extends AppCompatActivity {
    private Menu menu;
    MenuItem item;
    TabLayout mTabs;
    ViewPager mViewPager;
    Toolbar toolbar;
    OAuthManager oAuthManager;
    static String accessToken;
    static NavigationView navigationView;
    DrawerLayout drawer;
    FloatingActionButton fab;
    String url;
    StringRequest mStringRequest;
    RequestQueue mQueue;
    Context context;
    static TextView title;
    static TextView profileName;
    static TextView profileType;
    Tags tempGetTag;
    ListView mListView;
    ArrayList<Tags> tags = new ArrayList<>();
    ArrayList<String> tagsName = new ArrayList<>();
    ArrayList<String> tagsID = new ArrayList<>();
    Info info=new Info();
    ArrayAdapter<String> arrayAdapter;
    private CookieManager cookieManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapsearch);
        mQueue = Volley.newRequestQueue(this);
        context = this;
        initToolbar();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:{

            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initToolbar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu);
        setSupportActionBar(toolbar);
    }
    @Override
    public void onBackPressed(){
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
