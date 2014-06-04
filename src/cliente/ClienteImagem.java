package cliente;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialogs;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

import metodo.MetodoInterface;

public class ClienteImagem extends Application implements Initializable,
		Serializable {

	// Atributos responsaveis pela manipulação das ações do cliente
	private AnchorPane painel;
	private Scene cena;
	private Stage stage;
	private String url;
	private MetodoInterface stub;
	private FileChooser fileChooser;
	private File arquivo;
	private Image image;
	private static String nomeObjRemoto, enderecoIP;
	private static Registry registro;
	private static int porta;
	private BufferedImage entrada;
	// Componentes da Janela
	@FXML
	private TextField fieldURL, fieldPorta, fieldImagem;
	@FXML
	private Button btnConectar, btnDesconectar, btnAbrir, btnCinza;
	@FXML
	private Label lbServidor, lbData, lbHora;
	@FXML
	private ImageView imgView;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		fieldImagem.setEditable(false);

		btnConectar.setDisable(false);
		btnDesconectar.setDisable(true);
		btnAbrir.setDisable(true);
		btnCinza.setDisable(true);
		lbServidor.setTextFill(Color.web("#FF0000"));

	}

	@Override
	public void start(Stage stage) throws Exception {

		painel = FXMLLoader.load(ClienteImagem.class
				.getResource("ImagemView.fxml"));
		painel.setId("painel");

		cena = new Scene(painel);

		stage.setScene(cena);
		stage.getIcons().add(new Image("img/Images-icon.png"));
		stage.setTitle("Manipulando Imagem");
		stage.show();
		stage.setResizable(false);

		this.stage = stage;

	}

	public static void main(String[] args) {
		launch(args);
	}

	@FXML
	void btnConectarOnAction(ActionEvent event) {
		if (!fieldURL.getText().isEmpty() && !fieldPorta.getText().isEmpty()) {

			nomeObjRemoto = "ServidorImagem";
			enderecoIP = fieldURL.getText();
			porta = Integer.parseInt(fieldPorta.getText());
			MetodoInterface stub = null;

			try {
				registro = LocateRegistry.getRegistry(enderecoIP, porta);
				stub = (MetodoInterface) registro.lookup(nomeObjRemoto);
				conectar();
				lbData.setText(lbData.getText() + " " + stub.data());
				lbHora.setText(lbHora.getText() + " " + stub.hora());
			} catch (RemoteException e) {
				Dialogs.showErrorDialog(stage, "Exceção encontrada",
						"Erro em encontrar o servidor", "Erro", e);
			} catch (NotBoundException e) {
				Dialogs.showErrorDialog(stage, "Exceção encontrada",
						"Erro na interface", "Erro", e);
			}

		} else
			Dialogs.showWarningDialog(stage, "Preencha os campos em branco!",
					"Campos em branco!", "Atenção");

	}

	@FXML
	void btnDesconectarOnAction(ActionEvent event) {
		desconectar();
	}

	@FXML
	void btnAbrirOnAction(ActionEvent event) throws Exception {

		fileChooser = new FileChooser();

		if ((arquivo = fileChooser.showOpenDialog(stage)) != null) {

			url = arquivo.toURI().toURL().toString();

			fieldImagem.setText(arquivo.getAbsolutePath());

			image = new Image(url);
			imgView.setImage(image);

		} else
			Dialogs.showWarningDialog(stage,
					"Escolha um arquivo, pois é preciso", "Escolha o arquivo",
					"Atenção");

	}

	@FXML
	void btnCinzaOnAction(ActionEvent event) {
		try {
			if ((entrada = ImageIO.read(arquivo)) != null) {

				registro = LocateRegistry.getRegistry(enderecoIP, porta);
				stub = (MetodoInterface) registro.lookup(nomeObjRemoto);

				int largura = entrada.getWidth();
				int altura = entrada.getHeight();
				int[] pixels = entrada.getRGB(0, 0, largura, altura, null, 0,
						largura);

				pixels = stub.tomDeCinza(pixels);
				entrada.setRGB(0, 0, largura, altura, pixels, 0, largura);

				ImageIO.write(entrada, "png", arquivo.getAbsoluteFile());

				image = new Image(url);
				imgView.setImage(image);

			} else
				Dialogs.showWarningDialog(stage, "Escolha uma imagem",
						"Imagem", "Atenção");
		} catch (IOException e) {
			Dialogs.showErrorDialog(stage, "Exceção encontrada",
					"Erro em receber o arquivo no Buffer", "Erro", e);
		} catch (NotBoundException e) {
			Dialogs.showErrorDialog(stage, "Exceção encontrada",
					"Erro em receber Identificar o Metodo", "Erro", e);
		} catch (IllegalArgumentException e) {
			Dialogs.showErrorDialog(stage, "Exceção encontrada",
					"Nenhum arquivo foi selecionado, por favor escolha um",
					"Erro", e);
		}
	}

	private void conectar() {

		btnConectar.setDisable(true);
		btnDesconectar.setDisable(false);
		btnAbrir.setDisable(false);
		btnCinza.setDisable(false);

		lbServidor.setTextFill(Color.web("#00FF00"));
		lbServidor.setText("Conectado");

		fieldImagem.setText("");

		Dialogs.showWarningDialog(stage, "Conexão estabelecida com sucesso!!!",
				"Servidor", "Atenção");
	}

	private void desconectar() {

		btnConectar.setDisable(false);
		btnDesconectar.setDisable(true);
		btnAbrir.setDisable(true);
		btnCinza.setDisable(true);

		lbServidor.setTextFill(Color.web("#FF0000"));
		lbServidor.setText("Desconectado");
		lbData.setText("Data:");
		lbHora.setText("Hora:");

		fieldURL.setText("");
		fieldPorta.setText("");
		fieldImagem.setText("");

		imgView.setImage(null);

		Dialogs.showWarningDialog(stage, "Desconectado com sucesso!!!",
				"Servidor", "Atenção");

	}

}
