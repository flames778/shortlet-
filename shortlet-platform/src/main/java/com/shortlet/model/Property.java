package com.shortlet.model;

import java.math.BigDecimal;

public class Property {
    private long id;
    private String title;
    private String city;
    private String address;
    private BigDecimal nightlyRate;
    private String imageUrl;
    private String sourceUrl;

    private double latitude;
    private double longitude;
    private boolean wifi;
    private boolean selfCheckIn;
    private boolean nearAirport;
    private int replyTimeMinutes;
    private double rating;
    private java.util.List<String> images = new java.util.ArrayList<>();
    private boolean isWishlisted;
    private int urgencyViews;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getNightlyRate() {
        return nightlyRate;
    }

    public void setNightlyRate(BigDecimal nightlyRate) {
        this.nightlyRate = nightlyRate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isWifi() {
        return wifi;
    }

    public void setWifi(boolean wifi) {
        this.wifi = wifi;
    }

    public boolean isSelfCheckIn() {
        return selfCheckIn;
    }

    public void setSelfCheckIn(boolean selfCheckIn) {
        this.selfCheckIn = selfCheckIn;
    }

    public boolean isNearAirport() {
        return nearAirport;
    }

    public void setNearAirport(boolean nearAirport) {
        this.nearAirport = nearAirport;
    }

    public int getReplyTimeMinutes() {
        return replyTimeMinutes;
    }

    public void setReplyTimeMinutes(int replyTimeMinutes) {
        this.replyTimeMinutes = replyTimeMinutes;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public java.util.List<String> getImages() {
        return images;
    }

    public void setImages(java.util.List<String> images) {
        this.images = images;
    }

    public boolean getIsWishlisted() {
        return isWishlisted;
    }

    public void setIsWishlisted(boolean isWishlisted) {
        this.isWishlisted = isWishlisted;
    }

    public int getUrgencyViews() {
        return urgencyViews;
    }

    public void setUrgencyViews(int urgencyViews) {
        this.urgencyViews = urgencyViews;
    }

    public String getFormattedReplyTime() {
        if (replyTimeMinutes < 60) {
            return "Usually replies in <1 hour";
        } else {
            int hours = replyTimeMinutes / 60;
            return "Usually replies in " + hours + " hour" + (hours > 1 ? "s" : "");
        }
    }
}
