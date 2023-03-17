import net.lenni0451.commandlib.CommandExecutor;
import net.lenni0451.commandlib.ParseResult;
import net.lenni0451.commandlib.builder.ArgumentBuilder;
import net.lenni0451.commandlib.exceptions.CommandExecutionException;
import net.lenni0451.commandlib.types.IntegerType;

public class WrongRecommendations implements ArgumentBuilder<ExampleExecutor> {

    public static void main(String[] args) {
        new WrongRecommendations();
    }


    public WrongRecommendations() {
        CommandExecutor<ExampleExecutor> executor = new CommandExecutor<>();

        executor.register(
                this.string("test").then(this.string("int").then(this.typed("i", IntegerType.integer()).executes(ctx -> {

                }).then(this.string("a").executes(ctx -> {

                }))))
        );
        executor.register(
                this.string("t").then(this.string("int").then(this.typed("i", IntegerType.integer()).executes(ctx -> {

                })))
        );

        try {
            executor.execute(ExampleExecutor.INSTANCE, "t int a");
        } catch (CommandExecutionException e) {
            System.out.println(e.getMessage());
            for (ParseResult.FailedChain<?> chain : e.getMostLikelyChains()) {
                System.out.println(chain.getExecutionException().getMessage());
                System.out.println(chain.getArgumentChain());
            }
        }
    }

}
