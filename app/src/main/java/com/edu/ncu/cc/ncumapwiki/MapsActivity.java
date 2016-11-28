package com.edu.ncu.cc.ncumapwiki;
import android.app.SearchManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.api.client.auth.oauth2.BearerToken;
import com.google.api.client.auth.oauth2.ClientParametersAuthentication;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialStore;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.json.jackson.JacksonFactory;
import com.wuman.android.auth.AuthorizationFlow;
import com.wuman.android.auth.AuthorizationUIController;
import com.wuman.android.auth.DialogFragmentController;
import com.wuman.android.auth.OAuthManager;
import com.wuman.android.auth.oauth2.store.SharedPreferencesCredentialStore;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CancellationException;
import android.webkit.CookieManager;

public class MapsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,OnMapReadyCallback, OnMapClickListener {
    private GoogleMap mMap;
    private Menu menu;
    private static final LatLng defaultLocation = new LatLng(24.968297, 121.192151);
    private Toolbar toolbar;
    private OAuthManager oAuthManager;
    static String accessToken;
    static NavigationView navigationView;
    static TextView title;
    static TextView profileName;
    static TextView profileType;
    MenuItem itemChange;

    ArrayList<Places> places = new ArrayList<>();
    ArrayList<Location> Location = new ArrayList<>();
    ArrayList<String> placesID = new ArrayList<>();
    ArrayList<String> placesCName = new ArrayList<>();
    ArrayList<String> placesEName = new ArrayList<>();
    ArrayList<String> placesDescription= new ArrayList<>();
    ArrayList<String> placesLastAuthor= new ArrayList<>();
    ArrayList<Double> placesLat= new ArrayList<>();
    ArrayList<Double> placesLng= new ArrayList<>();
    Places tempPlaces;
    Places tempGetPlaces;
    Places tempAddPlaces;
    Places tempModifyPlaces;
    Places tempDeletePlaces;
    Location tempLocation;
    JSONObject placeI;
    Tags tempTags;

    Info info=new Info();
    ArrayAdapter<String> arrayAdapter;
    Spinner spinner;
    ArrayAdapter<String> tagAdapter;
    ArrayList<String> items = new ArrayList<>();
    ArrayList<Tags> tags = new ArrayList<>();
    ArrayList<String> TagsName = new ArrayList<>();
    ArrayList<String> TagsID = new ArrayList<>();
    Tags tempGetTag;
    Tags GetTag_selected;
    Tags GetTag;
    ListView mListView;
    int click_count=0;
    int placeCount=0;
    String url;
    StringRequest mStringRequest;
    RequestQueue mQueue;

    MenuItem menu_tags;
    private Context context;
    final int RQS_GooglePlayServices = 1;
    private Bitmap bitmap;
    private int PICK_IMAGE_REQUEST = 1;
    private ImageView imageView;
    private CookieManager cookieManager;
    BitmapDescriptor bitmapMarker;
    ArrayAdapter<String> listadapter;
    int listIndex;
    int tagIndex;
    int tempIndexlist;
    int default_index;
    int customized_index;
    String customized_tag;
    String customize_tag_name;
    String default_tags_text[];
    String[] default_tags = {
            "wheelchair_ramp", "disabled_car_parking", "disabled_motor_parking", "emergency",
            "aed", "restaurant", "sport_recreation", "administration", "research", "dormitory",
            "other", "toilet", "atm","bus_station", "parking_lot"
    };
    String[] default_tags_ch = {
            "無障礙坡道", "無障礙汽車位", "無障礙機車位", "緊急",
            "自動體外心臟去顫器", "餐廳", "休閒生活", "行政服務", "教學研究", "宿舍",
            "其他單位", "廁所", "提款機","公車站牌", "停車場"
    };
    String cName;
    String eName;
    String description;
    String latValue;
    String lngValue;
    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        initToolbar();
        mQueue = Volley.newRequestQueue(this);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList= (ListView) findViewById(R.id.right_drawer);
        /*String[] drawer_menu = this.getResources().getStringArray(R.array.drawer_menu);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.drawer_list_item, drawer_menu);
        mDrawerList.setAdapter(adapter);*/
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);

        toggle.syncState();
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
        title=(TextView)header.findViewById(R.id.title);
        profileName=(TextView)header.findViewById(R.id.profileName);
        profileType=(TextView)header.findViewById(R.id.profileType);
        MainActivity.title=(TextView)header.findViewById(R.id.title);
        MainActivity.profileName=(TextView)header.findViewById(R.id.profileName);
        MainActivity.profileType=(TextView)header.findViewById(R.id.profileType);
        if(getAccessToken()!=null || MainActivity.accessToken !=null){
            getInfo();
            Toast.makeText(getApplicationContext(), getString(R.string.login_state), Toast.LENGTH_SHORT).show();
            changeLogin(true);
        }
        else{
            changeLogin(false);
            //MainActivity.navigationView.getMenu().findItem(R.id.nav_favorite).setVisible(false);
        }
        cookieManager = CookieManager.getInstance();

        Intent intent = this.getIntent();
        default_index = intent.getIntExtra("default_tag",-1);
        default_tags_text = context.getResources().getStringArray(R.array.imgText);
        customize_tag_name=intent.getStringExtra("customize_tag_name");
        customized_tag = intent.getStringExtra("customized_tag");
        customized_index = intent.getIntExtra("customized_index",-1);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            if(getAccessToken()!=null || MainActivity.accessToken !=null){
                getInfo();
                Toast.makeText(getApplicationContext(), getString(R.string.login_state), Toast.LENGTH_SHORT).show();
            }
            else{
                title.setText("");
                profileName.setText("");
                profileType.setText("");
            }
        } else {
            super.onBackPressed();
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_menu, menu);
        menu_tags = menu.findItem(R.id.map_category);
        if(default_index>=0)
            menu_tags.setTitle(getResources().getString(R.string.tag_name)+"  "+default_tags_text[default_index]);
        else if(default_index==-1)
            menu_tags.setTitle(getResources().getString(R.string.tag_name)+"  "+customize_tag_name);
        this.menu = menu;
        return true;
    }
    private void changeLogin(boolean state){
        if(state){
            navigationView.getMenu().findItem(R.id.nav_favorite).setVisible(true);
            navigationView.getMenu().findItem(R.id.login_in).setTitle(getString(R.string.drawer_out));
            navigationView.getMenu().findItem(R.id.login_in).setIcon(R.mipmap.ic_lock_open_black_24dp);
            MainActivity.navigationView.getMenu().findItem(R.id.nav_favorite).setVisible(true);
            MainActivity.navigationView.getMenu().findItem(R.id.login_in).setTitle(getString(R.string.drawer_out));
            MainActivity.navigationView.getMenu().findItem(R.id.login_in).setIcon(R.mipmap.ic_lock_open_black_24dp);
        }
        else{
            navigationView.getMenu().findItem(R.id.nav_favorite).setVisible(false);
            navigationView.getMenu().findItem(R.id.login_in).setTitle(getString(R.string.drawer_in));
            navigationView.getMenu().findItem(R.id.login_in).setIcon(R.mipmap.ic_lock_black_24dp);
            MainActivity.navigationView.getMenu().findItem(R.id.nav_favorite).setVisible(false);
            MainActivity.navigationView.getMenu().findItem(R.id.login_in).setTitle(getString(R.string.drawer_in));
            MainActivity.navigationView.getMenu().findItem(R.id.login_in).setIcon(R.mipmap.ic_lock_black_24dp);
            title.setText("");
            profileName.setText("");
            profileType.setText("");
            MainActivity.title.setText("");
            MainActivity.profileName.setText("");
            MainActivity.profileType.setText("");
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.login_in: {
                itemChange = item;
                if (getAccessToken() == null && MapsActivity.accessToken == null) {
                    changeLogin(false);
                    final String CREDENTIAL_FILE_NAME = "credential.file";
                    final String AUTH_ENDPOINT_PATH = "http://140.115.3.188/oauth/oauth/authorize";
                    final String TOKEN_ENDPOINT_PATH = "http://140.115.3.188/oauth/oauth/token";
                    final String CLIENT_ID = "YzM0MGI3MjEtM2EwYS00MmVkLWIxOGYtZTBiZWUwMmUwODdl";
                    final String CLIENT_SECRET = "10620a5e2f616cf49ff9de17fb9aea02fe4d168c330febed3d7c3d6fc29c05ad1053c2a6fa9db9bb294321debaddbc0641edf8e87cc3e2583295a504e2dc1cdc";
                    final String CALL_BACK = "https://github.com/NCU-CC";
                    String scope = "user.Info.basic.read";

                    CredentialStore credentialStore = new SharedPreferencesCredentialStore(this, CREDENTIAL_FILE_NAME, new JacksonFactory());

                    AuthorizationFlow authorizationFlow = null;
                    AuthorizationUIController authorizationUIController = null;
                    AuthorizationFlow.Builder builder = new AuthorizationFlow.Builder(
                            BearerToken.authorizationHeaderAccessMethod(),
                            AndroidHttp.newCompatibleTransport(),
                            new JacksonFactory(),
                            new GenericUrl(TOKEN_ENDPOINT_PATH),
                            new ClientParametersAuthentication(CLIENT_ID, CLIENT_SECRET),
                            CLIENT_ID,
                            AUTH_ENDPOINT_PATH);
                    builder.setCredentialStore(credentialStore);
                    builder.setScopes(Arrays.asList(scope));
                    authorizationFlow = builder.build();

                    authorizationUIController = new DialogFragmentController(getSupportFragmentManager()) {
                        @Override
                        public boolean isJavascriptEnabledForWebView() {
                            return true;
                        }

                        @Override
                        public String getRedirectUri() throws IOException {
                            return CALL_BACK;
                        }
                    };
                    oAuthManager = new OAuthManager(authorizationFlow, authorizationUIController);
                    new AuthTask().execute();
                } else {
                    changeLogin(true);
                    View logoutView = View.inflate(getApplicationContext(), R.layout.logout_remind, null);
                    new android.app.AlertDialog.Builder(MapsActivity.this)
                            .setTitle(R.string.log_out)
                            .setView(logoutView)
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getApplicationContext(), getString(R.string.logout_cancel), Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    itemChange.setIcon(R.mipmap.ic_lock_white_24dp);
                                    cookieManager.setCookie("portal.ncu.edu.tw", "JSESSIONID=");
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.Logout), Toast.LENGTH_SHORT).show();
                                    accessToken = null;
                                    MainActivity.accessToken = null;
                                    Log.e(" ff ", accessToken + "  " + MapsActivity.accessToken);
                                    oAuthManager.deleteCredential("user", null, null);
                                    changeLogin(false);
                                }
                            }).show();
                }
                break;
            }
            case R.id.nav_favorite:{
                if(getAccessToken()==null && MapsActivity.accessToken ==null)
                    Toast.makeText(getApplicationContext(), getString(R.string.remind_login), Toast.LENGTH_SHORT).show();
                else {
                    final android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(MapsActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View convertView = inflater.inflate(R.layout.favorite_tag_item, null);
                    alertDialog.setView(convertView);
                    mListView = (ListView) convertView.findViewById(R.id.listView_tag);
                    alertDialog.show();
                    url = getResources().getString(R.string.url)+"tags?limit=1000&page=1&author="+info.getPersonID();
                    mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                if(arrayAdapter==null) {
                                    JSONObject GetTagsObject = new JSONObject(response);
                                    JSONArray objects = GetTagsObject.getJSONArray("tags");
                                    for (int i = 0; i < objects.length(); i++) {
                                        JSONObject currentParsingTags = objects.getJSONObject(i);
                                        tempGetTag = new Tags(currentParsingTags.getString("id"), currentParsingTags.getString("name"));
                                        tags.add(tempGetTag);
                                        tags.get(i);
                                        TagsName.add(tempGetTag.getTagsName());
                                        TagsID.add(tempGetTag.getTagsId());
                                    }
                                }

                                arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, TagsName);
                                mListView.setAdapter(arrayAdapter);
                                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view2, int position, long id) {
                                        String customize_tag_name = mListView.getAdapter().getItem(position).toString();
                                        menu_tags.setTitle(getResources().getString(R.string.tag_name)+"  "+TagsName.get(position));
                                        Intent intent = new Intent();
                                        intent.setClass(view2.getContext(), MapsActivity.class);
                                        intent.putExtra("customize_tag_name",customize_tag_name);
                                        intent.putExtra("customized_tag", TagsID.get(position));
                                        intent.putExtra("customized_index",position);
                                        startActivity(intent);
                                    }
                                });
                            } catch (JSONException e) {
                                Log.e("debug", e.getLocalizedMessage());
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("debug", error.getMessage());
                            error.printStackTrace();
                        }
                    }) {
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> headers = new HashMap<>();
                            headers.put("X-NCU-API-TOKEN", getResources().getString(R.string.apiToken));
                            return headers;
                        }
                    };
                    mQueue.add(mStringRequest);
                }
                break;
            }
            case R.id.nav_map:{
                String uri = "http://www.ncu.edu.tw/assets/thumbs/pic/NCU_Campus_Map_(map-JPG).jpg";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(uri));
                startActivity(i);
                break;
            }
            case R.id.nav_about:{
                LayoutInflater inflater = LayoutInflater.from(MapsActivity.this);
                final View v = inflater.inflate(R.layout.about, null);
                new android.support.v7.app.AlertDialog.Builder(MapsActivity.this)
                        .setTitle(getResources().getString(R.string.drawer4))
                        .setView(v)
                        .show();
                break;
            }
            case R.id.nav_opinion:{
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
                sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { "mobile@cc.ncu.edu.tw" });
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.emailTitle));
                sendIntent.setType("plain/text");
                startActivity(sendIntent);
                break;
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private class AuthTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            boolean authSuccess = false;
            try {
                Credential authResult = oAuthManager.authorizeExplicitly("user",null,null).getResult();
                if (authResult.getExpiresInSeconds() <= 60)
                    authResult.refreshToken();
                accessToken = authResult.getAccessToken();
                getInfo();
                authSuccess=true;
            } catch (IOException e) {
                e.printStackTrace();
                //Toast.makeText(getApplicationContext(),getResources().getString(R.string.login_fail), Toast.LENGTH_SHORT).show();
            } catch (CancellationException e){
                MainActivity.accessToken =null;
                accessToken =null;
            }
            return authSuccess;
        }
        @Override
        protected void onPostExecute(Boolean type) {
            if(type)
                changeLogin(type);
        }

    }
    TextView image_edit_ch;
    TextView image_edit_en;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15));
        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        getPlaces();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                currentMarker = marker;
                position =  currentMarker.getPosition();
                if(default_index>=0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setPositiveButton(getResources().getString(R.string.confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).setNegativeButton(getResources().getString(R.string.download), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    final AlertDialog dialog = builder.create();
                    LayoutInflater inflater = getLayoutInflater();
                    View dialogImage = inflater.inflate(R.layout.default_image, null);
                    image_edit_ch = (TextView) dialogImage.findViewById(R.id.image_ch);
                    image_edit_en = (TextView) dialogImage.findViewById(R.id.image_en);
                    getPlaceID(-1);
                    dialog.setView(dialogImage);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.show();
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface d) {
                            ImageView image = (ImageView) dialog.findViewById(R.id.goProDialogImage);
                            Bitmap icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ncu_9);
                            float imageWidthInPX = (float)image.getWidth();

                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(Math.round(imageWidthInPX),
                                    Math.round(imageWidthInPX * (float)icon.getHeight() / (float)icon.getWidth()));
                            image.setLayoutParams(layoutParams);
                        }
                    });
                }
                else if(default_index==-1){
                    if(getAccessToken()==null && MainActivity.accessToken ==null) {
                        Toast.makeText(getApplicationContext(), getString(R.string.remind_login_modify_location), Toast.LENGTH_SHORT).show();
                    }
                    else
                        showAlertDialogForPoint();
                }
            }
        });
        if(default_index==-1)
            mMap.setOnMapClickListener(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());

        if (resultCode == ConnectionResult.SUCCESS) {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.loading_map), Toast.LENGTH_LONG).show();
        } else {
            GooglePlayServicesUtil.getErrorDialog(resultCode, this, RQS_GooglePlayServices);
        }
    }
    Marker currentMarker;
    LatLng position;
    LatLng AddPoint;
    @Override
    public void onMapClick(LatLng point) {
        if(getAccessToken()==null && MainActivity.accessToken ==null) {
            Toast.makeText(this, getString(R.string.remind_login_add_location), Toast.LENGTH_SHORT).show();
        }
        else{
            AddPoint = point;
            map_add_location(AddPoint);
        }
    }
    private void showAlertDialogForPoint() {
        final String names[]={getResources().getString(R.string.map_information),
                getResources().getString(R.string.modify_location),getResources().getString(R.string.delete_location)};
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MapsActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.map_item, null);
        alertDialog.setView(convertView);
        final ListView listView = (ListView) convertView.findViewById(R.id.listView1);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
        listView.setAdapter(adapter);
        alertDialog.show();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                int i;
                switch (position) {
                    case 0: {
                        i = position;
                        getPlaceID(i);
                        break;
                    }
                    case 1:
                        i = position;
                        getPlaceID(i);
                        break;
                    case 2: {
                        i = position;
                        getPlaceID(i);
                        break;
                    }
                }
            }
        });
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return true;
            }
        });
        toolbar.inflateMenu(R.menu.menu);
        setSupportActionBar(toolbar);
    }
    private void getPlaces() {
        placeCount=0;
        bitmapMarker = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
        if(default_index>=0){
            String default_type= default_tags[default_index];
            url = getResources().getString(R.string.url)+"places?limit=1000&page=1&type="+default_type;
        }
        else if(default_index==-1){
            url = getResources().getString(R.string.url)+"places?limit=1000&page=1&type=WIKI&tagID="+customized_tag;
        }
        mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                LatLng ncuLocation;
                try {
                    JSONObject GetResponseObject = new JSONObject(response);
                    if(default_index==-1)
                        placeCount= Integer.valueOf(GetResponseObject.getString("count"));
                    Log.e("count",placeCount+"");
                    final JSONArray objects = GetResponseObject.getJSONArray("places");
                    listIndex=objects.length();
                    for (int i = 0; i < objects.length(); i++) {
                        placeI = objects.getJSONObject(i);
                        tempGetPlaces = new Places(placeI.getString("id"),placeI.getString("chineseName"),
                                placeI.getString("englishName"),placeI.getString("type"),
                                placeI.getString("description"),placeI.getString("lastAuthor"));
                        JSONObject locationI = placeI.getJSONObject("location");
                        tempLocation =new Location(locationI.getDouble("lat"),locationI.getDouble("lng"));
                        places.add(tempGetPlaces);
                        places.get(i);
                        placesID.add(placeI.getString("id"));
                        placesCName.add(placeI.getString("chineseName"));
                        placesEName.add(placeI.getString("englishName"));
                        placesDescription.add(placeI.getString("description"));
                        placesLastAuthor.add(placeI.getString("lastAuthor"));
                        placesLat.add(locationI.getDouble("lat"));
                        placesLng.add(locationI.getDouble("lng"));
                        ncuLocation = new LatLng(locationI.getDouble("lat"),locationI.getDouble("lng"));
                        mMap.addMarker(new MarkerOptions()
                                .position(ncuLocation)
                                .title(tempGetPlaces.getChineseName())
                                .snippet(tempGetPlaces.getEnglishName())
                                .icon(bitmapMarker));
                    }
                    listadapter = new ArrayAdapter<String>(context, R.layout.drawer_list_item, placesCName);
                    mDrawerList.setAdapter(listadapter);
                    mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            for(int i=0;i<objects.length();i++){
                                if(placesCName.get(i).equals(placesCName.get(position))){
                                    tempIndexlist=i;
                                    break;
                                }
                            }
                            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(placesLat.get(tempIndexlist), placesLng.get(tempIndexlist)), 15));
                            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                                @Override
                                public void onInfoWindowClick(Marker marker) {

                                }
                            });
                        }
                    });
                } catch (JSONException e) {
                    Log.e("debug", e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("debug", error.getMessage());
                error.printStackTrace();
            }
        }) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("X-NCU-API-TOKEN", getResources().getString(R.string.apiToken));
                return headers;
            }
        };
        mQueue.add(mStringRequest);
    }

    View item;
    EditText edit_ch;
    EditText edit_en;
    EditText edit_description;
    private void getPlaceID(final int PlaceChoice) {
        item = View.inflate(MapsActivity.this, R.layout.map_modify_location, null);
        edit_ch = (EditText) item.findViewById(R.id.location_ch);
        edit_en = (EditText) item.findViewById(R.id.location_en);
        edit_description = (EditText) item.findViewById(R.id.location_description);
        latValue= new BigDecimal(String.valueOf(position.latitude)).setScale(6, BigDecimal.ROUND_HALF_UP).toString();
        lngValue= new BigDecimal(String.valueOf(position.longitude)).setScale(6, BigDecimal.ROUND_HALF_UP).toString();
        if(PlaceChoice==-1)
            url=getResources().getString(R.string.url)+"places?startLat="+latValue+"&endLat="+latValue+
                    "&startLng="+lngValue+"&endLng="+lngValue;
        else
            url = getResources().getString(R.string.url)+"places?type=WIKI&startLat="+latValue+"&endLat="+latValue+
                "&startLng="+lngValue+"&endLng="+lngValue+"&tagID="+customized_tag;
        Log.e("de",url);
        mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject GetResponseObject = new JSONObject(response);
                    JSONArray objects = GetResponseObject.getJSONArray("places");
                    JSONObject onePlace = objects.getJSONObject(0);
                    Log.e("debug",onePlace.getString("id")+":");
                    for (int k=0;k<placeCount;k++){
                        Log.e("test",onePlace.getString("id")+"  "+placesID.get(k));
                        if(onePlace.getString("id").equals(placesID.get(k))){
                            click_count=k;
                            break;
                        }
                    }
                    currentMarker.setTitle(onePlace.getString("chineseName"));
                    currentMarker.setSnippet(onePlace.getString("englishName"));
                    if(PlaceChoice==-1){
                        image_edit_ch.setText(onePlace.getString("chineseName"));
                        image_edit_en.setText(onePlace.getString("englishName"));
                    }
                    else if(PlaceChoice==0){
                        cName=getResources().getString(R.string.location_chinese_name)+ onePlace.getString("chineseName");
                        eName=getResources().getString(R.string.location_english_name)+ onePlace.getString("englishName");
                        description=getResources().getString(R.string.location_description)+onePlace.getString("description");
                        new AlertDialog.Builder(MapsActivity.this)
                                .setTitle(R.string.diolog_information)
                                .setMessage(cName+ "\n"+ eName + "\n"+description)
                                .show();
                    }
                    else if(PlaceChoice==1){
                        edit_ch.setText(onePlace.getString("chineseName"));
                        edit_en.setText(onePlace.getString("englishName"));
                        edit_description.setText(onePlace.getString("description"));
                        map_modify_location(item);
                        Log.e("de",onePlace.getString("chineseName"));
                    }
                    else if(PlaceChoice==2){
                        map_delete_location();
                    }
                } catch (JSONException e) {
                    Log.e("debug", e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("X-NCU-API-TOKEN", getResources().getString(R.string.apiToken));
                return headers;
            }
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                map.put("startLat", latValue);
                map.put("endLat", latValue);
                map.put("startLng", lngValue);
                map.put("endLng", lngValue);
                return map;
            }
        };
        mQueue.add(mStringRequest);
    }
    public void map_add_location(final LatLng point) {
        final View add_view = View.inflate(MapsActivity.this, R.layout.map_add_location, null);
        spinner = (Spinner) add_view.findViewById(R.id.tag_spinner);
        url = getResources().getString(R.string.url)+"tags?limit=1000&page=1";
        mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject GetResponseObject = new JSONObject(response);
                    JSONArray objects = GetResponseObject.getJSONArray("tags");
                    for (int i = 0; i < objects.length(); i++) {
                        if(tagAdapter==null){
                            JSONObject currentParsingTags = objects.getJSONObject(i);
                            GetTag= new Tags(currentParsingTags.getString("id"),currentParsingTags.getString("name"));
                            tags.add(GetTag);
                            items.add(GetTag.getTagsName());
                            Log.e("eeee",customize_tag_name+"   "+GetTag.getTagsName());
                            if(customize_tag_name.equals(GetTag.getTagsName())){
                                tagIndex=i;
                                break;
                            }
                        }
                    }
                    tagAdapter = new ArrayAdapter<>(getApplication(), android.R.layout.simple_spinner_item,items);
                    spinner.setAdapter(tagAdapter);
                    spinner.setSelection(tagIndex);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                        @Override
                        public void onItemSelected(AdapterView<?> arg0, View arg1,int position, long arg3) {
                            try{
                                Log.e("uu",items.get(position));
                                url = getResources().getString(R.string.url)+"tags?name="+ URLEncoder.encode(items.get(position),"UTF-8");
                                mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.e("TAG", response);
                                        try {
                                            JSONObject GetResponseObject = new JSONObject(response);
                                            JSONArray objects = GetResponseObject.getJSONArray("tags");
                                            JSONObject currentParsingTags = objects.getJSONObject(0);
                                            GetTag_selected= new Tags(currentParsingTags.getString("id"),currentParsingTags.getString("name"));
                                            tags.add(GetTag_selected);
                                            tags.get(0);
                                            Log.e("Parsed data is", ":" + GetTag_selected.getTagsId() + "," +GetTag_selected.getTagsName());
                                        } catch (JSONException e) {
                                            Log.e("debug", e.getLocalizedMessage());
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.e("TAG", error.getMessage(), error);
                                    }
                                }) {
                                    public Map<String, String> getHeaders() throws AuthFailureError {
                                        Map<String, String> headers = new HashMap<>();
                                        headers.put("X-NCU-API-TOKEN", getResources().getString(R.string.apiToken));
                                        return headers;
                                    }
                                };
                                mQueue.add(mStringRequest);
                            }
                            catch (Exception ex) {
                                System.out.println("URL Error");
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {
                        }
                    });
                } catch (JSONException e) {
                    Log.e("debug", e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        }) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("X-NCU-API-TOKEN", getResources().getString(R.string.apiToken));
                return headers;
            }
        };
        mQueue.add(mStringRequest);

        new AlertDialog.Builder(MapsActivity.this)
                .setTitle(R.string.add_location)
                .setView(add_view)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), getString(R.string.location_message_add_cancel), Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final DialogInterface d_add = dialog;
                        url=getResources().getString(R.string.url)+"place";
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("Success", response);
                                try {
                                    JSONObject placeAdd = new JSONObject(response);
                                    Log.e("test",response);
                                    tempPlaces = new Places(placeAdd.getString("id"),placeAdd.getString("chineseName"),
                                            placeAdd.getString("englishName"),placeAdd.getString("type"),
                                            placeAdd.getString("description"), placeAdd.getString("lastAuthor"));
                                    places.add(tempPlaces);
                                    placesID.add(placeAdd.getString("id"));
                                    placesCName.add(placeAdd.getString("chineseName"));
                                    placesEName.add(placeAdd.getString("englishName"));
                                    placesDescription.add(placeAdd.getString("description"));
                                    placesLastAuthor.add(placeAdd.getString("lastAuthor"));
                                    url=getResources().getString(R.string.url)+"place/"+placeAdd.getString("id")+"/tag/"+GetTag_selected.getTagsId();
                                    Log.e("test0",url);
                                    listadapter.notifyDataSetChanged();
                                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Log.d("Success", response);
                                            try {
                                                placeI = new JSONObject(response);
                                                tempAddPlaces = new Places(placeI.getString("id"),placeI.getString("chineseName"),
                                                        placeI.getString("englishName"),placeI.getString("type"),
                                                        placeI.getString("description"), placeI.getString("lastAuthor"));
                                                if(customized_tag.equals(GetTag_selected.getTagsId())){
                                                    mMap.addMarker(new MarkerOptions()
                                                            .position(point)
                                                            .title(placeI.getString("chineseName"))
                                                            .snippet(placeI.getString("englishName"))
                                                            .icon(BitmapDescriptorFactory.defaultMarker()));
                                                }
                                                placeCount++;
                                                Toast.makeText(getApplicationContext(), getString(R.string.location_message_add)+": "+placeI.getString("chineseName"), Toast.LENGTH_SHORT).show();
                                            } catch (JSONException e) {
                                                Log.e("debug", e.getLocalizedMessage());
                                                e.printStackTrace();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                        public void onErrorResponse(VolleyError error) {
                                            Log.e("Error occur", error.getMessage(), error);
                                        }
                                    }) {
                                        public Map<String, String> getHeaders() throws AuthFailureError {
                                            Map<String, String> map = new HashMap<>();
                                            if(getAccessToken()!=null)
                                                map.put("Authorization", "Bearer " + getAccessToken());
                                            else if(MainActivity.accessToken !=null)
                                                map.put("Authorization", "Bearer " + MainActivity.accessToken);
                                            Log.e("parse",getAccessToken());
                                            return map;
                                        }
                                        public Map<String, String> getParams() throws AuthFailureError {
                                            Map<String, String> map = new HashMap<>();
                                            Log.e("root",tempPlaces.getId());
                                            map.put("place_id", tempPlaces.getId()+"   "+GetTag_selected.getTagsId());
                                            map.put("tag_id",GetTag_selected.getTagsId());
                                            return map;
                                        }
                                    };
                                    mQueue.add(stringRequest);
                                } catch (JSONException e) {
                                    Log.e("debug", e.getLocalizedMessage());
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Error occur", error.getMessage(), error);
                            }
                        }) {
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> map = new HashMap<>();
                                if(getAccessToken()!=null)
                                    map.put("Authorization", "Bearer " + getAccessToken());
                                else if(MainActivity.accessToken !=null)
                                    map.put("Authorization", "Bearer " + MainActivity.accessToken);
                                return map;
                            }

                            String lat = String.valueOf(point.latitude);
                            String lng = String.valueOf(point.longitude);

                            public Map<String, String> getParams() throws AuthFailureError {
                                String chineseName = ((EditText) ((AlertDialog) d_add).findViewById(R.id.location_ch)).getText().toString();
                                String englishName = ((EditText) ((AlertDialog) d_add).findViewById(R.id.location_en)).getText().toString();
                                String description = ((EditText) ((AlertDialog) d_add).findViewById(R.id.location_description)).getText().toString();
                                //String photo_url = ((EditText) ((AlertDialog) d_add).findViewById(R.id.photo_url)).getText().toString();
                                Map<String, String> map = new HashMap<>();
                                map.put("chineseName", chineseName);
                                map.put("englishName", englishName);
                                map.put("latitude", lat);
                                map.put("longitude", lng);
                                map.put("description", description);
                                //map.put("pictureURL", photo_url);
                                Log.e("Parsed data is", " :" + chineseName);
                                return map;
                            }
                        };
                        mQueue.add(stringRequest);
                    }
                }).show();
    }
    public void map_modify_location(View modify_view) {
        currentMarker.hideInfoWindow();
        spinner = (Spinner) modify_view.findViewById(R.id.tag_spinner);
        url = getResources().getString(R.string.url)+"tags?limit=1000&page=1";
        mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("TAG", response);
                try {
                    if(tagAdapter==null){
                        JSONObject GetResponseObject = new JSONObject(response);
                        JSONArray objects = GetResponseObject.getJSONArray("tags");
                        for (int i = 0; i < objects.length(); i++) {
                            JSONObject currentParsingTags = objects.getJSONObject(i);
                            GetTag= new Tags(currentParsingTags.getString("id"),currentParsingTags.getString("name"));
                            tags.add(GetTag);
                            tags.get(i);
                            Log.e("Parsed data is", ":" + GetTag.getTagsId() + "," +GetTag.getTagsName());
                            items.add(GetTag.getTagsName());
                            if(customize_tag_name==GetTag.getTagsName())
                                tagIndex=i;
                        }
                    }
                    tagAdapter = new ArrayAdapter<>(getApplication(), android.R.layout.simple_spinner_item,items);
                    spinner.setAdapter(tagAdapter);
                    spinner.setSelection(tagIndex);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                        @Override
                        public void onItemSelected(AdapterView<?> arg0, View arg1,int position, long arg3) {
                            try{
                                url = getResources().getString(R.string.url)+"tags?name="+ URLEncoder.encode(items.get(position),"UTF-8");
                                mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.e("TAG", response);
                                        try {
                                            JSONObject GetResponseObject = new JSONObject(response);
                                            JSONArray objects = GetResponseObject.getJSONArray("tags");
                                            JSONObject currentParsingTags = objects.getJSONObject(0);
                                            GetTag_selected= new Tags(currentParsingTags.getString("id"),currentParsingTags.getString("name"));
                                            tags.add(GetTag_selected);
                                            tags.get(0);
                                            Log.e("Parsed data is", ":" + GetTag_selected.getTagsId() + "," +GetTag_selected.getTagsName());
                                        } catch (JSONException e) {
                                            Log.e("debug", e.getLocalizedMessage());
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.e("TAG", error.getMessage(), error);
                                    }
                                }) {
                                    public Map<String, String> getHeaders() throws AuthFailureError {
                                        Map<String, String> headers = new HashMap<>();
                                        headers.put("X-NCU-API-TOKEN", getResources().getString(R.string.apiToken));
                                        return headers;
                                    }
                                };
                                mQueue.add(mStringRequest);
                            }
                            catch (Exception ex) {
                                System.out.println("URL Error");
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {
                        }
                    });
                } catch (JSONException e) {
                    Log.e("debug", e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
                Toast.makeText(context, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("X-NCU-API-TOKEN", getResources().getString(R.string.apiToken));
                return headers;
            }
        };
        mQueue.add(mStringRequest);

        new AlertDialog.Builder(MapsActivity.this)
            .setTitle(R.string.modify_location)
            .setView(modify_view)
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), getString(R.string.location_message_add_cancel), Toast.LENGTH_SHORT).show();
                }
            })
            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final DialogInterface d_modify = dialog;
                final int selectedId = Integer.parseInt(placesID.get(click_count));
                url = getResources().getString(R.string.url)+"place/" + selectedId;
                StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Success", response);
                        try {
                            placeI = new JSONObject(response);
                            tempModifyPlaces = new Places(placeI.getString("id"), placeI.getString("chineseName"),
                                    placeI.getString("englishName"), placeI.getString("type"),
                                    placeI.getString("description"), placeI.getString("lastAuthor"));

                            for(int i=0;i<listIndex;i++){
                                if(placeI.getString("id").equals(placesCName.get(i))){
                                    tempIndexlist=i;
                                    break;
                                }
                            }
                            placesCName.set(tempIndexlist,placeI.getString("chineseName"));
                            listadapter.notifyDataSetChanged();
                            if(!customized_tag.equals(GetTag_selected.getTagsId())){
                                url=getResources().getString(R.string.url)+"place/"+placeI.getString("id")+"/tag/"+customized_tag;
                                StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.d("Success", response);
                                        try {
                                            placeI = new JSONObject(response);
                                            currentMarker.setVisible(false);
                                        } catch (JSONException e) {
                                            Log.e("debug", e.getLocalizedMessage());
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    public void onErrorResponse(VolleyError error) {
                                        Log.e("Error occur", error.getMessage(), error);
                                    }
                                }) {
                                    public Map<String, String> getHeaders() throws AuthFailureError {
                                        Map<String, String> map = new HashMap<>();
                                        if(getAccessToken()!=null)
                                            map.put("Authorization", "Bearer " + getAccessToken());
                                        else if(MainActivity.accessToken !=null)
                                            map.put("Authorization", "Bearer " + MainActivity.accessToken);
                                        return map;
                                    }
                                    public Map<String, String> getParams() throws AuthFailureError {
                                        Map<String, String> map = new HashMap<>();
                                        map.put("place_id", tempModifyPlaces.getId());
                                        map.put("tag_id",customized_tag);
                                        return map;
                                    }
                                };
                                mQueue.add(stringRequest);

                                url=getResources().getString(R.string.url)+"place/"+placeI.getString("id")+"/tag/"+GetTag_selected.getTagsId();
                                stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.d("Success", response);
                                        try {
                                            placeI = new JSONObject(response);
                                            tempModifyPlaces = new Places(placeI.getString("id"),placeI.getString("chineseName"),
                                                    placeI.getString("englishName"), placeI.getString("type"),
                                                    placeI.getString("description"), placeI.getString("lastAuthor"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {
                                    public void onErrorResponse(VolleyError error) {
                                        error.printStackTrace();
                                    }
                                }) {
                                    public Map<String, String> getHeaders() throws AuthFailureError {
                                        Map<String, String> map = new HashMap<>();
                                        if(getAccessToken()!=null)
                                            map.put("Authorization", "Bearer " + getAccessToken());
                                        else if(MainActivity.accessToken !=null)
                                            map.put("Authorization", "Bearer " + MainActivity.accessToken);
                                        return map;
                                    }
                                    public Map<String, String> getParams() throws AuthFailureError {
                                        Map<String, String> map = new HashMap<>();
                                        map.put("place_id", tempModifyPlaces.getId());
                                        map.put("tag_id",GetTag_selected.getTagsId());
                                        return map;
                                    }
                                };
                                mQueue.add(stringRequest);
                            }
                            currentMarker.setTitle(placeI.getString("chineseName"));
                            currentMarker.setSnippet(placeI.getString("englishName"));
                            Toast.makeText(getApplicationContext(), getString(R.string.location_message_modify)+": "+placeI.getString("chineseName"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), getString(R.string.modify_location_fail), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), getString(R.string.modify_location_fail), Toast.LENGTH_SHORT).show();
                    }
                }) {
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> map = new HashMap<>();
                        if(getAccessToken()!=null)
                            map.put("Authorization", "Bearer " + getAccessToken());
                        else if(MainActivity.accessToken !=null)
                            map.put("Authorization", "Bearer " + MainActivity.accessToken);
                        return map;
                    }
                    String lat = String.valueOf(position.latitude);
                    String lng = String.valueOf(position.longitude);
                    public Map<String, String> getParams() throws AuthFailureError {
                        String chineseName = ((EditText) ((AlertDialog) d_modify).findViewById(R.id.location_ch)).getText().toString();
                        String englishName = ((EditText) ((AlertDialog) d_modify).findViewById(R.id.location_en)).getText().toString();
                        String description = ((EditText) ((AlertDialog) d_modify).findViewById(R.id.location_description)).getText().toString();
                        //String photo_url = ((EditText) ((AlertDialog) d_modify).findViewById(R.id.photo_url)).getText().toString();
                        Map<String, String> map = new HashMap<>();
                        map.put("chineseName", chineseName);
                        map.put("englishName", englishName);
                        map.put("latitude", lat);
                        map.put("longitude", lng);
                        map.put("description", description);
                        //map.put("pictureURL", photo_url);
                        return map;
                    }
                };
                mQueue.add(stringRequest);
            }
        }).show();
    }

    public void map_delete_location() {
        new AlertDialog.Builder(MapsActivity.this)
            .setTitle(R.string.delete_location)
            .setMessage(R.string.delete_location_message)
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), getString(R.string.location_message_delete_cancel), Toast.LENGTH_SHORT).show();
                }
            })
        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final int selectedId = Integer.parseInt(placesID.get(click_count));
                url = getResources().getString(R.string.url)+"place/" + selectedId;
                StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            placeI = new JSONObject(response);
                            tempDeletePlaces = new Places(placeI.getString("id"), placeI.getString("chineseName"),
                                    placeI.getString("englishName"),placeI.getString("type"),
                                    placeI.getString("description"), placeI.getString("lastAuthor"));
                            placesCName.remove(placeI.getString("chineseName"));
                            listadapter.notifyDataSetChanged();
                            currentMarker.setVisible(false);
                            currentMarker.remove();
                            placeCount--;
                            Toast.makeText(getApplicationContext(), getString(R.string.location_message_delete)+": "+placeI.getString("chineseName"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), getString(R.string.delete_location_fail), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        currentMarker.setVisible(true);
                        Toast.makeText(getApplicationContext(), getString(R.string.delete_location_fail), Toast.LENGTH_SHORT).show();
                    }
                }) {
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> map = new HashMap<>();
                        if(getAccessToken()!=null)
                            map.put("Authorization", "Bearer " + getAccessToken());
                        else if(MainActivity.accessToken !=null)
                            map.put("Authorization", "Bearer " + MainActivity.accessToken);
                        return map;
                    }
                    public Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<>();
                        map.put("id",tempDeletePlaces.getId());
                        return map;
                    }
                };
                mQueue.add(stringRequest);
            }
        }).show();
    }
    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void getInfo(){
        url = "http://140.115.3.188/personnel/v1/info";
        mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject InfoObject = new JSONObject(response);
                    info.setPersonID(InfoObject.getString("id"));
                    info.setPersonNumber(InfoObject.getString("number"));
                    info.setPersonName(InfoObject.getString("name"));
                    info.setPersonType(InfoObject.getString("type"));
                    info.setPersonUnit(InfoObject.getString("unit"));
                    info.setPersonGroup(InfoObject.getString("group"));
                    title.setText(getResources().getString(R.string.user_login));
                    profileName.setText(info.getPersonName()+"  "+info.getPersonType());
                    profileType.setText(info.getPersonType());
                    MainActivity.title.setText(getResources().getString(R.string.user_login));
                    MainActivity.profileName.setText(info.getPersonName()+"  "+info.getPersonType());
                    MainActivity.profileType.setText(info.getPersonType());
                } catch (JSONException e) {
                    Log.e("debug", e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.getMessage();
                error.printStackTrace();
            }
        }) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> map = new HashMap<>();
                if(getAccessToken()!=null)
                    map.put("Authorization", "Bearer " + getAccessToken());
                else if(MapsActivity.accessToken !=null)
                    map.put("Authorization", "Bearer " + MapsActivity.accessToken);
                return map;
            }
        };
        mQueue.add(mStringRequest);
    }
    public String getAccessToken() {
        return accessToken;
    }
}
