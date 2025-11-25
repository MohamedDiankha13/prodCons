package prodcons;

/*
 * Nous devons utiliser :

Un tableau Message[] buffer de taille bufSz.
Deux index circulaires :
   in (prochaine case où mettre un message)
   out (prochaine case à retirer)
Deux compteurs :
   nfull = nombre de cases occupées
   nempty = nombre de cases libres = bufSz − nfull
Synchronisation (wait/notify) :
	Pour put(m) :
		Garde : nempty > 0
	    Sinon : wait()
       → puis insert, update index, update compteurs, notifyAll()
       
	Pour get() :
		Garde : nfull > 0
		Sinon : wait()
		→ puis retirer, update index, update compteurs, notifyAll()
 */

public class ProdConsBuffer  implements IProdConsBuffer {

    private final Message[] buffer;
    private final int size;

    private int in = 0;       // index circulaire insertion
    private int out = 0;      // index circulaire retrait
    private int nfull = 0;    // nb cases occupées
    private int total = 0;    // nb total de messages produits

    public ProdConsBuffer(int size) {
        this.size = size;
        this.buffer = new Message[size];
    }

    @Override
    public synchronized void put(Message m) throws InterruptedException {
        while (nfull == size) {
            wait();                 // buffer plein, attendre
        }

        buffer[in] = m;             // insertion
        in = (in + 1) % size;       // index circulaire
        nfull++;                    // 1 élément de plus
        total++;                    // message historisé

        notifyAll();                // réveiller consommateurs
    }

    @Override
    public synchronized Message get() throws InterruptedException {
        while (nfull == 0) {
            wait();                 // buffer vide, attendre
        }
        Message m = buffer[out];
        buffer[out] = null;         // optionnel
        out = (out + 1) % size;
        nfull--;                    // 1 élément de moins
        notifyAll();                // réveiller producteurs

        return m;
    }

    @Override
    public synchronized int nmsg() {
        return nfull;
    }

    @Override
    public synchronized int totmsg() {
        return total;
    }
    
/*
 * ynchronized

Verrouille l’objet buffer → section critique → accès exclusif.

while (nfull == size) wait();

Obligation de vérifier dans un while (pas un if) car un spurious wakeup peut arriver.

notifyAll();

Obligatoire en MPMC (sinon deadlocks).

in = (in + 1) % size

Permet au buffer d’être circulaire (évite de décaler les cases).
 */
}
