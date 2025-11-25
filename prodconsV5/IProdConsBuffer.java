package prodconsV5;

import prodcons.Message;

public interface IProdConsBuffer {

	public void put(Message m) throws InterruptedException;
    public Message get() throws InterruptedException;
    public Message[] get(int k) throws InterruptedException;

    public int nmsg();
    public int totmsg();
}
