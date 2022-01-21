import br.com.caelum.camel.RotaPedidos;
import org.apache.camel.*;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultMessage;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest(classes = {RotaPedidos.class}, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ContextConfiguration
public class ProcessorExampleTest {

    @Autowired
    CamelContext camelContext;

    @Autowired
    ProducerTemplate producerTemplate;

    private Exchange mockedExchange;

    @DirtiesContext
    @Test
    void testProcessor() throws Exception {
        mockedExchange = new ExchangeBuilder(camelContext).build();
        Message message = new DefaultMessage(camelContext);
        message.setHeader(Exchange.HTTP_RESPONSE_CODE, 200);
        // sets response object
        message.setBody(new Object());
        mockedExchange.setIn(message);

        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // new ResponseObject()
                from("direct:start").process(new String())
                        .unmarshal()
                        .json(JsonLibrary.Jackson, Object.class);
            }
        });

        Exchange body = producerTemplate.send("direct:start", mockedExchange);
        assertEquals(body.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE), 200);
    }
}
