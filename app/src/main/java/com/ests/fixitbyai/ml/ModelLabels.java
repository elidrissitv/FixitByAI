package com.ests.fixitbyai.ml;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ModelLabels {
    private static final String TAG = "ModelLabels";
    private static final String LABELS_FILE = "labels.txt";
    private final List<String> labels;

    public ModelLabels(Context context) {
        labels = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(LABELS_FILE)));
            String line;
            while ((line = reader.readLine()) != null) {
                labels.add(line.trim());
            }
            reader.close();
        } catch (IOException e) {
            Log.e(TAG, "Erreur lors de la lecture des labels", e);
        }
    }

    public String getLabel(int index) {
        if (index >= 0 && index < labels.size()) {
            return labels.get(index);
        }
        return "Unknown";
    }

    public int getLabelCount() {
        return labels.size();
    }
} 