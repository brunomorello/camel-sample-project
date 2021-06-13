package br.com.caelum.camel.exercise;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.xstream.XStreamDataFormat;
import org.apache.camel.impl.DefaultCamelContext;

import com.thoughtworks.xstream.XStream;

import br.com.caelum.camel.exercise.model.Negociacao;

public class NegotiationsIntegration {

	public static void main(String[] args) throws Exception {
		
		DefaultCamelContext context = new DefaultCamelContext();
		
		context.addRoutes(new RouteBuilder() {
			
			@Override
			public void configure() throws Exception {
				final XStream xtream = new XStream();
				xtream.alias("negociacao", Negociacao.class);
						
				from("timer://negociacoes?fixedRate=true&delay=1s&period=360s")
					.to("http4://argentumws-spring.herokuapp.com/negociacoes")
					.convertBodyTo(String.class)
					.unmarshal(new XStreamDataFormat(xtream))
					.split(body())
					.log("Camel working on ${body}")
					.end();
			}
			
		});
		
		context.start();
		Thread.sleep(5_000);
		context.stop();
	}
}
