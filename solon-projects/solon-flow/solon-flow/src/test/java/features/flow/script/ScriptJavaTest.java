package features.flow.script;

import org.junit.jupiter.api.Test;
import org.noear.solon.flow.core.*;

/**
 * @author noear 2025/1/10 created
 */
public class ScriptJavaTest {
    private FlowEngine flowEngine =  FlowEngine.newInstance();

    @Test
    public void case1() throws Throwable {
        Chain chain = new Chain("c1");

        chain.addNode(new NodeDecl("n1", NodeType.start).linkAdd("n2"));
        chain.addNode(new NodeDecl("n2", NodeType.execute).task("context.result=111 + a;").linkAdd("n3"));
        chain.addNode(new NodeDecl("n3", NodeType.end));


        ChainContext context = new ChainContext();
        context.paramSet("a", 2);
        context.paramSet("b", 3);
        context.paramSet("c", 4);

        flowEngine.eval(context, chain);
    }
}