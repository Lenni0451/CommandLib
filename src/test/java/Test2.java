import net.lenni0451.commandlib.CommandExecutor;
import net.lenni0451.commandlib.exceptions.CommandNotFoundException;
import net.lenni0451.commandlib.nodes.StringArgumentNode;
import net.lenni0451.commandlib.utils.ArgumentBuilder;
import net.lenni0451.commandlib.utils.ArgumentComparator;

import java.util.Scanner;

import static net.lenni0451.commandlib.types.IntegerArgumentType.integer;

public class Test2 implements ArgumentBuilder<Runtime> {

    public static void main(String[] args) throws Throwable {
        new Test2().run();
    }


    public void run() throws Throwable {
        CommandExecutor<Runtime> executor = new CommandExecutor<>(ArgumentComparator.CASE_SENSITIVE);
        StringArgumentNode<Runtime> root = string("test");
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
            } catch (CommandNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }
    }

}
