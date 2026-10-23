import java.nio.charset.StandardCharsets;
import java.util.Map;

/** Envelope JSON: messageType, requestId, objectReference, methodId, arguments. */
public class Mensagem {
    public static final int REQUISICAO = 0;
    public static final int RESPOSTA = 1;

    private final int messageType;
    private final int requestId;
    private final String objectReference;
    private final String methodId;
    private final byte[] arguments;

    public Mensagem(int tipo, int id, String objeto, String metodo, byte[] argumentos) {
        this.messageType = tipo;
        this.requestId = id;
        this.objectReference = objeto;
        this.methodId = metodo;
        this.arguments = argumentos.clone();
    }
    public int getMessageType() { return messageType; }
    public int getRequestId() { return requestId; }
    public String getObjectReference() { return objectReference; }
    public String getMethodId() { return methodId; }
    public byte[] getArguments() { return arguments.clone(); }

    public byte[] empacotar() {
        String json = JsonSimples.criar(
                "messageType", messageType,
                "requestId", requestId,
                "objectReference", objectReference,
                "methodId", methodId,
                "arguments", new String(arguments, StandardCharsets.UTF_8));
        return json.getBytes(StandardCharsets.UTF_8);
    }

    public static Mensagem desempacotar(byte[] bytes) {
        Map<String, String> campos = JsonSimples.ler(new String(bytes, StandardCharsets.UTF_8));
        return new Mensagem(
                Integer.parseInt(campos.get("messageType")),
                Integer.parseInt(campos.get("requestId")),
                campos.get("objectReference"),
                campos.get("methodId"),
                campos.get("arguments").getBytes(StandardCharsets.UTF_8));
    }
}
