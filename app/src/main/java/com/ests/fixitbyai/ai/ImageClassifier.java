package com.ests.fixitbyai.ai;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageClassifier {
    private static final String TAG = "ImageClassifier";
    private final Context context;
    private final ExecutorService executorService;
    private final com.google.mlkit.vision.label.ImageLabeler labeler;

    public ImageClassifier(Context context) {
        this.context = context;
        this.executorService = Executors.newSingleThreadExecutor();
        
        // Configuration du labeler avec un seuil de confiance de 0.7
        ImageLabelerOptions options = new ImageLabelerOptions.Builder()
                .setConfidenceThreshold(0.7f)
                .build();
        
        this.labeler = ImageLabeling.getClient(options);
    }

    public void classifyImage(Bitmap bitmap, ClassificationCallback callback) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        
        labeler.process(image)
                .addOnSuccessListener(labels -> {
                    List<Classification> results = new ArrayList<>();
                    for (ImageLabel label : labels) {
                        results.add(new Classification(
                                label.getText(),
                                label.getConfidence()
                        ));
                    }
                    callback.onClassificationComplete(results);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Erreur de classification", e);
                    callback.onClassificationError("Erreur lors de la classification : " + e.getMessage());
                });
    }

    public void close() {
        executorService.shutdown();
    }

    public interface ClassificationCallback {
        void onClassificationComplete(List<Classification> results);
        void onClassificationError(String error);
    }

    public static class Classification {
        private final String label;
        private final float confidence;

        public Classification(String label, float confidence) {
            this.label = label;
            this.confidence = confidence;
        }

        public String getLabel() {
            return label;
        }

        public float getConfidence() {
            return confidence;
        }

        @Override
        public String toString() {
            return label + " (" + String.format("%.2f", confidence * 100) + "%)";
        }
    }
} 