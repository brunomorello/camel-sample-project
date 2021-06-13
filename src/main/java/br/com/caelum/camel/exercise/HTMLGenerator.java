package br.com.caelum.camel.exercise;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class HTMLGenerator {
	public static void main(String[] args) throws Exception {
		
		CamelContext context = new DefaultCamelContext();
		
		context.addRoutes(new RouteBuilder() {
			
			@Override
			public void configure() throws Exception {
				

				from("direct:entrada")
				.to("xslt:html-template.xslt")
					.setHeader(Exchange.FILE_NAME, constant("movimentacoes.html"))
					.log("${body}")
				.to("file:saida");
			}
		});
		
		// test velocity framework
		// message translator is a common pattern used on EIP 
		
		context.start();

		ProducerTemplate producer = context.createProducerTemplate();
		producer.sendBody("direct:entrada", "<movimentacoes><movimentacao><valor>123</valor><data>2021-06-10</data><tipo>OUT</tipo></movimentacao><movimentacao><valor>222222</valor><data>2021-05-31</data><tipo>IN</tipo></movimentacao></movimentacoes>");
		
		Thread.sleep(1_000);
		context.stop();
	}
}
