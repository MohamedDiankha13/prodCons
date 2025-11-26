package prodconsV6;

import prodcons.Message;

public class MessageEx {

	public final Message content;
    public final int copies;

    public int consumed = 0;     // nb de copies déjà consommées
    public boolean productionDone = false;

    public MessageEx(Message m, int n) {
        this.content = m;
        this.copies = n;
    }
}
