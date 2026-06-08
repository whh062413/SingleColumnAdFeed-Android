package com.wuyiming.singlecolumnadfeed_android.data.model;

import java.io.Serializable;
import java.util.List;

public class AdInsight implements Serializable {
    private final String summary;
    private final List<String> tags;

    public AdInsight(String summary, List<String> tags) {
        this.summary = summary;
        this.tags = tags;
    }

    public String getSummary() {
        return summary;
    }

    public List<String> getTags() {
        return tags;
    }
}
