package com.ests.fixitbyai.database;

import android.content.Context;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseInitializer {
    private final AppDatabase database;
    private final ExecutorService executorService;

    public DatabaseInitializer(Context context) {
        database = AppDatabase.getInstance(context);
        executorService = Executors.newSingleThreadExecutor();
    }

    public void initializeDatabase() {
        executorService.execute(() -> {
            // Vérifier si la base de données est vide
            if (database.guideDao().getAllGuides().getValue() == null || 
                database.guideDao().getAllGuides().getValue().isEmpty()) {
                
                // Ajouter des guides de test
                database.guideDao().insert(new Guide(
                    "Réparation d'un écran de smartphone",
                    "Guide complet pour remplacer un écran de smartphone cassé",
                    "Électronique",
                    "Moyen",
                    "1. Éteindre le téléphone\n2. Retirer la batterie\n3. Déconnecter l'ancien écran\n4. Installer le nouvel écran\n5. Reconnecter la batterie",
                    "https://example.com/smartphone_screen.jpg"
                ));

                database.guideDao().insert(new Guide(
                    "Remplacement d'une courroie de distribution",
                    "Guide étape par étape pour remplacer une courroie de distribution",
                    "Mécanique",
                    "Difficile",
                    "1. Démonter les accessoires\n2. Marquer les repères\n3. Retirer l'ancienne courroie\n4. Installer la nouvelle courroie\n5. Vérifier l'alignement",
                    "https://example.com/timing_belt.jpg"
                ));

                database.guideDao().insert(new Guide(
                    "Réparation d'une fuite de robinet",
                    "Guide simple pour réparer un robinet qui fuit",
                    "Plomberie",
                    "Facile",
                    "1. Couper l'eau\n2. Démonter le robinet\n3. Remplacer le joint\n4. Remonter le robinet\n5. Tester l'étanchéité",
                    "https://example.com/faucet.jpg"
                ));
            }
        });
    }

    public void shutdown() {
        executorService.shutdown();
    }
} 