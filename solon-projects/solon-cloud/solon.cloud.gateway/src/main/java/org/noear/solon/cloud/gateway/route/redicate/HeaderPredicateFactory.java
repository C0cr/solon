package org.noear.solon.cloud.gateway.route.redicate;

import org.noear.solon.cloud.gateway.exchange.ExContext;
import org.noear.solon.cloud.gateway.exchange.ExPredicate;
import org.noear.solon.cloud.gateway.route.RoutePredicateFactory;


import java.util.regex.Pattern;


/**
 * @author wjc28
 * @version 1.0
 * @description: 路由请求头匹配
 * @date 2024-08-19
 */
public class HeaderPredicateFactory implements RoutePredicateFactory {
    @Override
    public String prefix() {
        return "Header";
    }


    @Override
    public ExPredicate create(String config) {
        HeaderPredicate headerPredicate = new HeaderPredicate(config);
        return headerPredicate;
    }

    private static class HeaderPredicate implements ExPredicate {
        private String headerKey;
        private Pattern pattern;


        public HeaderPredicate(String config) {
            String[] configs = config.split(",");
            headerKey = configs[0].trim();
            String regex = configs[1].trim();
            pattern = Pattern.compile(regex);
        }

        @Override
        public boolean test(ExContext exContext) {
            String value = exContext.rawHeader(headerKey);
            return  pattern.matcher(value).find();
        }
    }
}
