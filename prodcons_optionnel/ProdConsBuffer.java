package prodcons_optionnel;

import prodcons.Message;

/*
 * Le producteur peut déposer n exemplaires d’un même message.

Le message ne disparaît du buffer que lorsque les n consommateurs l’ont consommé.

Un producteur est bloqué tant que ses n exemplaires n’ont pas été tous consommés.

Tout consommateur qui consomme une copie est également bloqué jusqu’à ce que toutes les copies aient été consommées.

Le dernier consommateur à consommer déclenche la libération du producteur et des autres consommateurs bloqués.
 */
public class ProdConsBuffer implements IProdConsBuffer {

	private final MessageEx[] buffer;
    private final int size;

    private int in = 0;
    private int out = 0;
    private int nfull = 0;

    public ProdConsBuffer(int size) {
        this.size = size;
        this.buffer = new MessageEx[size];
    }

    @Override
    public synchronized void put(Message m, int n) throws InterruptedException {

        // 1. Attendre qu’une place soit libre
        while (nfull == size) {
            wait();
        }

        // 2. Créer le message "synchronisé"
        MessageEx mex = new MessageEx(m, n);

        // 3. Stocker une SEULE entrée dans le buffer
        buffer[in] = mex;
        in = (in + 1) % size;
        nfull++;

        // 4. Réveiller un consommateur
        notifyAll();

        // 5. Le producteur attend que toutes les copies soient consommées
        while (!mex.productionDone) {
            wait();
        }
    }

    @Override
    public synchronized Message get() throws InterruptedException {

        while (nfull == 0) {
            wait();
        }

        // MessageEx courant
        MessageEx mex = buffer[out];

        // Le consommateur consomme UNE copie
        mex.consumed++;

        Message content = mex.content;

        // Si ce consommateur n’est pas le dernier
        if (mex.consumed < mex.copies) {

            // Attendre la fin de la vague
            while (mex.consumed < mex.copies) {
                wait();
            }

            // Tous les consommateurs sont réveillés
            return content;
        }

        // === Ici : ce consommateur est le DERNIER ===

        // 1. Informer le producteur
        mex.productionDone = true;

        // 2. Réveiller producteur + consommateurs en attente
        notifyAll();

        // 3. Retirer le message du buffer
        buffer[out] = null;
        out = (out + 1) % size;
        nfull--;

        // 4. Réveiller les producteurs en attente
        notifyAll();

        return content;
    }
}
