package com.wuyiming.singlecolumnadfeed_android.tracking;

public class TrackingModels {

    public static class ExposureEvent {
        public final String feedId;
        public final long timestamp;
        public final float visibleRatio;

        public ExposureEvent(String feedId, long timestamp, float visibleRatio) {
            this.feedId = feedId;
            this.timestamp = timestamp;
            this.visibleRatio = visibleRatio;
        }
    }

    public static class ClickEvent {
        public final String feedId;
        public final long timestamp;

        public ClickEvent(String feedId, long timestamp) {
            this.feedId = feedId;
            this.timestamp = timestamp;
        }
    }
}
