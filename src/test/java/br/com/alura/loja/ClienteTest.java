package br.com.alura.loja;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.xstream.XStream;

import br.com.alura.loja.modelo.Carrinho;

public class ClienteTest {
	
	private HttpServer server;

	@Before
	public void iniciaServidor() {
		server = Servidor.inicializarServidor();
	}
	
	@After
	public void pararServidor() {
		server.stop();
	}

	@Test
	public void TestaConexao() {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:8080/");
		String conteudo = target.path("/carrinhos").request().get(String.class);
//		System.out.println(conteudo);
		Carrinho carrinho = ((Carrinho)new XStream().fromXML(conteudo));
		
		Assert.assertEquals("Rua Vergueiro 3185, 8 andar", carrinho.getRua());
	}
}
