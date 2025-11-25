package prodconsV5;


import prodcons.Message;

/*
 * Cas 1 : K plus petit que size (simple)

Buffer size = 5
Messages présents = 4
Consommateur demande K=3 → il peut retirer immédiatement.

Cas 2 : K > size

Buffer size = 5
Consommateur demande K=20

Il doit attendre jusqu’à :

Producteurs remplissent

Consommateurs (autres) ne consomment pas trop

Ou K diminue

Quand nfull atteint 20 → retrait en bloc.

Cas 3 : Plusieurs consommateurs avec différents K

Exemple :

C1 → get(1)

C2 → get(10)

C3 → get(2)

La file d’attente reflète la logique :
Chaque thread attend sa propre condition nfull >= k.

 */
public class ProdConsBuffer implements IProdConsBuffer {

    private final Message[] buffer;
    private final int size;

    private int in = 0;
    private int out = 0;
    private int nfull = 0;

    private int totalProduced = 0;

    public ProdConsBuffer(int size) {
        this.size = size;
        this.buffer = new Message[size];
    }

    @Override
    public synchronized void put(Message m) throws InterruptedException {
        while (nfull == size) {
            wait();
        }

        buffer[in] = m;
        in = (in + 1) % size;
        nfull++;
        totalProduced++;

        notifyAll();
    }

    @Override
    public synchronized Message get() throws InterruptedException {
        while (nfull == 0) {
            wait();
        }

        Message m = buffer[out];
        buffer[out] = null;
        out = (out + 1) % size;
        nfull--;

        notifyAll();
        return m;
    }

    @Override
    public synchronized Message[] get(int k) throws InterruptedException {

        // attendre que k messages soient disponibles
        while (nfull < k) {
            wait();
        }

        Message[] msgs = new Message[k];

        for (int i = 0; i < k; i++) {
            msgs[i] = buffer[out];
            buffer[out] = null;
            out = (out + 1) % size;
            nfull--;
        }

        // réveiller les producteurs qui attendaient de la place
        notifyAll();

        return msgs;
    }

    @Override
    public synchronized int nmsg() {
        return nfull;
    }

    @Override
    public synchronized int totmsg() {
        return totalProduced;
    }
}
