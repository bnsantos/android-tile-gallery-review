package com.bnsantos.tile.gallery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bruno on 01/07/15.
 */
public class Item {
    public final List<String> urls;

    public Item(List<String> urls) {
        this.urls = new ArrayList<>();
        this.urls.addAll(urls);
    }
}
