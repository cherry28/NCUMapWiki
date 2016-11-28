package com.edu.ncu.cc.ncumapwiki;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapsSearchableActivity extends ListActivity {
    JSONObject objects;
    Context context;
    RequestQueue mQueue;
    String query;
    Search tempQuery;
    ArrayList<Search> getQuery = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleIntent(getIntent());
        //Volley
        mQueue = Volley.newRequestQueue(this);
        context = this;
        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_GLOBAL);
    }
    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
        Intent intentSearch = new Intent();
        intentSearch.setClass(this, MapSearchActivity.class);
       // doSearch(query);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            doSearch(query);
        }
    }
    private void doSearch(String queryStr) {
        String url = "http://140.115.189.151:9292/location/v2/search?q="+queryStr;
        StringRequest mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("TAG", response);
                try {
                    JSONArray ArrayObjects=new JSONArray(response);
                    for (int i = 0; i < ArrayObjects.length(); i++) {
                        objects=ArrayObjects.getJSONObject(i);
                        tempQuery=new Search(objects.getString("name"),objects.getString("type"),objects.getString("id"));
                        getQuery.add(tempQuery);
                        getQuery.get(i);
                        Toast.makeText(getApplicationContext(), query, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e("debug", "Json parse failed!");
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
    }
    public void onListItemClick(ListView l, View v, int position, long id) {
        // call the appropriate detail activity
    }

    /*@Override
    public boolean onSearchRequested() {
        Bundle appSearchData = new Bundle();
        appSearchData.putString("KEY", query);
        startSearch(null, false, appSearchData, false);
        // 必须返回true。否则绑定的数据作废
        return true;
    }*/
}
