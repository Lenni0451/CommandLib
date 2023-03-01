import net.lenni0451.commandlib.ArgumentChain;
import net.lenni0451.commandlib.CommandExecutor;
import net.lenni0451.commandlib.builder.ArgumentBuilder;
import net.lenni0451.commandlib.exceptions.ChainExecutionException;
import net.lenni0451.commandlib.exceptions.CommandExecutionException;

import java.util.Map;

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
            for (Map.Entry<ArgumentChain<?>, ChainExecutionException> entry : e.getMostLikelyChains().entrySet()) {
                System.out.println(entry.getKey());
                System.out.println(" -> " + entry.getValue());
            }
        }
    }

}
