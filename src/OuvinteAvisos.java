import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/** Referencia remota passada pelo cliente ao servidor para receber avisos. */
public class OuvinteAvisos extends UnicastRemoteObject implements AvisoRemoto {
    private static final long serialVersionUID = 1L;
    public OuvinteAvisos() throws RemoteException { super(); }
    @Override
    public void receberAviso(String texto) throws RemoteException {
        System.out.println("\n[NOTIFICACAO] " + texto);
        System.out.print("Opcao: ");
    }
}
