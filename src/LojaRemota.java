import java.rmi.Remote;
import java.rmi.RemoteException;

/** Interface do objeto remoto, obtido por referencia via registro RMI. */
public interface LojaRemota extends Remote {
    byte[] executar(byte[] requisicao) throws RemoteException;
    void entrar(AvisoRemoto ouvinte) throws RemoteException;
    void sair(AvisoRemoto ouvinte) throws RemoteException;
}
