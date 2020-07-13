package com.example.MySpot.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.MySpot.R;
import com.example.MySpot.activities.MainActivity;
import com.example.MySpot.activities.SpotView;
import com.example.MySpot.models.Spot;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

import static androidx.core.app.ActivityCompat.startActivityForResult;

public class MySpotAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String SPOT_DETAIL = "spot_details";
    private Context mContext;
    private ArrayList<Spot> mSpotLists;
    private OnClickListener onClickListener = null;
    public MySpotAdapter(Context context, ArrayList<Spot> spotLists){
        mContext = context;
        mSpotLists = spotLists;
    }
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

            return new  MyViewHolder(LayoutInflater.from(mContext).inflate( R.layout.spot_item, parent, false));
        }

        public void setOnClickListener(OnClickListener onClickListener){
            this.onClickListener = onClickListener;
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
             Spot model = mSpotLists.get(position);

            if (holder instanceof MyViewHolder) {
                ((ImageView) holder.itemView.findViewById(R.id.iv_spot_image)).setImageURI(Uri.parse(model.getImage()));
                ( (TextView) holder.itemView.findViewById(R.id.tv_title) ).setText(model.getTitel());
                ( (TextView) holder.itemView.findViewById(R.id.tv_description) ).setText(model.getDescription());
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), SpotView.class);
                        intent.putExtra(SPOT_DETAIL, mSpotLists.get(position));
                        mContext.startActivity(intent);
                }
            });
        }


        public interface OnClickListener {
            void onClick(int position, Spot spot);
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
