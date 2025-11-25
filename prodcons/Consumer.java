package prodcons;

public class Consumer extends Thread {

    private final IProdConsBuffer buffer;
    private final int consTime;

    public Consumer(IProdConsBuffer buffer, int consTime) {
        this.buffer = buffer;
        this.consTime = consTime;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Message m = buffer.get();
                Thread.sleep(consTime);
                System.out.println("Consumer " + getId() + " consumed " + m);

            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
