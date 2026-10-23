import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/** Registra pedidos no servidor, evitando IDs repetidos. */
public class PedidoService {
    private final Map<String, ClienteLoja> clientes = new HashMap<>();
    private final Set<Integer> ids = new HashSet<>();
    private int total = 0;

    public synchronized String registrar(Pedido pedido, Produto produto) {
        if (pedido.getId() <= 0 || pedido.getQuantidade() <= 0)
            throw new IllegalArgumentException("ID e quantidade devem ser maiores que zero.");
        if (produto == null) throw new IllegalArgumentException("Produto nao encontrado.");
        if (!ids.add(pedido.getId())) throw new IllegalArgumentException("ID de pedido ja cadastrado.");
        ClienteLoja cliente = clientes.computeIfAbsent(pedido.getCliente(), ClienteLoja::new);
        cliente.adicionarPedido(pedido);
        total++;
        double valor = pedido.getQuantidade() * produto.getPreco();
        return "Pedido " + pedido.getId() + " registrado para " + cliente.getNome()
                + ". Total: R$ " + String.format("%.2f", valor);
    }

    public synchronized int consultarTotal() { return total; }
}
