package br.com.caelum.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.xml.sax.SAXParseException;

import br.com.caelum.camel.model.processors.DefaultRedelivery;

public class RotaPedidos {

	public static void main(String[] args) throws Exception {

		CamelContext context = new DefaultCamelContext();
		context.addRoutes(new RouteBuilder() {

			@Override
			public void configure() throws Exception {
				
				onException(SAXParseException.class)
					.handled(true)
					.maximumRedeliveries(3)
					.redeliveryDelay(1000)
					.onRedelivery(new DefaultRedelivery());
				
				
				from("file:pedidos?delay=5s&noop=true")
					.routeId("main-route")
				.to("validator:pedido.xsd")
//			---> errorHandler handles any error, but what if you want to handle specific exceptions?				
//					.errorHandler(deadLetterChannel("file:error")
//							.useOriginalMessage()
//							.logExhaustedMessageHistory(true)
//							.maximumRedeliveries(3)
//							.redeliveryDelay(2000)
//							.onRedelivery(new DefaultRedelivery()))
					.multicast()
						.parallelProcessing()
							.to("direct:soap")
							.to("direct:http");
				

				from("direct:http")
					.routeId("route-http")
					.setProperty("pedidoId", xpath("/pedido/id/text()"))
					.setProperty("clienteId", xpath("/pedido/pagamento/email-titular/text()"))
						.split(xpath("/pedido/itens/item"))
						.filter(xpath("/item/formato[text()='EBOOK']"))
					.setProperty("ebookId", xpath("item/livro/codigo/text()"))
					.marshal()
						.xmljson()
						.log("${routeId} - posting ${body}")
						.setHeader(Exchange.HTTP_QUERY, simple("clienteId=${property.clienteId}&pedidoId=${property.pedidoId}&ebookId=${property.ebookId}"))
				.to("http4://localhost:8080/webservices/ebook/item");
				
				from("direct:soap")
					.routeId("route-soap")
				.to("xslt:pedido-para-soap.xslt")
					.log("consuming SOAP webservice - request ${body}")
					.setHeader(Exchange.CONTENT_TYPE, constant("text/xml"))
				.to("http4://localhost:8080/webservices/financeiro");
				
			}

//			v1
//			@Override
//			public void configure() throws Exception {
//				from("file:pedidos?delay=5s&noop=true")
//					.routeId("xml-filter")
//					.log("${exchange.pattern}")
//					.setProperty("pedidoId", xpath("/pedido/id/text()"))
//					.setProperty("clienteId", xpath("/pedido/pagamento/email-titular/text()"))
//						.split(xpath("/pedido/itens/item"))
//						.filter(xpath("/item/formato[text()='EBOOK']"))
//					.setProperty("ebookId", xpath("/item/livro/codigo/text()"))
//					.marshal()
//						.xmljson()
//						.log("Camel working on ${routeId} - ${body}")
////					.setHeader("CamelFileName", simple("${file:name.noext}.json"))
//					.setHeader(Exchange.HTTP_QUERY, simple("clienteId=${property.clienteId}&pedidoId=${property.pedidoId}&ebookId=${property.ebookId}"))
//				.to("http4://localhost:8080/webservices/ebook/item");
//			}
			
		});
		
		context.start();
		Thread.sleep(10_000);
		context.stop();
	}	
}
