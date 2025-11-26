package prodconsV5;

import prodcons.Message;

public interface IProdConsBuffer {

	/**
     * Place le message dans le buffer
     **/
	
	public void put(Message m) throws InterruptedException;
    
	/**
     * Récupère un message du buffer, en suivant l'ordre FIFO
     **/
	public Message get() throws InterruptedException;
    
	/**
     * Récupère k messages consécutifs du buffer.
     */
	public Message[] get(int k) throws InterruptedException;

	/**
     * Retourne le nombre de messages disponibles dans le buffer
     **/
    public int nmsg();
    
    /**
     * Retourne le nombre total de messages placés dans le buffer depuis sa création
     **/
    public int totmsg();
}
