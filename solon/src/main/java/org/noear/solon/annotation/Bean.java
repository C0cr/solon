package org.noear.solon.annotation;

import java.lang.annotation.*;

/**
 * 通用 组件（只能配合 @Configuration 使用）
 *
 * <pre><code>
 * //注解在配置器的函数上
 * @Configuration
 * public class Config{
 *     @Bean
 *     public DataSource db1(@Inject("${db1}") HikariDataSource ds){
 *         return ds;
 *     }
 * }
 * </code></pre>
 *
 * @author noear
 * @since 1.0
 * */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Bean {
    @Alias("name")
    String value() default ""; //as bean.name

    @Alias("value")
    String name() default "";

    /**
     * 标签，用于快速查找
     * */
    String tag() default "";

    /**
     * 要类型的（同时注册类型），仅当名称非空时有效
     * */
    boolean typed() default false;

    /**
     * 排序（只对部分类型有效）
     * */
    int index() default 0;

    /**
     * 要注册的（能力接口）
     */
    boolean registered() default true;

    /**
     * 要注入的
     * */
    boolean injected() default false;
}
