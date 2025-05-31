import tensorflow as tf
import os

def convert_saved_model_to_tflite():
    # Chemin vers le modèle SavedModel
    saved_model_dir = "mobilenet-v2-tensorflow2-100-224-classification-v2"
    
    # Créer le dossier assets s'il n'existe pas
    assets_dir = "app/src/main/assets"
    if not os.path.exists(assets_dir):
        os.makedirs(assets_dir)

    # Convertir le modèle
    converter = tf.lite.TFLiteConverter.from_saved_model(saved_model_dir)
    
    # Configurer les options de conversion
    converter.optimizations = [tf.lite.Optimize.DEFAULT]
    converter.target_spec.supported_types = [tf.float32]
    converter.target_spec.supported_ops = [
        tf.lite.OpsSet.TFLITE_BUILTINS,
        tf.lite.OpsSet.SELECT_TF_OPS
    ]
    
    # Convertir le modèle
    tflite_model = converter.convert()
    
    # Sauvegarder le modèle converti
    tflite_model_path = os.path.join(assets_dir, "model.tflite")
    with open(tflite_model_path, 'wb') as f:
        f.write(tflite_model)
    
    print(f"Modèle converti et sauvegardé dans : {tflite_model_path}")

if __name__ == "__main__":
    convert_saved_model_to_tflite() 