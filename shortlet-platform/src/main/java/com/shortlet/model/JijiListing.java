package com.shortlet.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class JijiListing {
    private long id;
    private String jijiUrl;
    private String title;
    private BigDecimal priceNumeric;
    private String priceText;
    private String location;
    private String description;
    private String imageUrl;
    private Timestamp scrapedAt;
    private boolean isActive;

    public JijiListing() {}

    public JijiListing(String jijiUrl, String title, BigDecimal priceNumeric, String priceText, String location, String description, String imageUrl) {
        this.jijiUrl = jijiUrl;
        this.title = title;
        this.priceNumeric = priceNumeric;
        this.priceText = priceText;
        this.location = location;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getJijiUrl() {
        return jijiUrl;
    }

    public void setJijiUrl(String jijiUrl) {
        this.jijiUrl = jijiUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getPriceNumeric() {
        return priceNumeric;
    }

    public void setPriceNumeric(BigDecimal priceNumeric) {
        this.priceNumeric = priceNumeric;
    }

    public String getPriceText() {
        return priceText;
    }

    public void setPriceText(String priceText) {
        this.priceText = priceText;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Timestamp getScrapedAt() {
        return scrapedAt;
    }

    public void setScrapedAt(Timestamp scrapedAt) {
        this.scrapedAt = scrapedAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
