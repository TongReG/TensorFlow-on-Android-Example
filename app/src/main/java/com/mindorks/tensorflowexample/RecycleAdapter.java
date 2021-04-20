package com.mindorks.tensorflowexample;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.MyHolder> {

    private LayoutInflater mLayoutInflater;

    private static List<Ritem> mDataSyn = new ArrayList<Ritem>(MenuItemUtils.settingsItemCount + MenuItemUtils.sideItemCount);


    public RecycleAdapter(Context context) {
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public RecycleAdapter.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //https://blog.csdn.net/tnove/article/details/78761511
        return new MyHolder(mLayoutInflater.inflate(R.layout.rec_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecycleAdapter.MyHolder holder, final int pos) {
        holder.mTextView.setText(mDataSyn.get(pos).getName());
        holder.mTextViewtwo.setText(mDataSyn.get(pos).getState());
        holder.mImageView.setImageResource(mDataSyn.get(pos).getImageId());
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
        return mDataSyn == null ? 0 : mDataSyn.size();
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

    public void addData(int position, Ritem newitem) {
        mDataSyn.add(position, newitem);
        notifyItemInserted(position);
    }

    public void removeData(int position) {
        mDataSyn.remove(position);
        notifyItemRemoved(position);
    }

    public void clearData() {
        mDataSyn.clear();
    }

    public static Ritem getItem(int position) {
        return mDataSyn.get(position);
    }

    public boolean isListEmpty() {
        return mDataSyn.isEmpty();
    }

}
