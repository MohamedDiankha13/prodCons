package prodcons_optionnel;

import java.util.Properties;



public class TestProdCons {

	public static void main(String[] args) throws Exception {

        Properties p = new Properties();
        p.loadFromXML(TestProdCons.class.getClassLoader().getResourceAsStream("options.xml"));

        int nProd = Integer.parseInt(p.getProperty("nProd"));
        int nCons = Integer.parseInt(p.getProperty("nCons"));
        int bufSz = Integer.parseInt(p.getProperty("bufSz"));
        int prodTime = Integer.parseInt(p.getProperty("prodTime"));
        int consTime = Integer.parseInt(p.getProperty("consTime"));
        int minProd = Integer.parseInt(p.getProperty("minProd"));
        int maxProd = Integer.parseInt(p.getProperty("maxProd"));

        ProdConsBuffer buffer = new ProdConsBuffer(bufSz);
        for (int i = 0; i < nProd; i++)
            new Producer(buffer, minProd, maxProd, prodTime).start();
        for (int i = 0; i < nCons; i++)
            new Consumer(buffer, consTime).start();
    }
}
