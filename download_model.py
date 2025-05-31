import kagglehub
import os
import shutil

def download_model():
    # Télécharger le modèle
    model_path = kagglehub.model_download("google/mobilenet-v2/tensorFlow2/035-128-classification")
    print("Chemin du modèle téléchargé:", model_path)

    # Créer le dossier assets s'il n'existe pas
    assets_dir = "app/src/main/assets"
    if not os.path.exists(assets_dir):
        os.makedirs(assets_dir)

    # Copier le fichier model.tflite vers le dossier assets
    model_file = os.path.join(model_path, "model.tflite")
    if os.path.exists(model_file):
        shutil.copy2(model_file, os.path.join(assets_dir, "model.tflite"))
        print("Modèle copié avec succès dans", assets_dir)
    else:
        print("Erreur: Fichier model.tflite non trouvé dans", model_path)

if __name__ == "__main__":
    download_model() 