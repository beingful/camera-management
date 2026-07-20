package com.beingful.camera.admin.model;

public class CameraEntry {
    public int id;
    public String name = "";
    public String url = "";
    public int rate;
    public int width;
    public int height;
    public int streamingServiceCode;
    public String encoding = "";

    @Override
    public String toString() {
        return id + " - " + name + " (" + url + ")";
    }
}
