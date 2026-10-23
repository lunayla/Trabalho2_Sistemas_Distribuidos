/** Entidade que representa um pedido. */
public class Pedido {
    private final int id;
    private final String cliente;
    private final int produtoId;
    private final int quantidade;

    public Pedido(int id, String cliente, int produtoId, int quantidade) {
        this.id = id;
        this.cliente = cliente;
        this.produtoId = produtoId;
        this.quantidade = quantidade;
    }
    public int getId() { return id; }
    public String getCliente() { return cliente; }
    public int getProdutoId() { return produtoId; }
    public int getQuantidade() { return quantidade; }
}
