package br.com.alura.loja;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
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
	private WebTarget target;

	@Before
	public void iniciaServidor() {
		this.server = Servidor.inicializarServidor();
		ClientConfig config = new ClientConfig();
		config.register(new LoggingFilter());
		this.client = ClientBuilder.newClient(config);
		this.target = client.target("http://localhost:8080/");
	}
	
	@After
	public void pararServidor() {
		this.server.stop();
	}

	@Test
	public void testaConexao() {
		String conteudo = target.path("/carrinhos/1").request().get(String.class);
		Carrinho carrinho = ((Carrinho)new XStream().fromXML(conteudo));
		
		Assert.assertEquals("Rua Vergueiro 3185, 8 andar", carrinho.getRua());
	}
	
	@Test
	public void testaInclusao() {
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
	
	@Test
	public void testaRemocaoProduto() {
		Response response = target.path("/carrinhos/1/produtos/6237").request().delete();
		
		Assert.assertEquals(200, response.getStatus());
		String conteudo = target.path("/carrinhos/1").request().get(String.class);
		
		Assert.assertFalse(conteudo.contains("6237"));
		
	}
	
	@Test
	public void testaAlteracaoProduto() {
		Produto produto = new Produto(3467, "Jogo de esporte", 60, 10);
		String xml = new XStream().toXML(produto);
		Entity<String> entity = Entity.entity(xml, MediaType.APPLICATION_XML);
		Response response = target.path("/carrinhos/1/produtos/3467").request().put(entity);
		Assert.assertEquals(200, response.getStatus());
		
		String conteudo = target.path("/carrinhos/1").request().get(String.class);
		Carrinho carrinho = (Carrinho)new XStream().fromXML(conteudo);
		List<Produto> produtos = carrinho.getProdutos();
		for (Produto produto2 : produtos) {
			if(produto.getId() == 3467) {
				Assert.assertEquals(10, produto2.getQuantidade());
				
			}
		}
		
	}
	
	
}
