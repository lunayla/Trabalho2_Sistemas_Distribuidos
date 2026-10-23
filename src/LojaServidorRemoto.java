import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/** Implementacao RMI: servidor executa as operacoes e distribui avisos. */
public class LojaServidorRemoto extends UnicastRemoteObject implements LojaRemota {
    private static final long serialVersionUID = 1L;
    private final ProdutoService produtos = new ProdutoService();
    private final PedidoService pedidos = new PedidoService();
    private final CopyOnWriteArrayList<AvisoRemoto> ouvintes = new CopyOnWriteArrayList<>();

    public LojaServidorRemoto() throws RemoteException { super(); }

    @Override
    public byte[] executar(byte[] requisicaoBytes) throws RemoteException {
        Mensagem requisicao;
        try {
            requisicao = getRequest(requisicaoBytes);
        } catch (RuntimeException e) {
            throw new RemoteException("Requisicao invalida", e);
        }
        String resposta;
        try {
            if (!"LojaVirtual".equals(requisicao.getObjectReference()))
                throw new IllegalArgumentException("Objeto remoto desconhecido.");
            Map<String, String> args = JsonSimples.ler(new String(requisicao.getArguments(), StandardCharsets.UTF_8));
            resposta = executarMetodo(requisicao.getMethodId(), args);
        } catch (RuntimeException e) {
            resposta = JsonSimples.criar("status", "ERRO", "mensagem", e.getMessage());
        }
        return sendReply(requisicao, resposta);
    }

    // Substitui a leitura de uma porta; RMI ja entregou os bytes ao metodo remoto.
    public Mensagem getRequest(byte[] requisicaoBytes) {
        Mensagem mensagem = Mensagem.desempacotar(requisicaoBytes);
        if (mensagem.getMessageType() != Mensagem.REQUISICAO)
            throw new IllegalArgumentException("A mensagem nao e uma requisicao.");
        return mensagem;
    }

    // RMI devolve automaticamente estes bytes ao cliente que fez a chamada.
    public byte[] sendReply(Mensagem requisicao, String resultadoJson) {
        return new Mensagem(Mensagem.RESPOSTA, requisicao.getRequestId(),
                requisicao.getObjectReference(), requisicao.getMethodId(),
                resultadoJson.getBytes(StandardCharsets.UTF_8)).empacotar();
    }

    private String executarMetodo(String metodo, Map<String, String> args) {
        switch (metodo) {
            case "autenticar": {
                String usuario = args.get("usuario");
                String senha = args.get("senha");
                boolean valido = ("cliente".equals(usuario) && "123".equals(senha))
                        || ("admin".equals(usuario) && "admin123".equals(senha));
                return JsonSimples.criar("status", valido ? "OK" : "ERRO",
                        "mensagem", valido ? "Login autorizado." : "Login invalido.");
            }
            case "listarProdutos": {
                StringBuilder lista = new StringBuilder();
                for (Produto p : produtos.listar()) lista.append(p).append('\n');
                return JsonSimples.criar("status", "OK", "mensagem", lista.toString().trim());
            }
            case "consultarProduto": {
                int id = inteiro(args, "produtoId");
                Produto p = produtos.buscar(id);
                if (p == null) throw new IllegalArgumentException("Produto nao encontrado.");
                return JsonSimples.criar("status", "OK", "mensagem", p.toString());
            }
            case "registrarPedido": {
                // Passagem POR VALOR: JSON vira um novo objeto Pedido local ao servidor.
                Pedido p = new Pedido(inteiro(args, "pedidoId"), texto(args, "cliente"),
                        inteiro(args, "produtoId"), inteiro(args, "quantidade"));
                String mensagem = pedidos.registrar(p, produtos.buscar(p.getProdutoId()));
                avisarTodos("NOVO PEDIDO: " + p.getId() + " registrado.");
                return JsonSimples.criar("status", "OK", "mensagem", mensagem);
            }
            case "consultarTotalPedidos":
                return JsonSimples.criar("status", "OK", "mensagem",
                        "Total de pedidos recebidos: " + pedidos.consultarTotal());
            case "enviarAviso": {
                String mensagem = texto(args, "mensagem");
                avisarTodos("AVISO DA LOJA: " + mensagem);
                return JsonSimples.criar("status", "OK", "mensagem", "Aviso enviado aos clientes.");
            }
            default:
                throw new IllegalArgumentException("Metodo remoto desconhecido: " + metodo);
        }
    }

    private static int inteiro(Map<String, String> args, String chave) {
        try { return Integer.parseInt(args.get(chave)); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Numero invalido: " + chave); }
    }
    private static String texto(Map<String, String> args, String chave) {
        String valor = args.get(chave);
        if (valor == null || valor.isBlank()) throw new IllegalArgumentException("Campo obrigatorio: " + chave);
        return valor;
    }

    @Override
    public void entrar(AvisoRemoto ouvinte) throws RemoteException {
        if (ouvinte == null) throw new RemoteException("Ouvinte nao informado.");
        ouvintes.addIfAbsent(ouvinte);
        System.out.println("Cliente entrou no grupo de avisos. Total: " + ouvintes.size());
    }

    @Override
    public void sair(AvisoRemoto ouvinte) throws RemoteException {
        ouvintes.remove(ouvinte);
        System.out.println("Cliente saiu do grupo de avisos. Total: " + ouvintes.size());
    }

    private void avisarTodos(String texto) {
        // CopyOnWriteArrayList permite multiplos clientes sem sincronizacao manual na iteracao.
        for (AvisoRemoto ouvinte : ouvintes) {
            try { ouvinte.receberAviso(texto); }
            catch (RemoteException e) {
                ouvintes.remove(ouvinte);
                System.out.println("Ouvinte desconectado removido.");
            }
        }
        System.out.println(texto);
    }
}
