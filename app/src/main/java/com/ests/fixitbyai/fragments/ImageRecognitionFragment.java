package com.ests.fixitbyai.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.ests.fixitbyai.R;
import com.ests.fixitbyai.ai.ImageClassifier;
import com.ests.fixitbyai.GeminiHelper;
import com.ests.fixitbyai.dialogs.ImageResultDialog;
import java.io.InputStream;
import java.util.List;

public class ImageRecognitionFragment extends Fragment {
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 101;
    private static final int GALLERY_REQUEST_CODE = 102;

    private ImageView imageView;
    private EditText promptEdit;
    private Button captureButton;
    private Button importButton;
    private Button sendPromptButton;
    private ImageClassifier imageClassifier;
    private String currentClassification;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_recognition, container, false);

        imageView = view.findViewById(R.id.image_view);
        promptEdit = view.findViewById(R.id.prompt_edit);
        captureButton = view.findViewById(R.id.capture_button);
        importButton = view.findViewById(R.id.import_button);
        sendPromptButton = view.findViewById(R.id.send_prompt_button);

        imageClassifier = new ImageClassifier(requireContext());

        captureButton.setOnClickListener(v -> checkCameraPermissionAndOpen());
        importButton.setOnClickListener(v -> openGallery());
        sendPromptButton.setOnClickListener(v -> sendPromptToAI());

        return view;
    }

    private void checkCameraPermissionAndOpen() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_CODE);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Toast.makeText(requireContext(), getString(R.string.error_permission_denied),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == getActivity().RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imageView.setImageBitmap(imageBitmap);
                processImage(imageBitmap);
            }
        } else if (requestCode == GALLERY_REQUEST_CODE && resultCode == getActivity().RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                try (InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri)) {
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    imageView.setImageBitmap(bitmap);
                    processImage(bitmap);
                } catch (Exception e) {
                    showResult("Erreur lors de l'import de l'image");
                }
            }
        }
    }

    private void processImage(Bitmap bitmap) {
        showResult("Analyse en cours...");
        promptEdit.setText("");
        
        imageClassifier.classifyImage(bitmap, new ImageClassifier.ClassificationCallback() {
            @Override
            public void onClassificationComplete(List<ImageClassifier.Classification> results) {
                if (results.isEmpty()) {
                    showResult("Aucun objet reconnu");
                    return;
                }

                StringBuilder classificationText = new StringBuilder();
                for (ImageClassifier.Classification result : results) {
                    classificationText.append(result.toString()).append("\n");
                }

                currentClassification = results.get(0).getLabel();
                String defaultPrompt = "J'ai identifié un objet qui semble être " + currentClassification + 
                                     ". Pouvez-vous me donner des conseils sur comment le réparer ou l'entretenir ?";
                
                showResult(classificationText.toString());
                promptEdit.setText(defaultPrompt);
            }

            @Override
            public void onClassificationError(String error) {
                showResult(error);
            }
        });
    }

    private void sendPromptToAI() {
        String prompt = promptEdit.getText().toString().trim();
        if (prompt.isEmpty()) {
            Toast.makeText(requireContext(), "Veuillez entrer un prompt", Toast.LENGTH_SHORT).show();
            return;
        }

        showResult("Chargement de la réponse IA...");

        new Thread(() -> {
            try {
                String iaResponse = GeminiHelper.getGeminiResponse(prompt);
                requireActivity().runOnUiThread(() -> {
                    showResult(iaResponse);
                });
            } catch (Exception e) {
                requireActivity().runOnUiThread(() -> {
                    showResult("Erreur Gemini : " + e.getMessage());
                });
            }
        }).start();
    }

    private void showResult(String result) {
        ImageResultDialog dialog = new ImageResultDialog(requireContext(), result);
        dialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (imageClassifier != null) {
            imageClassifier.close();
        }
    }
} 