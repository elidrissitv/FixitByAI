package com.ests.fixitbyai.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ests.fixitbyai.R;

public class ImageResultDialog extends Dialog {
    private String resultText;

    public ImageResultDialog(@NonNull Context context, String resultText) {
        super(context);
        this.resultText = resultText;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_image_result);

        // Configurer le dialog pour prendre toute la largeur
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(layoutParams);
        }

        TextView resultTextView = findViewById(R.id.result_text);
        resultTextView.setText(resultText);
    }
} 