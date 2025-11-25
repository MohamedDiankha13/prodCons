package prodcons_optionnel;

import prodcons.Message;


public interface IProdConsBuffer {

	void put(Message m, int n) throws InterruptedException;
    Message get() throws InterruptedException;
}
