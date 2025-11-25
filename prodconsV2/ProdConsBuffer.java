package prodconsV2;

import prodcons.IProdConsBuffer;
import prodcons.Message;

/*
 * Tous les producteurs finissent → arrêt de la production.

Tous les messages déposés finissent par être consommés.

Ensuite les consommateurs doivent détecter qu’il n’y aura plus jamais de messages → et sortir proprement.

 */
public class ProdConsBuffer implements IProdConsBuffer {

    private final Message[] buffer;
    private final int size;

    private int in = 0;
    private int out = 0;
    private int nfull = 0;
    private int totalProduced = 0;   // compteur historique
    /*
     * Il faut compter :

combien de messages chaque producteur va produire (au moment où il démarre)

additionner ces quantités dans le buffer

de sorte qu’on puisse savoir exactement combien de messages doivent exister au total
     */
    private int totalToProduce = 0;  // total final prévu
    
    /*Chaque fois qu’un consommateur fait get(), on incrémente :*/
    private int totalConsumed = 0;
    private int prodCount = 0;       // nombre total de producteurs déclarés
    private int prodFinished = 0;    // nombre de producteurs qui ont terminé

    public ProdConsBuffer(int size, int producers) {
        this.size = size;
        this.buffer = new Message[size];
        this.prodCount = producers;
    }

    // appelé par chaque producteur AVANT de produire
    public synchronized void declareProduction(int n) {
        totalToProduce += n;
    }

    // appelé par chaque producteur après avoir produit
    public synchronized void prodFinished() {
        prodFinished++;
        notifyAll();
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

        while (nfull == 0 && prodFinished < prodCount) {
            wait();
        }

        // buffer vide et plus de production possible
        if (nfull == 0 && prodFinished == prodCount) {
            return null;
        }

        Message m = buffer[out];
        buffer[out] = null;
        out = (out + 1) % size;
        nfull--;

        totalConsumed++;

        notifyAll();
        return m;
    }

    @Override
    public synchronized int nmsg() {
        return nfull;
    }

    @Override
    public synchronized int totmsg() {
        return totalProduced;
    }

    public synchronized int getTotalToProduce() {
        return totalToProduce;
    }

    public synchronized int getTotalConsumed() {
        return totalConsumed;
    }
}
