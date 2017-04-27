package br.com.alura.loja;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.thoughtworks.xstream.XStream;

import br.com.alura.loja.modelo.Carrinho;
import br.com.alura.loja.modelo.Produto;

public class ClienteTest {
	
	private HttpServer server;
	private Client client;

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
		client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:8080/");
		String conteudo = target.path("/carrinhos/1").request().get(String.class);
		Carrinho carrinho = ((Carrinho)new XStream().fromXML(conteudo));
		
		Assert.assertEquals("Rua Vergueiro 3185, 8 andar", carrinho.getRua());
	}
	
	@Test
	public void TestaInclusao() {
		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("http://localhost:8080/");
		Carrinho carrinho = new Carrinho();
		carrinho.adiciona(new Produto(32L, "Tablet", 250, 10));
		carrinho.setRua("Rua Riachuelo, 83");
		carrinho.setCidade("Barueri");
		String xml = carrinho.toXml();
		
		Entity<String> entity = Entity.entity(xml, MediaType.APPLICATION_XML);
		
		Response response = target.path("/carrinhos").request().post(entity);
		Assert.assertEquals(201, response.getStatus());
		String location = response.getHeaderString("location");
		String conteudo = client.target(location).request().get(String.class);
		Assert.assertTrue(conteudo.contains("Tablet"));
	}
}
