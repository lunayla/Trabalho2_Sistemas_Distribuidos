import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Entidade: ClienteLoja tem muitos Pedidos (agregacao). */
public class ClienteLoja {
    private final String nome;
    private final List<Pedido> pedidos = new ArrayList<>();

    public ClienteLoja(String nome) { this.nome = nome; }
    public String getNome() { return nome; }
    public void adicionarPedido(Pedido pedido) { pedidos.add(pedido); }
    public List<Pedido> getPedidos() { return Collections.unmodifiableList(pedidos); }
}
