import net.lenni0451.commandlib.CommandExecutor;
import net.lenni0451.commandlib.ParseResult;
import net.lenni0451.commandlib.builder.ArgumentBuilder;
import net.lenni0451.commandlib.exceptions.CommandExecutionException;
import net.lenni0451.commandlib.types.IntegerType;

public class ReversedRecommendations implements ArgumentBuilder<ExampleExecutor> {

    public static void main(String[] args) {
        new ReversedRecommendations();
    }


    public ReversedRecommendations() {
        CommandExecutor<ExampleExecutor> executor = new CommandExecutor<>();

        executor.register(
                this.string("test")
//                        .then(this.string("int").then(this.typed("int", IntegerType.integer()).executes(ctx -> {
//
//                        })))
                        .then(this.string("direct").executes(ctx -> {

                        }))
                        .then(this.typed("int", IntegerType.integer()).executes(ctx -> {

                        }))
        );

        try {
            executor.execute(ExampleExecutor.INSTANCE, "test dir");
        } catch (CommandExecutionException e) {
            System.out.println(e.getMessage());
            for (ParseResult.FailedChain<?> chain : e.getMostLikelyChains()) {
                System.out.println(chain.getArgumentChain());
            }
        }
    }

}
