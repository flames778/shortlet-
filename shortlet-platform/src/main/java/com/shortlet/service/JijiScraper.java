package com.shortlet.service;

import com.shortlet.model.JijiListing;
import java.util.List;

public interface JijiScraper {
    List<JijiListing> fetchListings(String searchUrl) throws Exception;
}
