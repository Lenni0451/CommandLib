import net.lenni0451.commandlib.CommandExecutor;
import net.lenni0451.commandlib.ParseResult;
import net.lenni0451.commandlib.builder.ArgumentBuilder;
import net.lenni0451.commandlib.exceptions.CommandExecutionException;

public class SortTest implements ArgumentBuilder<ExampleExecutor> {

    public static void main(String[] args) {
        new SortTest();
    }

    public SortTest() {
        CommandExecutor<ExampleExecutor> commandExecutor = new CommandExecutor<>();
        commandExecutor.register(this.string("test").executes(() -> System.out.println("test")));
        commandExecutor.register(this.string("testa").executes(() -> System.out.println("testa")));
        commandExecutor.register(this.string("testb").executes(() -> System.out.println("testb")));

        try {
            commandExecutor.execute(ExampleExecutor.INSTANCE, "test c");
        } catch (CommandExecutionException e) {
            if (e.getMostLikelyChains() == null || e.getMostLikelyChains().isEmpty()) return;
            for (ParseResult.FailedChain<?> failedChain : e.getMostLikelyChains()) {
                System.out.println(failedChain.getArgumentChain());
                System.out.println(" -> " + failedChain.getExecutionException());
            }
        }
    }

}
