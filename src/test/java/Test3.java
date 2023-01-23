import net.lenni0451.commandlib.CommandExecutor;
import net.lenni0451.commandlib.nodes.StringArgumentNode;

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

    private static StringArgumentNode<Runtime> literal(final String literal) {
        return new StringArgumentNode<>(literal);
    }

    private static StringArgumentNode<Runtime> literal(final String literal, final Consumer<StringArgumentNode<Runtime>> consumer) {
        StringArgumentNode<Runtime> node = literal(literal);
        consumer.accept(node);
        return node;
    }

}
