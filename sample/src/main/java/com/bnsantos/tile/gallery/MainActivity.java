package com.bnsantos.tile.gallery;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.bnsantos.tile.gallery.lib.GalleryTilePreview;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MainActivity extends ActionBarActivity {
    private GalleryTilePreview mMediaPreview1;
    private GalleryTilePreview mMediaPreview2;
    private GalleryTilePreview mMediaPreview3;
    private GalleryTilePreview mMediaPreview4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMediaPreview1 = (GalleryTilePreview) findViewById(R.id.mediaPreview1);
        mMediaPreview2 = (GalleryTilePreview) findViewById(R.id.mediaPreview2);
        mMediaPreview3 = (GalleryTilePreview) findViewById(R.id.mediaPreview3);
        mMediaPreview4 = (GalleryTilePreview) findViewById(R.id.mediaPreview4);

        List<String> pictures = Arrays.asList(getResources().getStringArray(R.array.pictures));
        Random random = new Random(System.currentTimeMillis());
        Collections.shuffle(pictures, random);
        mMediaPreview1.loadFromString(pictures);
        Collections.shuffle(pictures, random);
        mMediaPreview2.loadFromString(pictures);
        Collections.shuffle(pictures, random);
        mMediaPreview3.loadFromString(pictures);
        Collections.shuffle(pictures, random);
        mMediaPreview4.loadFromString(pictures);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
