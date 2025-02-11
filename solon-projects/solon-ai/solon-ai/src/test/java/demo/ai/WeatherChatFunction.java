package demo.ai;

import org.noear.solon.ai.chat.functioncall.ChatFunction;
import org.noear.solon.ai.chat.functioncall.ChatFunctionParam;
import org.noear.solon.ai.chat.functioncall.ChatFunctionParamDecl;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author noear 2025/2/6 created
 */
@Component
public class WeatherChatFunction implements ChatFunction {
    @Inject
    //WeatherService weatherService;

    private List<ChatFunctionParam> params = new ArrayList<>();
    public WeatherChatFunction() {
        params.add(new ChatFunctionParamDecl("location", String.class, "根据用户提到的地点推测城市"));
    }

    @Override
    public String name() {
        return "get_weather";
    }

    @Override
    public String description() {
        return "获取指定城市的天气情况";
    }

    @Override
    public Iterable<ChatFunctionParam> params() {
        return params;
    }

    @Override
    public String handle(Map<String, Object> args) {
        String location = (String) args.get("location");

        if(location == null) {
            throw new IllegalStateException("arguments location is null (Assistant recognition failure)");
        }

        return "24度";// + weatherService.get(location);
    }
}