import java.rmi.Remote;
import java.rmi.RemoteException;

/** Callback remoto: o servidor envia avisos para cada cliente inscrito. */
public interface AvisoRemoto extends Remote {
    void receberAviso(String texto) throws RemoteException;
}
