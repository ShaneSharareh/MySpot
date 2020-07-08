package com.example.MySpot.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.MySpot.R;
import com.example.MySpot.models.Spot;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class MySpotAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<Spot> mSpotLists;
    public MySpotAdapter(Context context, ArrayList<Spot> spotLists){

        mContext = context;
        mSpotLists = spotLists;
    }
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

            return new  MyViewHolder(LayoutInflater.from(mContext).inflate( R.layout.spot_item, parent, false));
        }


        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Spot model = mSpotLists.get(position);

            if (holder instanceof MyViewHolder) {
                ((ImageView) holder.itemView.findViewById(R.id.iv_spot_image)).setImageURI(Uri.parse(model.getImage()));
                ( (TextView) holder.itemView.findViewById(R.id.tv_title) ).setText(model.getTitel());
                ( (TextView) holder.itemView.findViewById(R.id.tv_description) ).setText(model.getDescription());
            }
        }


       public int getItemCount(){
            return mSpotLists.size();
        }


        private class MyViewHolder extends  RecyclerView.ViewHolder {
            public MyViewHolder(View view){
                super(view);
            }
        }
    }
