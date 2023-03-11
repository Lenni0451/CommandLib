import net.lenni0451.commandlib.ArgumentChain;
import net.lenni0451.commandlib.builder.ArgumentBuilder;
import net.lenni0451.commandlib.nodes.RedirectNode;
import net.lenni0451.commandlib.nodes.StringNode;

public class ChainPrintTest implements ArgumentBuilder<ExampleExecutor> {

    public static void main(String[] args) {
        new ChainPrintTest();
    }

    public ChainPrintTest() {
        StringNode<ExampleExecutor> target = (StringNode<ExampleExecutor>) this.string("target").executes(() -> {});
        RedirectNode<ExampleExecutor> redirect = this.redirect("redirect", target);
        StringNode<ExampleExecutor> root = (StringNode<ExampleExecutor>) this.string("test").then(redirect);

        ArgumentChain<ExampleExecutor> rootChain = ArgumentChain.buildChains(root).get(0);
        ArgumentChain<ExampleExecutor> mergedChain = ArgumentChain.merge(rootChain, redirect.getTargetChains().get(0));
        System.out.println(rootChain);
        System.out.println(mergedChain);
        System.out.println(mergedChain.toString(false));
        System.out.println(mergedChain.toString(true));
    }

}
