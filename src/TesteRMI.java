import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Demonstra requisicao/resposta, passagem por referencia e avisos para dois ouvintes. */
public class TesteRMI {
    public static class OuvinteTeste extends UnicastRemoteObject implements AvisoRemoto {
        private static final long serialVersionUID = 1L;
        private final List<String> avisos = new ArrayList<>();
        protected OuvinteTeste() throws RemoteException { super(); }
        @Override public synchronized void receberAviso(String aviso) { avisos.add(aviso); }
        public synchronized int quantidade() { return avisos.size(); }
    }

    public static void main(String[] args) throws Exception {
        Registry registro = LocateRegistry.getRegistry("127.0.0.1", 1099);
        LojaRemota loja = (LojaRemota) registro.lookup("Loja");
        Comunicacao rpc = new Comunicacao(loja);
        OuvinteTeste a = new OuvinteTeste();
        OuvinteTeste b = new OuvinteTeste();
        try {
            loja.entrar(a);
            loja.entrar(b);
            verificar(rpc, "listarProdutos", "{}", "Notebook");
            verificar(rpc, "consultarProduto", JsonSimples.criar("produtoId", 2), "Mouse");
            String registroPedido = rpc.chamar("registrarPedido", JsonSimples.criar(
                    "pedidoId", java.util.concurrent.ThreadLocalRandom.current().nextInt(100000, 999999), "cliente", "cliente", "produtoId", 1, "quantidade", 2));
            if (!"OK".equals(JsonSimples.ler(registroPedido).get("status")))
                throw new AssertionError("Registro falhou: " + registroPedido);
            verificar(rpc, "consultarTotalPedidos", "{}", "Total de pedidos");
            verificar(rpc, "enviarAviso", JsonSimples.criar("mensagem", "Promocao hoje"), "Aviso enviado");
            if (a.quantidade() < 2 || b.quantidade() < 2)
                throw new AssertionError("Notificacoes nao chegaram aos clientes.");
            System.out.println("TESTE OK: chamadas remotas e avisos entregues aos clientes.");
        } finally {
            loja.sair(a);
            loja.sair(b);
            UnicastRemoteObject.unexportObject(a, true);
            UnicastRemoteObject.unexportObject(b, true);
        }
    }

    private static void verificar(Comunicacao rpc, String metodo, String argumentos, String trecho) throws Exception {
        Map<String, String> retorno = JsonSimples.ler(rpc.chamar(metodo, argumentos));
        if (!"OK".equals(retorno.get("status")) || !retorno.get("mensagem").contains(trecho))
            throw new AssertionError("Falhou " + metodo + ": " + retorno);
        System.out.println("OK: " + metodo);
    }
}
