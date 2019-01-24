package com.example.thanx2.patternview.model;

import java.util.Date;

import static com.example.thanx2.patternview.constant.Constant.ORIGINAL_SCALE;
import static com.example.thanx2.patternview.constant.Constant.ROW_HEIGHT_DEFAULT;

public class Pattern {

    private Long id;
    private String uri;
    private Integer rowHeight;
    private Integer scrollX;
    private Integer scrollY;
    private Float scale;
    private Long lastOpened;

    public Pattern() {
        id = null;
        uri = null;
        rowHeight = ROW_HEIGHT_DEFAULT;
        scrollX = 0;
        scrollY = 0;
        scale = ORIGINAL_SCALE;
        lastOpened = null;
    }

    public Pattern(Long id, String uri, Integer rowHeight, Integer scrollX, Integer scrollY, Float scale) {
        this.id = id;
        this.uri = uri;
        this.rowHeight = rowHeight;
        this.scrollX = scrollX;
        this.scrollY = scrollY;
        this.scale = scale;
        this.lastOpened = new Date().getTime();
    }

    public Pattern(String uri, Integer rowHeight, Integer scrollX, Integer scrollY, Float scale) {
        this.uri = uri;
        this.rowHeight = rowHeight;
        this.scrollX = scrollX;
        this.scrollY = scrollY;
        this.scale = scale;
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

    public Integer getScrollX() {
        return scrollX;
    }

    public void setScrollX(Integer scrollX) {
        this.scrollX = scrollX;
    }

    public Integer getScrollY() {
        return scrollY;
    }

    public void setScrollY(Integer scrollY) {
        this.scrollY = scrollY;
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
