import net.lenni0451.commandlib.CommandExecutor;
import net.lenni0451.commandlib.builder.ArgumentBuilder;
import net.lenni0451.commandlib.types.StringArgumentType;

public class ReturnTest implements ArgumentBuilder<ExampleExecutor> {

    public static void main(String[] args) throws Throwable {
        new ReturnTest();
    }


    private ReturnTest() throws Throwable {
        CommandExecutor<ExampleExecutor> commandExecutor = new CommandExecutor<>();
        commandExecutor.register(this.string("hello").then(this.typed("name", StringArgumentType.word()).executes(ctx -> "Hello " + ctx.getArgument("name"))));
        System.out.println(commandExecutor.<String>execute(ExampleExecutor.INSTANCE, "hello World"));
    }

}
