package com.mindorks.tensorflowexample;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.MyHolder> {

    private final LayoutInflater mLayoutInflater;
    private static Ritem chmodel =new Ritem("Choose Model", R.drawable.baseline_input_black_36);
    private static Ritem chout =new Ritem("Change Output", R.drawable.baseline_logout_black_36);
    private static Ritem mthread =new Ritem("Multi-threading", R.drawable.baseline_speed_black_36);
    private static Ritem ret =new Ritem("Return", R.drawable.baseline_arrow_back_black_36);

    static ArrayList<Ritem> mData = new ArrayList<Ritem>() {
    };


    public RecycleAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);

        for (int i = 0; i < 3; i++) {
            mData.add(chmodel);
            mData.add(chout);
            mData.add(mthread);
            mData.add(ret);
        }
    }

    @Override
    public RecycleAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //https://blog.csdn.net/tnove/article/details/78761511
        return new MyHolder(mLayoutInflater.inflate(R.layout.rec_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecycleAdapter.MyHolder holder, final int pos) {
        holder.mTextView.setText(mData.get(pos).getName());
        holder.mTextViewtwo.setText(mData.get(pos).getState());
        holder.mImageView.setImageResource(mData.get(pos).getImageId());
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int psn = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView, psn);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int psn = holder.getLayoutPosition();
                    mOnItemClickListener.onItemLongClick(holder.itemView, psn);
                    return false;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {
        TextView mTextView, mTextViewtwo;
        ImageView mImageView;

        MyHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.rec_text);
            mTextViewtwo = itemView.findViewById(R.id.rec_text2);
            mImageView = itemView.findViewById(R.id.rec_img);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    private RecycleAdapter.OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(RecycleAdapter.OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void addData(int position) {
        //mData.add(position, "hello x");
        notifyItemInserted(position);
    }

    public void removeData(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
    }

}

class Ritem {
    private static int imageId;
    private static String name;
    private static String state;

    Ritem(String name, int imageId) {
        Ritem.name = name;
        Ritem.imageId = imageId;
        Ritem.state = "Undefined";
    }

    public String getName() {
        return name;
    }

    public String getState() {
        return name.equals("Return") ? "Return to MainView" : state;
    }

    int getImageId() {
        return imageId;
    }
}