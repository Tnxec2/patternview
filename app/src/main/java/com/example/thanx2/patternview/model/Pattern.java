package com.example.thanx2.patternview.model;

import android.net.Uri;

import java.util.Date;

import static com.example.thanx2.patternview.constant.Constant.ORIGINAL_SCALE;
import static com.example.thanx2.patternview.constant.Constant.ROW_HEIGHT_DEFAULT;

public class Pattern {

    private Long id;
    private Uri uri;
    private Integer rowHeight;
    private Integer patternX;
    private Integer patternY;
    private Float scale;
    private Long lastOpened;

    public Pattern() {
        id = null;
        uri = null;
        rowHeight = ROW_HEIGHT_DEFAULT;
        patternX = 0;
        patternY = 0;
        scale = ORIGINAL_SCALE;
        lastOpened = null;
    }

    public Pattern(Long id, Uri uri, Integer rowHeight, Integer patternX, Integer patternY, Float scale) {
        this.id = id;
        this.uri = uri;
        this.rowHeight = rowHeight;
        this.patternX = patternX;
        this.patternY = patternY;
        this.scale = scale;
        this.lastOpened = new Date().getTime();
    }

    public Pattern(Uri uri, Integer rowHeight, Integer patternX, Integer patternY, Float scale) {
        this.uri = uri;
        this.rowHeight = rowHeight;
        this.patternX = patternX;
        this.patternY = patternY;
        this.scale = scale;
        this.lastOpened = new Date().getTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Uri getUri() {
            return uri;
    }

    public String getUriString() {
        if ( uri != null) {
            return uri.toString();
        } else {
            return null;
        }
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public void setUri(String uri) {
        this.uri = Uri.parse(uri);
    }

    public Integer getRowHeight() {
        return rowHeight;
    }

    public void setRowHeight(Integer rowHeight) {
        this.rowHeight = rowHeight;
    }

    public Integer getPatternX() {
        return patternX;
    }

    public void setPatternX(Integer patternX) {
        this.patternX = patternX;
    }

    public Integer getPatternY() {
        return patternY;
    }

    public void setPatternY(Integer patternY) {
        this.patternY = patternY;
    }

    public Float getScale() {
        return scale;
    }

    public void setScale(Float scalle) {
        this.scale = scalle;
    }

    public Long getLastOpened() {
        return lastOpened;
    }

    public void setLastOpened(Long lastOpened) {
        this.lastOpened = lastOpened;
    }
}
