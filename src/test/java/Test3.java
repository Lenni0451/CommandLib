import net.lenni0451.commandlib.CommandExecutor;
import net.lenni0451.commandlib.nodes.StringNode;

import java.util.Scanner;
import java.util.function.Consumer;

public class Test3 {

    public static void main(String[] args) {
        Runtime runtime = Runtime.getRuntime();
        CommandExecutor<Runtime> executor = new CommandExecutor<>();

        executor.register(literal("gamemode", n -> n.then(literal("survival").executes(() -> {})).then(literal("creative").executes(() -> {}))));
        executor.register(literal("gamerule", n -> n.executes(() -> {})));

        Scanner s = new Scanner(System.in);
        while (true) {
            System.out.println(executor.completions(runtime, s.nextLine()));
        }
    }

    private static StringNode<Runtime> literal(final String literal) {
        return new StringNode<>(literal);
    }

    private static StringNode<Runtime> literal(final String literal, final Consumer<StringNode<Runtime>> consumer) {
        StringNode<Runtime> node = literal(literal);
        consumer.accept(node);
        return node;
    }

}
