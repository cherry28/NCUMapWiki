package com.edu.ncu.cc.ncumapwiki;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.app.SearchManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.MenuInflater;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.support.multidex.MultiDex;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import java.util.concurrent.CancellationException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import android.webkit.CookieManager;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
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
        setContentView(R.layout.activity_main);
        initToolbar();
        initViewPager();
        initTelephony();
        mQueue = Volley.newRequestQueue(this);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
        MapsActivity.title=(TextView)header.findViewById(R.id.title);
        MapsActivity.profileName=(TextView)header.findViewById(R.id.profileName);
        MapsActivity.profileType=(TextView)header.findViewById(R.id.profileType);
        if(getAccessToken()!=null || MapsActivity.accessToken !=null){
            getInfo();
            Toast.makeText(getApplicationContext(), getString(R.string.login_state), Toast.LENGTH_SHORT).show();
            changeLogin(true);
        }
        else{
            changeLogin(false);
        }
        cookieManager = CookieManager.getInstance();
    }
    @Override
    protected void onStart() {
        super.onStart();
        //check network status
        NetworkInfo networkInfo = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.ic_launcher);
            builder.setTitle(R.string.network_unreachable);
            builder.setMessage(R.string.open_network_message);
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setPositiveButton(R.string.action_settings, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(Settings.ACTION_SETTINGS));
                }
            });
            builder.show();
        }
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
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
            // Assumes current activity is the searchable activity
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String query) {
                //doSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        });
        return true;
    }
    private void changeLogin(boolean state){
        if(state){
            navigationView.getMenu().findItem(R.id.nav_favorite).setVisible(true);
            navigationView.getMenu().findItem(R.id.login_in).setTitle(getString(R.string.drawer_out));
            navigationView.getMenu().findItem(R.id.login_in).setIcon(R.mipmap.ic_lock_open_black_24dp);
            //MapsActivity.navigationView.getMenu().findItem(R.id.nav_favorite).setVisible(true);
            //MapsActivity.navigationView.getMenu().findItem(R.id.login_in).setTitle(getString(R.string.drawer_out));
            //MapsActivity.navigationView.getMenu().findItem(R.id.login_in).setIcon(R.mipmap.ic_lock_open_black_24dp);
        }
        else{
            navigationView.getMenu().findItem(R.id.nav_favorite).setVisible(false);
            navigationView.getMenu().findItem(R.id.login_in).setTitle(getString(R.string.drawer_in));
            navigationView.getMenu().findItem(R.id.login_in).setIcon(R.mipmap.ic_lock_black_24dp);
            //MapsActivity.navigationView.getMenu().findItem(R.id.nav_favorite).setVisible(false);
            //MapsActivity.navigationView.getMenu().findItem(R.id.login_in).setTitle(getString(R.string.drawer_in));
            //MapsActivity.navigationView.getMenu().findItem(R.id.login_in).setIcon(R.mipmap.ic_lock_black_24dp);
            title.setText("");
            profileName.setText("");
            profileType.setText("");
            //MapsActivity.title.setText("1 ");
            //MapsActivity.profileName.setText(" ");
            //MapsActivity.profileType.setText(" ");
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.search:{
                Intent intentSearch = new Intent();
                intentSearch.setClass(this, MapSearchActivity.class);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.login_in:{
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
                }
                else {
                    changeLogin(true);
                    View logoutView = View.inflate(getApplicationContext(), R.layout.logout_remind, null);
                    new android.app.AlertDialog.Builder(MainActivity.this)
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
                                    changeLogin(false);
                                    cookieManager.setCookie("portal.ncu.edu.tw", "JSESSIONID=");
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.Logout), Toast.LENGTH_SHORT).show();
                                    accessToken = null;
                                    MapsActivity.accessToken = null;
                                    oAuthManager.deleteCredential("user", null, null);
                                }
                            }).show();
                }
                break;
            }
            case R.id.nav_favorite:{
                if(getAccessToken()==null && MapsActivity.accessToken ==null)
                    Toast.makeText(getApplicationContext(), getString(R.string.remind_login), Toast.LENGTH_SHORT).show();
                else {
                    final android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(MainActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View convertView = inflater.inflate(R.layout.favorite_tag_item, null);
                    alertDialog.setView(convertView);
                    mListView = (ListView) convertView.findViewById(R.id.listView_tag);

                    alertDialog.show();
                    url = getResources().getString(R.string.url)+"tags?limit=1000&page=1&author="+info.getPersonID();
                    mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.e("TAG", response);
                            try {
                                if(arrayAdapter==null){
                                    JSONObject GetTagsObject = new JSONObject(response);
                                    JSONArray objects = GetTagsObject.getJSONArray("tags");
                                    for (int i = 0; i < objects.length(); i++) {
                                        JSONObject currentParsingTags = objects.getJSONObject(i);
                                        tempGetTag = new Tags(currentParsingTags.getString("id"), currentParsingTags.getString("name"));
                                        tags.add(tempGetTag);
                                        tags.get(i);
                                        Log.e("Parsed data is", ":" + tempGetTag.getTagsId() + "," + tempGetTag.getTagsName());
                                        tagsName.add(tempGetTag.getTagsName());
                                        tagsID.add(tempGetTag.getTagsId());
                                    }
                                }
                                arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, tagsName);
                                mListView.setAdapter(arrayAdapter);
                                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view2, int position, long id) {
                                        String customize_tag_name = mListView.getAdapter().getItem(position).toString();
                                        Intent intent = new Intent();
                                        intent.setClass(view2.getContext(), MapsActivity.class);
                                        intent.putExtra("customize_tag_name",customize_tag_name);
                                        intent.putExtra("customized_tag", tagsID.get(position));
                                        intent.putExtra("customized_index",position);
                                        startActivity(intent);
                                    }
                                });
                            } catch (JSONException e) {
                                Log.e("debug", e.getLocalizedMessage());
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
                break;
            }
            case R.id.nav_map:{
                String url = "http://www.ncu.edu.tw/assets/thumbs/pic/NCU_Campus_Map_(map-JPG).jpg";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
            }
            case R.id.nav_about:{
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                final View v = inflater.inflate(R.layout.about, null);
                new AlertDialog.Builder(MainActivity.this)
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
            Log.e("di","dd");
            try {
                Credential authResult = oAuthManager.authorizeExplicitly("user",null,null).getResult();
                if (authResult.getExpiresInSeconds() <= 60)
                    authResult.refreshToken();
                accessToken = authResult.getAccessToken();
                getInfo();
                authSuccess=true;
            } catch (IOException e) {
                //Toast.makeText(getApplicationContext(),getResources().getString(R.string.login_fail), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (CancellationException e){
                MapsActivity.accessToken =null;
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
    private void initViewPager() {
        mTabs = (TabLayout) findViewById(R.id.tabs);
        mTabs.addTab(mTabs.newTab().setText(R.string.category_default));
        mTabs.addTab(mTabs.newTab().setText(R.string.category_customized));
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), mTabs.getTabCount(), fab,getApplicationContext());
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabs));
        mTabs.setupWithViewPager(mViewPager);
    }
    private void getInfo(){
        url = "http://140.115.3.188/personnel/v1/info";
        mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("TAG", response);
                try {
                    JSONObject InfoObject = new JSONObject(response);
                    info.setPersonID(InfoObject.getString("id"));
                    info.setPersonNumber(InfoObject.getString("number"));
                    info.setPersonName(InfoObject.getString("name"));
                    info.setPersonType(InfoObject.getString("type"));
                    info.setPersonUnit(InfoObject.getString("unit"));
                    info.setPersonGroup(InfoObject.getString("group"));
                    Log.e("Parsed data is", ":" + info.getPersonID() + "," + info.getPersonNumber() + "," + info.getPersonName() +
                            "," + info.getPersonType() + "," + info.getPersonUnit() + "," + info.getPersonGroup());
                    title.setText(getResources().getString(R.string.user_login));
                    profileName.setText(info.getPersonName()+"  "+info.getPersonType());
                    profileType.setText(info.getPersonType());
                    MapsActivity.title.setText(getResources().getString(R.string.user_login));
                    MapsActivity.profileName.setText(info.getPersonName()+"  "+info.getPersonType());
                    MapsActivity.profileType.setText(info.getPersonType());
                } catch (JSONException e) {
                    Log.e("debug", e.getLocalizedMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
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
    private void initTelephony() {
        if (((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number() == null) {
            View phone_view=findViewById(R.id.id_content_main);
            phone_view.setVisibility(View.GONE);
        }
        else {
            Button militaryCall = (Button) findViewById(R.id.military_button);
            Button frontSecurityCall = (Button) findViewById(R.id.front_security_button);
            Button backSecurityCall = (Button) findViewById(R.id.back_security_button);
            Button healthButton = (Button) findViewById(R.id.health_button);
            View.OnClickListener callListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    switch (v.getId()) {
                        case R.id.military_button:
                            intent.setData(Uri.parse("tel:03-2805666"));
                            break;
                        case R.id.front_security_button:
                            intent.setData(Uri.parse("tel:03-4227151%2357119"));
                            break;
                        case R.id.back_security_button:
                            intent.setData(Uri.parse("tel:03-4227151%2357319"));
                            break;
                        case R.id.health_button:
                            intent.setData(Uri.parse("tel:03-4227151%2357270"));
                            break;
                        default:
                            return;
                    }
                    startActivity(intent);
                }
            };
            militaryCall.setOnClickListener(callListener);
            frontSecurityCall.setOnClickListener(callListener);
            backSecurityCall.setOnClickListener(callListener);
            healthButton.setOnClickListener(callListener);
        }
    }
    public String getAccessToken() {
        return accessToken;
    }
}
