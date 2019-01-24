package com.example.thanx2.patternview.model;

import java.util.Date;

public class Pattern {

    private Long id;
    private String uri;
    private Integer rowHeight;
    private Integer imageScrollX;
    private Integer imageScrollY;
    private Float imageScalle;
    private Long lastOpened;

    public Pattern(Long id, String uri, Integer rowHeight, Integer imageScrollX, Integer imageScrollY, Float imageScalle) {
        this.id = id;
        this.uri = uri;
        this.rowHeight = rowHeight;
        this.imageScrollX = imageScrollX;
        this.imageScrollY = imageScrollY;
        this.imageScalle = imageScalle;
        this.lastOpened = new Date().getTime();
    }

    public Pattern(String uri, Integer rowHeight, Integer imageScrollX, Integer imageScrollY, Float imageScalle) {
        this.id = null;
        this.uri = uri;
        this.rowHeight = rowHeight;
        this.imageScrollX = imageScrollX;
        this.imageScrollY = imageScrollY;
        this.imageScalle = imageScalle;
        this.lastOpened = new Date().getTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Integer getRowHeight() {
        return rowHeight;
    }

    public void setRowHeight(Integer rowHeight) {
        this.rowHeight = rowHeight;
    }

    public Integer getImageScrollX() {
        return imageScrollX;
    }

    public void setImageScrollX(Integer imageScrollX) {
        this.imageScrollX = imageScrollX;
    }

    public Integer getImageScrollY() {
        return imageScrollY;
    }

    public void setImageScrollY(Integer imageScrollY) {
        this.imageScrollY = imageScrollY;
    }

    public Float getImageScalle() {
        return imageScalle;
    }

    public void setImageScalle(Float imageScalle) {
        this.imageScalle = imageScalle;
    }

    public Long getLastOpened() {
        return lastOpened;
    }

    public void setLastOpened(Long lastOpened) {
        this.lastOpened = lastOpened;
    }
}
