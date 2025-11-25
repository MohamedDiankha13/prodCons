package prodconsV5;

import prodcons.Message;

public class ConsumerK  extends Thread{
	
	private final IProdConsBuffer buffer;
    private final int k;
    private final int consTime;

    public ConsumerK(IProdConsBuffer buffer, int k, int consTime) {
        this.buffer = buffer;
        this.k = k;
        this.consTime = consTime;
    }

    @Override
    public void run() {
        try {
            Message[] msgs = buffer.get(k);
            System.out.println("Consumer " + getId() + " consumed " + k + " messages:");
            for (Message m : msgs) {
                System.out.println("   -> " + m);
            }
            Thread.sleep(consTime);
        } catch (InterruptedException e) {
            return;
        }
    }
}
