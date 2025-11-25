package prodconsV4;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import prodcons.IProdConsBuffer;
import prodcons.Message;


/*
 * La classe ReentrantLock est une alternative explicite à synchronized.
 * condition "notEmpty"

→ Un consommateur attend ici quand buffer vide

  *condition "notFull"

→ Un producteur attend ici quand buffer plein

Chaque condition possède sa file d’attente indépendante.
condition "notEmpty" → Un consommateur attend ici quand buffer vide
condition "notFull" → Un producteur attend ici quand buffer plein


 */
public class ProdConsBuffer implements IProdConsBuffer {

    private final Message[] buffer;
    private final int size;

    private int in = 0;
    private int out = 0;
    private int nfull = 0;

    private int totalProduced = 0;

    // Verrou explicite
    private final Lock lock = new ReentrantLock();

    // Deux files d’attente conditionnelles
    private final Condition notEmpty = lock.newCondition(); // consommateurs
    private final Condition notFull  = lock.newCondition(); // producteurs


    public ProdConsBuffer(int size) {
        this.size = size;
        this.buffer = new Message[size];
    }

    @Override
    public void put(Message m) throws InterruptedException {
    	
    	//Entrée en zone critique. À partir de là, aucun autre thread ne peut exécuter put/get.
        lock.lock();
        
        try {
            // Tant que buffer plein → attendre
            while (nfull == size) {
                notFull.await();
            }

            buffer[in] = m;
            in = (in + 1) % size;
            nfull++;
            totalProduced++;

            // Un message est arrivé → réveiller un consommateur
            notEmpty.signal();

        } finally {
            lock.unlock();
        }
    }

    @Override
    public Message get() throws InterruptedException {
        lock.lock();
        try {
            // Tant que buffer vide → attendre
            while (nfull == 0) {
                notEmpty.await();
            }

            Message m = buffer[out];
            buffer[out] = null;   // optionnel
            out = (out + 1) % size;
            nfull--;

            // Une place s'est libérée → réveiller un producteur
            notFull.signal();

            return m;

        } finally {
            lock.unlock();
        }
    }

    @Override
    public int nmsg() {
        lock.lock();
        try {
            return nfull;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int totmsg() {
        lock.lock();
        try {
            return totalProduced;
        } finally {
            lock.unlock();
        }
    }
}

