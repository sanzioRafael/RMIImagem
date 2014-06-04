package metodo;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MetodoInterface extends Remote {
	
	public String data() throws RemoteException;
	public String hora() throws RemoteException;
	public int[] tomDeCinza(int[] pixels) throws RemoteException;
	
}
