package com.ests.fixitbyai.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.ests.fixitbyai.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.Locale;
import com.ests.fixitbyai.GeminiHelper;

public class VoiceSearchFragment extends Fragment {
    private static final int SPEECH_REQUEST_CODE = 0;
    private static final int PERMISSION_REQUEST_CODE = 1;
    
    private FloatingActionButton voiceButton;
    private TextView resultText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_voice_search, container, false);
        
        voiceButton = view.findViewById(R.id.voice_button);
        resultText = view.findViewById(R.id.result_text);
        
        voiceButton.setOnClickListener(v -> startVoiceRecognition());
        
        return view;
    }

    private void startVoiceRecognition() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    PERMISSION_REQUEST_CODE);
            return;
        }

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.FRENCH.toString());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.voice_search_hint));
        
        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        } catch (Exception e) {
            Toast.makeText(requireContext(), getString(R.string.error_voice_recognition),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == getActivity().RESULT_OK && data != null) {
            ArrayList<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            if (results != null && !results.isEmpty()) {
                String recognizedText = results.get(0);
                resultText.setText(recognizedText);
                // Appel à l'IA Gemini pour obtenir la réponse
                resultText.append("\nChargement de la réponse IA...");
                new Thread(() -> {
                    try {
                        String iaResponse = GeminiHelper.getGeminiResponse(recognizedText);
                        requireActivity().runOnUiThread(() -> resultText.setText(iaResponse));
                    } catch (Exception e) {
                        requireActivity().runOnUiThread(() -> resultText.setText("Erreur Gemini : " + e.getMessage()));
                    }
                }).start();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startVoiceRecognition();
            } else {
                Toast.makeText(requireContext(), getString(R.string.error_permission_denied),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
} 