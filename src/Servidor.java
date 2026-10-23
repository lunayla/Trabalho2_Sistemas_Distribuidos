import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/** Inicializa o registro RMI e publica o servico remoto da loja. */
public class Servidor {
    public static void main(String[] args) {
        try {
            Registry registro = LocateRegistry.createRegistry(1099);
            registro.rebind("Loja", new LojaServidorRemoto());
            System.out.println("Servidor da loja iniciado.");
        } catch (Exception e) {
            System.err.println("Erro ao iniciar RMI: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
