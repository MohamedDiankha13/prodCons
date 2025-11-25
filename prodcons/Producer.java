package prodcons;

import java.util.Random;

public class Producer extends Thread {

    private final IProdConsBuffer buffer;
    private final int minProd, maxProd, prodTime;
    private final Random rand = new Random();

    public Producer(IProdConsBuffer buffer, int minProd, int maxProd, int prodTime) {
        this.buffer = buffer;
        this.minProd = minProd;
        this.maxProd = maxProd;
        this.prodTime = prodTime;
    }

    @Override
    public void run() {
        int nbMsg = rand.nextInt(maxProd - minProd + 1) + minProd;

        for (int i = 1; i <= nbMsg; i++) {
            try {
                Thread.sleep(prodTime);  // simule production
                Message m = new Message((int) getId(), i);
                buffer.put(m);
                System.out.println("Producer " + getId() + " produced " + m);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
}
