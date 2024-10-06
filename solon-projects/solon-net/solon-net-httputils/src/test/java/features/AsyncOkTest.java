package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.net.http.HttpResponse;
import org.noear.solon.net.http.HttpUtils;
import org.noear.solon.net.http.impl.okhttp.OkHttpUtilsImpl;

import java.util.concurrent.CompletableFuture;

/**
 * @author noear 2024/10/6 created
 */
public class AsyncOkTest {
    static HttpUtils http(String url) {
        return new OkHttpUtilsImpl(url);
    }

    @Test
    public void case11() throws Exception {
        CompletableFuture<HttpResponse> htmlFuture = http("https://solon.noear.org/").getAsync();

        String text = htmlFuture.get().bodyAsString();
        System.out.println(text);

        assert text != null;
        assert text.contains("Solon");
    }

    @Test
    public void case12() throws Exception {
        CompletableFuture<HttpResponse> htmlFuture = http("https://www.bilibili.com/").getAsync();

        String text = htmlFuture.get().bodyAsString();
        System.out.println(text);

        assert text != null;
        assert text.contains("bilibili");
    }
}
