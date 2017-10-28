package net.samhouse;

import net.samhouse.db.service.OrderService;
import net.samhouse.model.Order;
import net.samhouse.model.Step;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.BDDAssertions.then;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MainApp.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"management.port=0"})
public class MainAppTest {

    @LocalServerPort
    private int port;

    @Value("${local.management.port}")
    private int mgt;

    private final String orderID = "orderID";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private OrderService orderService;

    @Before
    public void setUp() {
        orderService.delete(orderID);
    }

    @After
    public void tearDown() {
        orderService.delete(orderID);
    }

    @Test
    public void testQueryOrder() throws Exception {
        Order order = new Order(orderID, 0, 0,
                new Step(Step.Phase.SCHEDULING, 0, 0), "");

        orderService.insert(order);

        ResponseEntity<Order> entity = testRestTemplate
                .withBasicAuth("querier", "querier")
                .getForEntity(
                        "http://localhost:" + port + "/query/orderID", Order.class);

        then(entity.getBody().getOrderID()).isEqualTo(order.getOrderID());
        then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    /**
     *
     */
    @Test
    public void testSubmitOrder() {
        Order order = new Order(orderID, 0, 0,
                new Step(Step.Phase.SCHEDULING, 0, 0), "payload");

        ResponseEntity<String> entity = testRestTemplate.
                withBasicAuth("submitter", "submitter")
                .postForEntity(
                        "http://localhost:" + port + "/submit", order, String.class);

        then(entity.getBody()).isNotBlank();
        then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}