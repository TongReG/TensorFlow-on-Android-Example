package com.mindorks.tensorflowexample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ListAdapter extends ArrayAdapter<Litem> {
    private int resourceId;
    private List<Litem> listobject;

    // 适配器的构造函数，把要适配的数据传入这里
    ListAdapter(Context context, int textViewResourceId, List<Litem> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        listobject = objects;
    }

    @Override
    public Litem getItem(int position) {
        return listobject.get(position);
    }

    // convertView 参数用于将之前加载好的布局进行缓存
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Litem pstn = getItem(position); //获取当前项的实例

        // 加个判断，以免ListView每次滚动时都要重新加载布局，以提高运行效率
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {

            // 避免ListView每次滚动时都要重新加载布局，以提高运行效率
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);

            // 避免每次调用getView()时都要重新获取控件实例
            viewHolder = new ViewHolder();
            viewHolder.ImageCache = view.findViewById(R.id.item_image);
            viewHolder.TextCache = view.findViewById(R.id.item_text);

            // 将ViewHolder存储在View中（即将控件的实例存储在其中）
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        // 获取控件实例，并调用set...方法使其显示出来
        viewHolder.ImageCache.setImageResource(pstn.getImageId());
        viewHolder.TextCache.setText(pstn.getName());
        return view;
    }

    // 定义一个内部类，用于对控件的实例进行缓存
    static class ViewHolder {
        ImageView ImageCache;
        TextView TextCache;
    }
}


class Litem {
    private static int imageId;
    private static String name;

    Litem(String name, int imageId) {
        Litem.name = name;
        Litem.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    int getImageId() {
        return imageId;
    }
}