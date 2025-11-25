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

        // Buffer (version 2 = avec gestion de la terminaison)
        ProdConsBuffer buffer = new ProdConsBuffer(bufSz, nProd);

        // Mélange aléatoire (comme exigé dans l’objectif 1)
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < nProd; i++) {
            threads.add(new Producer(buffer, minProd, maxProd, prodTime));
        }

        for (int i = 0; i < nCons; i++) {
            threads.add(new Consumer(buffer, consTime));
        }

        // Mélange aléatoire pour une vraie concurrence
        Collections.shuffle(threads);

        // Démarrage entrelacé
        for (Thread t : threads) {
            t.start();
            Thread.sleep((int)(Math.random() * 5));  // favorise le multitâche
        }
    }
}
