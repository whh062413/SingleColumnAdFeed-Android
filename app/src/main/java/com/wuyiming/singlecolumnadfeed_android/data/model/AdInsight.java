package com.wuyiming.singlecolumnadfeed_android.data.model;

import java.util.List;
import java.util.Objects;

public final class AdInsight {
    private final String summary;
    private final List<String> tags;

    public AdInsight(String summary, List<String> tags) {
        this.summary = summary;
        this.tags = tags;
    }

    public String getSummary() { return summary; }

    public List<String> getTags() { return tags; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AdInsight)) return false;
        AdInsight that = (AdInsight) o;
        return Objects.equals(summary, that.summary) && Objects.equals(tags, that.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hash(summary, tags);
    }

    @Override
    public String toString() {
        return "AdInsight{summary='" + summary + "', tags=" + tags + '}';
    }
}
