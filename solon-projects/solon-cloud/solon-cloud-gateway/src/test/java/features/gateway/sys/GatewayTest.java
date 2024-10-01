package features.gateway.sys;

import org.junit.jupiter.api.Test;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;

/**
 * @author noear 2024/10/1 created
 */
@SolonTest(App.class)
public class GatewayTest extends HttpTester {
    @Test
    public void hello() throws Exception {
        assert "hello".equals(path("/hello").get());
    }

    @Test
    public void gateway_hello() throws Exception {
        assert "hello".equals(path("/test/hello").get());
    }
}
