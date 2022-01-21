import br.com.caelum.camel.RotaPedidos;
import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.language.ConstantExpression;
import org.springframework.beans.factory.annotation.Autowired;

@CamelSpringBootTest
@SpringBootTest(classes = {RotaPedidos.class}, webEnvironment = WebEnvironment.NONE)
@ContextConfiguration
@MockEndpointsAndSkip("^http:.*")
@DirtiesContext
public class RouteBuilderTest {

    @Autowired
    CamelContext camelContext;

    @EndpointInject(value = "mock:http:{{backend.url}}")
    private MockEndpoint mockEndpoint;

    @Produce(value = "direct:ENDPOINT-SERVICE")
    private ProducerTemplate routeProducerTemplate;

    @DirtiesContext
    @Test
    void testRoute() throws InterruptedException {
        Object expextedResponse = new Object();

        mockEndpoint.returnReplyBody(new ConstantExpression(expextedResponse.toString()));

        Object response = (Object) routeProducerTemplate.requestBody(expextedResponse);
        mockEndpoint.assertIsSatisfied();

        MockEndpoint.expectsMessageCount(1, mockEndpoint);
    }
}
