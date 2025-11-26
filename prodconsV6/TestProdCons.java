package prodconsV6;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class TestProdCons {

    public static void main(String[] args) throws Exception {

        // Charger la configuration
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

        ProdConsBuffer buffer = new ProdConsBuffer(bufSz);

        // Liste pour mélanger producteur/consommateur
        List<Thread> threads = new ArrayList<>();

        // Ajouter les producteurs
        for (int i = 0; i < nProd; i++) {
            threads.add(new Producer(buffer, minProd, maxProd, prodTime));
        }

        // Ajouter les consommateurs
        for (int i = 0; i < nCons; i++) {
            threads.add(new Consumer(buffer, consTime));
        }

        // Mélange complet pour maximiser la concurrence
        Collections.shuffle(threads);

        // Démarrer les threads avec léger décalage pour interleaving
        for (Thread t : threads) {
            t.start();
            Thread.sleep((int)(Math.random() * 5));
        }

        /*
         * NOTE IMPORTANTE (pour le rapport) :
         *  - Dans l'objectif 6, on utilise des messages synchrones : 
         *        * un producteur dépose n copies logiques d’un message.
         *        * chaque consommateur consomme UNE copie.
         *        * tous les consommateurs doivent consommer AVANT que le producteur soit libéré.
         *  - Mélanger les threads au démarrage est ESSENTIEL pour éviter les blocages prolongés
         *    et garantir une vraie concurrence.
         *  - Comme les consommateurs tournent en boucle infinie et que le protocole synchrone
         *    n’a pas de mécanisme de terminaison globale → pas de join() dans cet objectif.
         */
    }
}
