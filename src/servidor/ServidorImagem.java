package servidor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Calendar;

import metodo.MetodoInterface;

public class ServidorImagem extends UnicastRemoteObject implements
		MetodoInterface {

	private static String nomeObjRemoto, enderecoIP;
	private static Registry registro;
	private static int porta;
	private static Calendar calendario;

	public ServidorImagem() throws RemoteException {
		super();
	}

	public static void main(String[] args) throws UnknownHostException {

		enderecoIP = (InetAddress.getLocalHost()).toString();
		porta = 6900;
		nomeObjRemoto = "ServidorImagem";

		System.out.println("Endereço IP: " + enderecoIP + "\nPorta: " + porta
				+ "\nNome do Objeto Remoto: " + nomeObjRemoto);

		try {
			ServidorImagem stub = new ServidorImagem();
			registro = LocateRegistry.createRegistry(porta);
			registro.rebind(nomeObjRemoto, stub);
			System.out.println("Servidor Pronto");
		} catch (Exception e) {
			System.err.println("Erro: " + e.getMessage());
		}

	}

	// Retorna a data do servidor ultilizando a classe Calendar
	@Override
	public String data() {
		calendario = Calendar.getInstance();

		String data = (calendario.get(Calendar.DAY_OF_MONTH) < 10 ? "0"
				+ calendario.get(Calendar.DAY_OF_MONTH) : calendario
				.get(Calendar.DAY_OF_MONTH))
				+ "/"
				+ (calendario.get(Calendar.MONTH) < 10 ? "0"
						+ calendario.get(Calendar.MONTH) : calendario
						.get(Calendar.MONTH))
				+ "/"
				+ calendario.get(calendario.YEAR);

		System.out.println(data);

		return data;
	}

	// Retorna a hora do servidor ultilizando a classe Calendar
	@Override
	public String hora() {
		calendario = Calendar.getInstance();

		String hora = (calendario.get(Calendar.HOUR_OF_DAY) < 10 ? "0"
				+ calendario.get(Calendar.HOUR_OF_DAY) : calendario
				.get(Calendar.HOUR_OF_DAY))
				+ ":"
				+ (calendario.get(Calendar.MINUTE) < 10 ? "0"
						+ calendario.get(Calendar.MINUTE) : calendario
						.get(Calendar.MINUTE));

		System.out.println(hora);

		return hora;
	}

	// Converte os pixels da imagem para tons de cinza
	@Override
	public int[] tomDeCinza(int[] imagem) {

		int[] imagemCinza = new int[imagem.length];

		for (int i = 0; i < imagemCinza.length; i++) {
			// Extrai informação RGBA em valor inteiro
			int alpha = (imagem[i] >> 24) & 0xff;
			int vermelho = (imagem[i] >> 16) & 0xff;
			int verde = (imagem[i] >> 8) & 0xff;
			int azul = (imagem[i]) & 0xff;

			// Media dos valores de RGB, para transformalos na escala do cinza
			int media = (vermelho + verde + azul) / 3;
			// Converte informação de RGB para int
			int cinza = ((0 << 24) & 0xFF000000) | ((media << 16) & 0x00FF0000)
					| ((media << 8) & 0x0000FF00) | (media & 0x000000FF);

			if (!(i > (imagemCinza.length / 2)))
				imagemCinza[i] = cinza;
			else
				imagemCinza[i] = imagemCinza[i];

		}

		System.out.println("Imagem alterada com sucesso");

		return imagemCinza;

	}

}
