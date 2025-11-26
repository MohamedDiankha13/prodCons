package prodconsV6;

import java.util.Random;

import prodcons.Message;

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
                Thread.sleep(prodTime);

                Message m = new Message((int)getId(), i);

                int copies = rand.nextInt(4) + 2;  // par exemple 2..5

                buffer.put(m, copies);

                System.out.println("Producer " + getId() +
                    " produced " + copies + " copies of " + m);

            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
