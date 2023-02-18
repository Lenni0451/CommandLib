import net.lenni0451.commandlib.ArgumentChain;
import net.lenni0451.commandlib.CommandExecutor;
import net.lenni0451.commandlib.builder.ArgumentBuilder;
import net.lenni0451.commandlib.exceptions.ChainExecutionException;
import net.lenni0451.commandlib.exceptions.CommandExecutionException;
import net.lenni0451.commandlib.nodes.StringNode;
import net.lenni0451.commandlib.utils.comparator.ArgumentComparator;

import java.util.Map;
import java.util.Scanner;

import static net.lenni0451.commandlib.types.IntegerType.integer;

public class Test2 implements ArgumentBuilder<Runtime> {

    public static void main(String[] args) throws Throwable {
        new Test2().run();
    }


    public void run() throws Throwable {
        CommandExecutor<Runtime> executor = new CommandExecutor<>(ArgumentComparator.CASE_SENSITIVE);
        StringNode<Runtime> root = string("test");
        root.then(
                typed("int", integer(0, 10)).executes(ctx -> {
                    int i = ctx.getArgument("int");
                    System.out.println("Got int: " + i);
                })
        );
        root.then(
                string("5").executes(() -> {
                    System.out.println("Got collision");
                })
        );
        root.then(
                string("exit").executes(() -> {
                    System.exit(0);
                })
        );
        executor.register(root);

        Scanner s = new Scanner(System.in);
        while (true) {
            String line = s.nextLine();
            try {
                executor.execute(Runtime.getRuntime(), line);
            } catch (CommandExecutionException e) {
                for (Map.Entry<ArgumentChain<?>, ChainExecutionException> entry : e.getMostLikelyChains().entrySet()) {
                    if (entry.getValue() == null) {
                        System.out.println(e.getMessage());
                    } else {
                        ChainExecutionException chainExecutionException = entry.getValue();
                        if (chainExecutionException.getReason().equals(ChainExecutionException.Reason.NO_ARGUMENTS_LEFT)) {
                            System.out.println("No data left! Missing arguments: " + chainExecutionException.getExtraData());
                        } else if (chainExecutionException.getReason().equals(ChainExecutionException.Reason.TOO_MANY_ARGUMENTS)) {
                            System.out.println("Too many arguments! Extra arguments: " + chainExecutionException.getExtraData());
                        } else {
                            System.out.println("Failed to parse argument '" + chainExecutionException.getArgumentName() + "': " + chainExecutionException.getExtraData());
                        }
                    }
                }
            }
        }
    }

}
