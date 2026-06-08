package com.wuyiming.singlecolumnadfeed_android.tracking;

import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AdTracker {
    private static final String TAG = "AdTracker";
    private static final long EXPOSURE_THRESHOLD_MS = 1000L;
    private static final float VISIBILITY_THRESHOLD = 0.5f;

    private final Set<String> exposedIds = new HashSet<>();
    private final Map<String, Long> pendingStartTime = new HashMap<>();

    public void trackVisibility(Map<String, Float> ratios) {
        long now = System.currentTimeMillis();
        for (Map.Entry<String, Float> entry : ratios.entrySet()) {
            String id = entry.getKey();
            float ratio = entry.getValue();

            if (exposedIds.contains(id)) continue;

            if (ratio >= VISIBILITY_THRESHOLD) {
                Long start = pendingStartTime.get(id);
                if (start == null) {
                    pendingStartTime.put(id, now);
                } else if (now - start >= EXPOSURE_THRESHOLD_MS) {
                    exposedIds.add(id);
                    pendingStartTime.remove(id);
                    Log.d(TAG, "Valid exposure: " + id);
                }
            } else {
                pendingStartTime.remove(id);
            }
        }
    }

    public void trackClick(String feedId) {
        Log.d(TAG, "Click: " + feedId);
    }

    public boolean isExposed(String feedId) {
        return exposedIds.contains(feedId);
    }

    public void reset() {
        exposedIds.clear();
        pendingStartTime.clear();
    }
}
