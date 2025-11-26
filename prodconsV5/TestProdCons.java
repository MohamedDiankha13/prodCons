package prodconsV5;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Random;

public class TestProdCons {

    public static void main(String[] args) throws Exception {

        // Chargement du fichier XML
        Properties p = new Properties();
        p.loadFromXML(
            TestProdCons.class.getClassLoader().getResourceAsStream("options.xml")
        );

        int nProd     = Integer.parseInt(p.getProperty("nProd"));
        int bufSz     = Integer.parseInt(p.getProperty("bufSz"));
        int prodTime  = Integer.parseInt(p.getProperty("prodTime"));
        int consTime  = Integer.parseInt(p.getProperty("consTime"));
        int minProd   = Integer.parseInt(p.getProperty("minProd"));
        int maxProd   = Integer.parseInt(p.getProperty("maxProd"));
        
        String kValuesStr = p.getProperty("kValues").trim();
        // Découper la ligne CSV du XML
        String[] kTokens = kValuesStr.split(",");

        // Conversion en entiers
        List<Integer> kList = new ArrayList<>();
        for (String ks : kTokens) {
            kList.add(Integer.parseInt(ks.trim()));
        }

        // Buffer
        ProdConsBuffer buffer = new ProdConsBuffer(bufSz);

        List<Thread> threads = new ArrayList<>();
        Random rng = new Random();

        //---------------------------------------------------
        // Producteurs : intensité variable pour plus de concurrence
        //---------------------------------------------------
        for (int i = 0; i < nProd; i++) {

            int localMinProd = minProd + rng.nextInt(3);
            int localMaxProd = maxProd + rng.nextInt(5);
            int localProdTime = Math.max(5, prodTime + rng.nextInt(30));

            threads.add(new Producer(buffer, localMinProd, localMaxProd, localProdTime));
        }

        //---------------------------------------------------
        // Consommateurs K tirés du XML
        //---------------------------------------------------
        for (int k : kList) {
            System.out.println("Ajout consumerK avec k = " + k);
            threads.add(new ConsumerK(buffer, k, consTime));
        }

        //---------------------------------------------------
        // Mélange + démarrage entrelacé pour concurrence réaliste
        //---------------------------------------------------
        Collections.shuffle(threads);
        for (Thread t : threads) {
            t.start();
            Thread.sleep(rng.nextInt(10));   // interleaving
        }

        /*
         * >>> NOTE POUR TON RAPPORT <<<
         * - Les consommateurs K lisent désormais leur valeur K depuis le XML.
         * - Chaque ConsumerK consomme des blocs de taille K, répétés dans le temps.
         * - Le scénario inclut : 
         *       * producteurs à vitesse variable
         *       * consommateurs K variés
         *       * mélange aléatoire des threads
         *       * forte concurrence sur le buffer
         * - Aucune terminaison vérifiée (non demandée)
         */
    }
}
