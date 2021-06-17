package br.com.caelum.camel.model.processors;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class DefaultRedelivery implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		int counter = Integer.valueOf(exchange.getIn().getHeader(Exchange.REDELIVERY_COUNTER).toString());
		int max = Integer.valueOf(exchange.getIn().getHeader(Exchange.REDELIVERY_MAX_COUNTER).toString());
		System.out.println("Redelivery: " + counter + "/" + max);
	}

}
