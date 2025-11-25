package prodcons;

public class Message {

    private final int idProd;
    private final int num;

    public Message(int idProd, int num) {
        this.idProd = idProd;
        this.num = num;
    }

    public int getProducerId() {
        return idProd;
    }

    public int getNumber() {
        return num;
    }

    @Override
    public String toString() {
        return "[Msg from Prod " + idProd + " : " + num + "]";
    }
}
