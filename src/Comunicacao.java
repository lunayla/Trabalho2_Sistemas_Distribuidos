import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.util.concurrent.atomic.AtomicInteger;

/** Cliente do protocolo requisicao-resposta, usando Java RMI internamente. */
public class Comunicacao {
    private final LojaRemota loja;
    private final AtomicInteger proximoId = new AtomicInteger(1);

    public Comunicacao(LojaRemota loja) { this.loja = loja; }

    // Metodo do protocolo pedido pelo enunciado; argumentos e resposta sao bytes JSON.
    public byte[] doOperation(String objectReference, String methodId, byte[] arguments)
            throws RemoteException {
        int id = proximoId.getAndIncrement();
        Mensagem requisicao = new Mensagem(Mensagem.REQUISICAO, id, objectReference, methodId, arguments);
        Mensagem resposta = Mensagem.desempacotar(loja.executar(requisicao.empacotar()));
        if (resposta.getMessageType() != Mensagem.RESPOSTA || resposta.getRequestId() != id)
            throw new RemoteException("Resposta com tipo ou requestId incorreto.");
        return resposta.getArguments();
    }

    public String chamar(String metodo, String argumentosJson) throws RemoteException {
        byte[] retorno = doOperation("LojaVirtual", metodo, argumentosJson.getBytes(StandardCharsets.UTF_8));
        return new String(retorno, StandardCharsets.UTF_8);
    }
}
