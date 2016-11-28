package com.edu.ncu.cc.ncumapwiki;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Map;

public class DefaultCategory extends Fragment {
    GridView mGridView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view1 =  inflater.inflate(R.layout.pager_item0, container, false);
        mGridView = (GridView) view1.findViewById(R.id.main_page_GridView);
        mGridView.setAdapter(new GridViewAdapter(getContext()));
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.setClass(view.getContext(), MapsActivity.class);
                intent.putExtra("default_tag",position);
                Log.e("result","is"+position);
                startActivity(intent);
            }
        });
        return view1;
    }
    static class  GridViewAdapter extends BaseAdapter{
        private Context context;
        private int[] image_gv1 = {
                R.drawable.wheelchair_ramp, R.drawable.disabled_car_parking,
                R.drawable.disabled_motor_parking, R.drawable.emergency,
                R.drawable.aed, R.drawable.restaurant, R.drawable.sport_recreation,
                R.drawable.administration,R.drawable.research, R.drawable.dormitory,
                R.drawable.other, R.drawable.toilet, R.drawable.atm,
                R.drawable.bus_station, R.drawable.parking_lot
        };
        String[] imgText_gv1;
        public GridViewAdapter(Context context) {
            this.context = context;
            imgText_gv1 = context.getResources().getStringArray(R.array.imgText);
        }
        //get count of image
        @Override
        public int getCount() {
            return image_gv1.length;
        }
        //every image
        @Override
        public Object getItem(int position) {
            return image_gv1[position];
        }
        @Override
        public long getItemId(int position) {
            return position;
        }
        //fill every image
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null)
            {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.from(context).inflate(R.layout.grid_item, parent, false);
                holder = new ViewHolder();
                holder.img = (ImageView) convertView.findViewById(R.id.gv_image);
                holder.tv = (TextView) convertView.findViewById(R.id.gv_text);
                convertView.setTag(holder);
            }else
            {
                holder = (ViewHolder)convertView.getTag();
            }
            holder.img.setImageResource(image_gv1[position]);
            holder.tv.setText(imgText_gv1[position]);
            return convertView;
        }
        static class ViewHolder
        {
            public ImageView img;
            public TextView tv;
        }
    }
}