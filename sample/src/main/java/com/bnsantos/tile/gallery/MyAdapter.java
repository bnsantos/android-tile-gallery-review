package com.bnsantos.tile.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.bnsantos.tile.gallery.lib.GalleryTilePreview;

import java.util.List;

/**
 * Created by bruno on 01/07/15.
 */
public class MyAdapter extends ArrayAdapter<Item> {
    public MyAdapter(Context context, List<Item> objects) {
        super(context, R.layout.adapter_item, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext() .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.adapter_item, parent, false);
        GalleryTilePreview galleryTilePreview = (GalleryTilePreview) rowView.findViewById(R.id.mediaPreview);
        galleryTilePreview.loadFromString(getItem(position).urls);
        return rowView;
    }
}
