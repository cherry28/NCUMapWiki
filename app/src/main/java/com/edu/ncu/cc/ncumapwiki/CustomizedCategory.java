package com.edu.ncu.cc.ncumapwiki;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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

public class CustomizedCategory extends Fragment {
    ListView mListView;
    ArrayList<String> tagsName = new ArrayList<>();
    ArrayList<String> tagsID = new ArrayList<>();
    Tags tempGetTag;
    int TagsCount=0;

    String url;
    StringRequest mStringRequest;
    JSONObject getResponseObject;
    RequestQueue mQueue;
    View view2;
    View modifyView;
    String tempID;
    String tempName;
    EditText edit_tagname;
    ArrayAdapter<String> arrayAdapter;
    FloatingActionButton fab;
    private SwipeRefreshLayout laySwipe;
    AlertDialog alert;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view2 = inflater.inflate(R.layout.pager_item1, container, false);
        fab = (FloatingActionButton)view2.findViewById(R.id.fab);
        mListView =(ListView) view2.findViewById(R.id.main_page_list);
        //swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        //swipeLayout.setOnRefreshListener();;
        mQueue = Volley.newRequestQueue(getActivity());
        url = getResources().getString(R.string.url)+"tags?limit=1000&page=1";
        mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("TAG", response);
                try {
                    getResponseObject = new JSONObject(response);
                    JSONArray objects= getResponseObject.getJSONArray("tags");
                    for (int i = 0; i < objects.length(); i++) {
                        JSONObject currentParsingTags = objects.getJSONObject(i);
                        tempGetTag= new Tags(currentParsingTags.getString("id"),currentParsingTags.getString("name"));
                        tagsName.add(tempGetTag.getTagsName());
                        tagsID.add(tempGetTag.getTagsId());
                    }
                    TagsCount=objects.length();
                    Log.e("testing", getResponseObject.getString("count"));
                    arrayAdapter = new ArrayAdapter<String>
                            (getActivity(), android.R.layout.simple_list_item_1, tagsName){
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent){
                            view2 = super.getView(position, convertView, parent);
                            TextView tv = (TextView) view2.findViewById(android.R.id.text1);
                            tv.setTextColor(Color.BLUE);
                            return view2;
                        }
                    };
                    initView(view2);
                    mListView.setAdapter(arrayAdapter);
                    initFab(fab);
                    mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        public boolean onItemLongClick(AdapterView<?> arg0, View v, int index, long arg3) {
                            if(MainActivity.accessToken ==null && MapsActivity.accessToken ==null)
                                Toast.makeText(getContext(), getString(R.string.remind_login_modify_tag), Toast.LENGTH_SHORT).show();
                            else{
                                tempID= tagsID.get(index);
                                tempName= tagsName.get(index);
                                Log.e("temp",tempID+"  "+tempName);
                                modifyView = View.inflate(getActivity(), R.layout.tag_modify, null);
                                edit_tagname= (EditText) modifyView.findViewById(R.id.tag_name);
                                final String names[]={getResources().getString(R.string.modify_tag),  getResources().getString(R.string.delete_tag)};
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                                LayoutInflater mInflater = getActivity().getLayoutInflater();
                                View convertView = mInflater.inflate(R.layout.map_item, null);
                                alertDialog.setView(convertView);
                                final ListView tagItem = (ListView) convertView.findViewById(R.id.listView1);
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, names);
                                tagItem.setAdapter(adapter);
                                alert = alertDialog.show();
                                tagItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                        alert.dismiss();
                                        switch (position) {
                                            case 0: {
                                                edit_tagname.setText(tempName);
                                                modifyTag(tempID, modifyView,tempName);
                                                break;
                                            }
                                            case 1:
                                                deleteTag(tempID,tempName);
                                                break;
                                        }
                                    }
                                });
                            }
                            return true;
                        }
                    });
                    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view2, int position, long id) {
                            String customize_tag_name = mListView.getAdapter().getItem(position).toString();
                            Log.e("dd",customize_tag_name+"    "+tagsID.get(position)+"    "+tagsID.get(position)+"    "+position);
                            Intent intent = new Intent();
                            intent.setClass(view2.getContext(), MapsActivity.class);
                            intent.putExtra("customize_tag_name",customize_tag_name);
                            intent.putExtra("customized_tag", tagsID.get(position));
                            intent.putExtra("customized_index",position);
                            Log.e("e::",customize_tag_name+" "+ tagsID.get(position)+"  "+position);
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
    return view2;
    }
    public void modifyTag(final String modifyID, View modifyView,final String unchangedText){
        alert.cancel();
        alert.dismiss();
        new android.app.AlertDialog.Builder(getActivity())
            .setTitle(R.string.modify_tag)
            .setView(modifyView)
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getContext(), getString(R.string.tag_message_modify_cancel)+": "+unchangedText, Toast.LENGTH_SHORT).show();
                }
                })
            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    final DialogInterface m_tag = dialog;
                    url = getResources().getString(R.string.url)+"tag/"+tempID;
                    StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Success", response);
                            try {
                                JSONObject PostResponseObject = new JSONObject(response);
                                for(int i=0;i<TagsCount;i++){
                                    if(tagsID.get(i)==modifyID){
                                        tagsID.set(i,PostResponseObject.getString("id"));
                                        tagsName.set(i,PostResponseObject.getString("name"));
                                        mListView.setAdapter(arrayAdapter);
                                        break;
                                    }
                                }
                                Toast.makeText(getContext(), getString(R.string.modify_tag)+": "+PostResponseObject.getString("name"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                Toast.makeText(getContext(), getString(R.string.modify_tag_fail), Toast.LENGTH_SHORT).show();
                            }
                            Toast.makeText(getContext(), getString(R.string.tag_message_modify), Toast.LENGTH_SHORT).show();
                            Log.e("Debug",tempID+", "+tempName);
                        }
                    }, new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getContext(), getString(R.string.modify_tag_fail), Toast.LENGTH_SHORT).show();
                        }
                    }) {
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> map = new HashMap<>();
                            if(MainActivity.accessToken !=null){
                                map.put("Authorization", "Bearer " + MainActivity.accessToken);
                            }
                            if(MapsActivity.accessToken !=null){
                                map.put("Authorization", "Bearer " + MapsActivity.accessToken);
                            }
                            return map;
                        }
                        public Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> map = new HashMap<>();
                            String tagName = ((EditText) ((AlertDialog) m_tag).findViewById(R.id.tag_name)).getText().toString();
                            map.put("id", modifyID);
                            map.put("name", tagName);
                            return map;
                        }
                    };
                    mQueue.add(stringRequest);
                }
                }).show();
    }
    public void deleteTag(final String deleteID,final String text){
        alert.cancel();
        alert.dismiss();
        final View item = View.inflate(getActivity(), R.layout.tag_delete,null);
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.delete_tag)
                .setView(item)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(), getString(R.string.tag_message_delete_cancel)+": "+text, Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final DialogInterface d_tag = dialog;
                        url = getResources().getString(R.string.url)+"tag/"+tempID;
                        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("Success", response);
                                try {
                                    JSONObject PostResponseObject = new JSONObject(response);
                                    tagsID.remove(PostResponseObject.getString("id"));
                                    tagsName.remove(PostResponseObject.getString("name"));
                                    arrayAdapter.remove(PostResponseObject.getString("name"));
                                    mListView.setAdapter(arrayAdapter);
                                    Toast.makeText(getContext(), getString(R.string.tag_message_delete)+": "+text, Toast.LENGTH_SHORT).show();
                                    TagsCount--;
                                } catch (JSONException e) {
                                    Toast.makeText(getContext(), getString(R.string.delete_tag_fail)+": "+text, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getContext(), getString(R.string.delete_tag_fail)+": "+text, Toast.LENGTH_SHORT).show();
                            }
                        }) {
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> map = new HashMap<>();
                                if(MainActivity.accessToken !=null){
                                    map.put("Authorization", "Bearer " + MainActivity.accessToken);
                                }
                                if(MapsActivity.accessToken !=null){
                                    map.put("Authorization", "Bearer " + MapsActivity.accessToken);
                                }
                                return map;
                            }
                            public Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> map = new HashMap<>();
                                map.put("id", deleteID);
                                return map;
                            }
                        };
                        mQueue.add(stringRequest);
                    }
                }).show();
    }
    public void initFab(FloatingActionButton fab){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.accessToken ==null && MapsActivity.accessToken ==null){
                    Toast.makeText(getContext(), getString(R.string.remind_login_add_tag), Toast.LENGTH_SHORT).show();
                }
                else{
                    final View item = View.inflate(getActivity(), R.layout.tag_add, null);
                    new android.app.AlertDialog.Builder(getActivity())
                            .setTitle(R.string.add_tag)
                            .setView(item)
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getContext(), getString(R.string.tag_message_add_cancel), Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final DialogInterface a_tag = dialog;
                                    url = getResources().getString(R.string.url)+"tag";
                                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Log.d("Success", response);
                                            try {
                                                JSONObject PostResponseObject = new JSONObject(response);
                                                tagsName.add(PostResponseObject.getString("name"));
                                                tagsID.add(PostResponseObject.getString("id"));
                                                //arrayAdapter.add(PostResponseObject.getString("name"));
                                                mListView.setAdapter(arrayAdapter);
                                                Toast.makeText(getContext(), getString(R.string.tag_message_add)+": "+PostResponseObject.getString("name"), Toast.LENGTH_SHORT).show();
                                                TagsCount++;
                                            } catch (JSONException e) {
                                                Toast.makeText(getContext(), getString(R.string.add_tag_fail), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(getContext(), getString(R.string.add_tag_fail), Toast.LENGTH_SHORT).show();
                                        }
                                    }) {
                                        public Map<String, String> getHeaders() throws AuthFailureError {
                                            Map<String, String> map = new HashMap<>();
                                            if(MainActivity.accessToken !=null){
                                                map.put("Authorization", "Bearer " + MainActivity.accessToken);
                                            }
                                            if(MapsActivity.accessToken !=null){
                                                map.put("Authorization", "Bearer " + MapsActivity.accessToken);
                                            }
                                            return map;
                                        }
                                        public Map<String, String> getParams() throws AuthFailureError {
                                            String tagName = ((EditText) ((android.app.AlertDialog) a_tag).findViewById(R.id.tag_name)).getText().toString();
                                            Log.e("Debug",tagName);
                                            Map<String, String> map = new HashMap<>();
                                            map.put("name", tagName);
                                            return map;
                                        }
                                    };
                                    mQueue.add(stringRequest);
                                }
                            }).show();
                }
            }
        });
    }
    private void initView(View view) {
        laySwipe = (SwipeRefreshLayout) view.findViewById(R.id.laySwipe);
        laySwipe.setOnRefreshListener(onSwipeToRefresh);
        laySwipe.setColorSchemeResources(
                android.R.color.holo_red_light,
                android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light);
        ListView lstData = (ListView) view.findViewById(R.id.main_page_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, tagsName);
        lstData.setAdapter(adapter);
        lstData.setOnScrollListener(onListScroll);
    }
    private SwipeRefreshLayout.OnRefreshListener onSwipeToRefresh = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            laySwipe.setRefreshing(true);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    laySwipe.setRefreshing(false);
                    Toast.makeText(getActivity(), getResources().getString(R.string.refresh), Toast.LENGTH_SHORT).show();
                }
            }, 300);
        }
    };
    private AbsListView.OnScrollListener onListScroll = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            if (firstVisibleItem == 0) {
                laySwipe.setEnabled(true);
            }else{
                laySwipe.setEnabled(false);
            }
        }
    };
}