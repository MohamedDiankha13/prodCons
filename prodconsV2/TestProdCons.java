package prodconsV2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class TestProdCons {

    public static void main(String[] args) throws Exception {

        // Chargement du fichier XML
        Properties p = new Properties();
        p.loadFromXML(
            TestProdCons.class.getClassLoader().getResourceAsStream("options.xml")
        );

        int nProd = Integer.parseInt(p.getProperty("nProd"));
        int nCons = Integer.parseInt(p.getProperty("nCons"));
        int bufSz = Integer.parseInt(p.getProperty("bufSz"));
        int prodTime = Integer.parseInt(p.getProperty("prodTime"));
        int consTime = Integer.parseInt(p.getProperty("consTime"));
        int minProd = Integer.parseInt(p.getProperty("minProd"));
        int maxProd = Integer.parseInt(p.getProperty("maxProd"));

        // Buffer version 2 : gère la terminaison proprement
        ProdConsBuffer buffer = new ProdConsBuffer(bufSz, nProd);

        // Création des threads
        List<Thread> producers = new ArrayList<>();
        List<Thread> consumers = new ArrayList<>();
        List<Thread> all = new ArrayList<>();

        for (int i = 0; i < nProd; i++) {
            Thread t = new Producer(buffer, minProd, maxProd, prodTime);
            producers.add(t);
            all.add(t);
        }

        for (int i = 0; i < nCons; i++) {
            Thread t = new Consumer(buffer, consTime);
            consumers.add(t);
            all.add(t);
        }

        // Mélange aléatoire pour maximiser la concurrence
        Collections.shuffle(all);

        // Démarrage aléatoire + petites pauses pour encore plus d’interleaving
        for (Thread t : all) {
            t.start();
            Thread.sleep((int)(Math.random() * 5));
        }

        /*
         * >>>>>>> COMMENTAIRE IMPORTANT POUR L’OBJECTIF 2 <<<<<<<
         *
         * Contrairement à l’objectif 1 :
         *   - les consommateurs NE tournent plus en boucle infinie.
         *   - lorsque tous les producteurs ont terminé ET que le buffer est vide,
         *         get() retourne null
         *     (voir ProdConsBuffer.get())
         *
         * Le consommateur détecte null et termine son thread proprement.
         *
         * Cela rend l’utilisation de join() enfin possible,
         * car tous les threads finiront naturellement.
         */

        // Attendre la fin des producteurs
        System.out.println("Attente des producteurs...");
        for (Thread prod : producers) {
            prod.join();
        }
        System.out.println("Tous les producteurs ont terminé.");

        // Attendre la fin des consommateurs
        System.out.println("Attente des consommateurs...");
        for (Thread cons : consumers) {
            cons.join();
        }
        System.out.println("Tous les consommateurs ont terminé.");

        // Vérification finale
        System.out.println("-----------------------------------------");
        if (buffer.getTotalConsumed() == buffer.getTotalToProduce()) {
            System.out.println("Tous les messages ont été consommés. Terminaison correcte.");
        } else {
            System.out.println("ERREUR : il reste des messages non consommés.");
        }
        System.out.println("-----------------------------------------");
    }
}
