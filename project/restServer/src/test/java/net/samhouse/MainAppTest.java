package net.samhouse;

import net.samhouse.model.Order;
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

import java.util.Date;

import static org.assertj.core.api.BDDAssertions.then;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MainApp.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {"management.port=0"})
public class MainAppTest {

    @LocalServerPort
    private int port;

    @Value("${local.management.port}")
    private int mgt;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void testQueryOrder() throws Exception {
        @SuppressWarnings("rawtypes")
        ResponseEntity<String> entity = testRestTemplate
                .withBasicAuth("querier", "querier")
                .getForEntity(
                "http://localhost:" + port + "/query/aaaa", String.class);

        then(entity.getBody()).isEqualTo("aaaa");
        then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void testSubmitOrder() {
        ResponseEntity<String> entity = testRestTemplate.
                withBasicAuth("submitter", "submitter")
                .postForEntity(
                        "http://localhost:" + port + "/submit", new Order("", new Date(), new Date()), String.class);

        then(entity.getBody()).isEqualTo("orderID");
        then(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}