package hello;

import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.prometheus.client.Counter;
import io.prometheus.client.spring.boot.EnablePrometheusEndpoint;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    static final Counter requests = Counter.build()
            .name("greeting_requests_total").help("Total requests.").register();
    
    private static final Logger logger = LoggerFactory.getLogger(GreetingController.class);
         
    
    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="NGP") String name) {
        requests.inc();
        logger.info("Greetings will be sent out! Hi!");
        return new Greeting(counter.incrementAndGet(),
                            String.format(template, name));
    }
}
