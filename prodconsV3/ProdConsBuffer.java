package prodconsV3;

import prodcons.IProdConsBuffer;
import prodcons.Message;
import java.util.concurrent.Semaphore;


/*
 * 1. empty
Nombre de places libres dans le buffer
Initialisé à bufSz
→ Un producteur doit faire : empty.acquire()

2. full
Nombre de cases occupées
Initialisé à 0
→ Un consommateur doit faire : full.acquire()

3. mutex
Un verrou binaire (1 seul détenteur)
→ Protège l’accès à la section critique : insertion/retrait dans le tableau.

Sémaphores réduisent les réveils inutiles, contrairement à notifyAll()

 */
public class ProdConsBuffer implements IProdConsBuffer {

	private final Message[] buffer;
	private final int size;

	private int in = 0;
	private int out = 0;

	private int totalProduced = 0;  // statistique pour totmsg()

	// Sémaphores principaux
	private final Semaphore empty;   // places libres
	private final Semaphore full;    // places occupées
	private final Semaphore mutex;   // exclusion mutuelle


	public ProdConsBuffer(int size) {
		this.size = size;
		this.buffer = new Message[size];

		this.empty = new Semaphore(size);  // bufSz places libres au début
		this.full = new Semaphore(0);      // aucune case pleine au début

		this.mutex = new Semaphore(1);     // section critique binaire
	}

	/*
	 * Producteur :

empty.acquire() → attend s’il n’y a plus de place

mutex.acquire() → entre en zone critique

écrit dans buffer

mutex.release()

full.release() → signale qu’un nouveau message est disponible
	 */
	@Override
	public void put(Message m) throws InterruptedException {

		empty.acquire();        // attendre une place libre
		mutex.acquire();        // entrer en section critique

		buffer[in] = m;
		in = (in + 1) % size;
		totalProduced++;

		mutex.release();        // quitter section critique
		full.release();         // signaler qu’un message est disponible
	}

	
	/*
	 * Consommateur :

full.acquire() → attend si aucun message

mutex.acquire()

lit dans buffer

mutex.release()

empty.release() → signale qu’une place s’est libérée
	 */
	@Override
	public Message get() throws InterruptedException {
		full.acquire();         // attendre qu’un message existe
		mutex.acquire();        // entrer en section critique
		Message m = buffer[out];
		buffer[out] = null;     // optionnel
		out = (out + 1) % size;
		mutex.release();
		empty.release();        // signaler qu’une place s'est libérée
		return m;
	}

	@Override
	public int nmsg() {
		return full.availablePermits();    // nombre de cases pleines
	}

	@Override
	public int totmsg() {
		return totalProduced;
	}
}
