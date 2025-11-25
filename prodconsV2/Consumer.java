package prodconsV2;

import prodcons.Message;

/*
 Comment un consommateur sait-il qu’il doit s’arrêter ?

Il peut se retrouver dans plusieurs cas :

Le buffer est temporairement vide, mais des producteurs existent encore
→ Il doit attendre.

Le buffer est vide et tous les producteurs ont fini
→ Il doit quitter la boucle.

Donc il faut un signaling global pour exprimer :
« Plus aucun message n’arrivera ».

 */
public class Consumer extends Thread {

	private final ProdConsBuffer buffer;
    private final int consTime;

    public Consumer(ProdConsBuffer buffer, int consTime) {
        this.buffer = buffer;
        this.consTime = consTime;
    }

    @Override
    public void run() {

        while (true) {
            try {
                Message m = buffer.get();

                if (m == null) {
                    // terminaison automatique
                    System.out.println("Consumer " + getId() + " terminating.");
                    return;
                }

                Thread.sleep(consTime);
                System.out.println("Consumer " + getId() + " consumed " + m);

            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
