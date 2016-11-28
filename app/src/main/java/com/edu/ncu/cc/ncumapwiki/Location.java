package com.edu.ncu.cc.ncumapwiki;
public class Location {
    private Double lat;
    private Double lng;
    public Location (Double lat,Double lng)
    {
        this.lat=lat;
        this.lng=lng;
    }
    public void setLat(Double lat) {this.lat = lat;}
    public Double getLat() {return this.lat;}
    public void setLng(Double lng) {this.lng = lng;}
    public Double getLng() {return this.lng;}
}
