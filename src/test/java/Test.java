import net.lenni0451.commandlib.CommandExecutor;
import net.lenni0451.commandlib.exceptions.CommandNotFoundException;
import net.lenni0451.commandlib.nodes.StringArgumentNode;
import net.lenni0451.commandlib.utils.ArgumentComparator;

public class Test {

    /*
literal("test").arg("lul", sr -> Integer.parse(sr.readUntilSpace())).executes(ctx -> {
	int g = ctx.getArg("lul");
});

literal("test").arg("lul", playerArg(), playerCompleter()).executes(ctx -> {
	int g = ctx.getArg("lul");
});

literal("test").arg("lul", playerArg(), playerCompleter(), 5).executes(ctx -> {
	int g = ctx.getArg("lul"); // default 5
});

literal("test").arg("lul", new PlayerArgType()).executes(ctx -> {
	int g = ctx.getArg("lul");
});
     */

//    public static void main(String[] args) {
//        String test = "Hallo das \"i\\st\" ein 'Te\\'st' 1337 13.37 13,37";
//        StringReader sr = new StringReader(test);
//
//        System.out.println(sr.readWordOrString());
//        System.out.println(sr.readWordOrString());
//        System.out.println(sr.readWordOrString());
//        System.out.println(sr.readWordOrString());
//        System.out.println(sr.readWordOrString());
//        System.out.println(sr.readIntegerNumber());
//        System.out.println(sr.readDecimalNumber());
//        System.out.println(sr.readDecimalNumber());
//    }

    public static void main(String[] args) throws CommandNotFoundException {
        StringArgumentNode<Runtime> node = literal("test");
        node.then(literal("lul").executes(() -> {
            System.out.println("1");
        }));
        node.then(literal("lul2").executes(() -> {
            System.out.println("2");
        }));
        node.then(literal("lul3").executes(() -> {
            System.out.println("3");
        }).then(literal("lul4").executes(() -> {
            System.out.println("4");
        })));

        CommandExecutor<Runtime> executor = new CommandExecutor<>(ArgumentComparator.CASE_SENSITIVE);
        executor.register(node);
        tryExecute(executor, "test");
        tryExecute(executor, "test fail");
        tryExecute(executor, "test lul fail again");
        tryExecute(executor, "test lul");
        tryExecute(executor, "test lul2");
        tryExecute(executor, "test lul3");
        tryExecute(executor, "test lul3 lul4");
    }

    private static void tryExecute(final CommandExecutor<Runtime> executor, final String command) {
        try {
            executor.execute(Runtime.getRuntime(), command);
        } catch (Throwable t) {
            System.out.println("error: " + command);
        }
    }

    private static StringArgumentNode<Runtime> literal(final String literal) {
        return new StringArgumentNode<>(literal);
    }

}
