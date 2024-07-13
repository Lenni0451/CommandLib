import net.lenni0451.commandlib.ArgumentChain;
import net.lenni0451.commandlib.CommandExecutor;
import net.lenni0451.commandlib.Completion;
import net.lenni0451.commandlib.ParseResult;
import net.lenni0451.commandlib.builder.ArgumentBuilder;
import net.lenni0451.commandlib.builder.LineBuilder;
import net.lenni0451.commandlib.contexts.CompletionContext;
import net.lenni0451.commandlib.contexts.ExecutionContext;
import net.lenni0451.commandlib.exceptions.CommandExecutionException;
import net.lenni0451.commandlib.nodes.ArgumentNode;
import net.lenni0451.commandlib.nodes.StringArrayNode;
import net.lenni0451.commandlib.nodes.StringNode;
import net.lenni0451.commandlib.types.FloatType;
import net.lenni0451.commandlib.types.IntegerType;
import net.lenni0451.commandlib.types.StringType;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class UITest extends JFrame implements ArgumentBuilder<ExampleExecutor> {

    public static void main(String[] args) {
        new UITest().setVisible(true);
    }


    private final CommandExecutor<ExampleExecutor> commandExecutor = new CommandExecutor<>();
    private final JTextField input = new JTextField();
    private final JTextArea output = new JTextArea();

    private UITest() {
        this.setTitle("UI Test");
        this.setSize(500, 600);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
        this.setLayout(null);

        this.addElements();
        this.registerCommands();
        this.onTextChange();
    }

    private void addElements() {
        JPanel root = new JPanel();
        root.setLayout(null);

        this.input.setBounds(10, 10, 470, 30);
        this.input.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                UITest.this.onTextChange();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                UITest.this.onTextChange();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                UITest.this.onTextChange();
            }
        });
        root.add(this.input);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(10, 50, 470, 500);
        root.add(scrollPane);

        this.output.setEditable(false);
        scrollPane.setViewportView(this.output);

        this.setContentPane(root);
    }

    private void registerCommands() {
        ArgumentNode<ExampleExecutor, ?> intList = this.list("list", IntegerType.integer(0, 100)).executes(c -> {
            System.out.println("List: " + c.getArgument("list"));
        });

        this.commandExecutor.register(
                this.string("gamemode")
                        .then(this.string("survival").completionMatcher(CompletionContext.CompletionMatcher.CONTAINS).executes(() -> System.out.println("Gamemode set to survival")))
                        .then(this.string("creative").completionMatcher(CompletionContext.CompletionMatcher.CONTAINS).executes(() -> System.out.println("Gamemode set to creative")))
        );
        this.commandExecutor.register(
                this.string("gm")
                        .then(this.typed("mode", IntegerType.integer(0, 1)).executes(c -> {
                            System.out.println("Gamemode set to " + c.getArgument("mode"));
                        }))
        );
        this.commandExecutor.register(
                this.string("list_test").then(intList)
        );
        this.commandExecutor.register(
                this.string("string_test")
                        .then(this.string("word").then(this.typed("s", StringType.word()).executes(c -> {
                            System.out.println("Word: " + c.getArgument("s"));
                        })))
                        .then(this.string("string").then(this.typed("s", StringType.string()).executes(c -> {
                            System.out.println("String: " + c.getArgument("s"));
                        })))
                        .then(this.string("greedy").then(this.typed("s", StringType.greedyString()).executes(c -> {
                            System.out.println("Greedy: " + c.getArgument("s"));
                        })))
        );
        this.register("line", line -> line
                .arg("arg1", StringType.word())
                .arg("arg2", this.dynamicType(r -> Integer.parseInt(r.readWordOrString())))
                .executes(c -> {
                    System.out.println(c.getArgument("arg1") + " " + c.getArgument("arg2"));
                }));
        this.register("oline", line -> line
                .arg("arg1", StringType.word())
                .arg("arg2", this.dynamicType(r -> Integer.parseInt(r.readWordOrString()))).defaultValue(1337)
                .executes(c -> {
                    System.out.println(c.getArgument("arg1") + " " + c.getArgument("arg2"));
                }));
        this.commandExecutor.register(
                this.line()
                        .node(this.string("doline"))
                        .arg("int", IntegerType.integer()).defaultValue(100)
                        .executes(ctx -> {
                            System.out.println("Int: " + ctx.getArgument("int"));
                        })
        );
        this.commandExecutor.register(
                this.string("valtest")
                        .then(this.typed("val", StringType.string()).validator(v -> v.length() == 5).executes(c -> {
                            System.out.println("Val: " + c.getArgument("val"));
                        }))
        );
        this.commandExecutor.register(
                this.string("custom_exception")
                        .then(this.string("callme")
                                .executes(() -> System.out.println("Called me"))
                                .exceptionHandler((executor, t) -> System.out.println("wrong")))
        );
        this.commandExecutor.register(
                this.string("legacy")
                        .then(this.stringArray("args", new StringArrayNode.Executor<ExampleExecutor>() {
                            @Override
                            public void execute(ExampleExecutor executor, String[] args, ExecutionContext<ExampleExecutor> executionContext) {
                                System.out.println("Legacy: " + Arrays.toString(args));
                            }
                        }, new StringArrayNode.Completor<ExampleExecutor>() {
                            @Override
                            public void complete(Set<String> completions, String[] currentArgs, ExecutionContext<ExampleExecutor> executionContext) {
                                if (currentArgs.length == 0) {
                                    completions.add("testa");
                                    completions.add("testb");
                                } else if (currentArgs.length == 1) {
                                    if (currentArgs[0].equals("testa")) completions.add("a");
                                    else if (currentArgs[0].equals("testb")) completions.add("b");
                                }
                            }
                        }))
        );
        this.commandExecutor.register(
                this.string("noperms")
                        .then(typed("arg", IntegerType.integer())
                                .requires(e -> false)
                                .executes(() -> System.out.println("How did you get here?"))
                        )
        );
        this.commandExecutor.register(
                this.string("lt")
                        .then(this.redirect(intList))
        );
        this.commandExecutor.register(
                this.string("at")
                        .then(this.array("arr", IntegerType.integer()).executes(c -> {
                            System.out.println("Array: " + c.<List<Integer>>getArgument("arr"));
                        }))
        );
        this.commandExecutor.register(
                this.string("float")
                        .then(this.typed("float", FloatType.rangeFloat(10, 50)).executes(c -> {
                            System.out.println("Float: " + c.getArgument("float"));
                        }))
        );

        this.commandExecutor.register(
                this.string("print").executes(() -> {
                    try {
                        Field chains = this.commandExecutor.getClass().getDeclaredField("chains");
                        chains.setAccessible(true);
                        Map<StringNode<?>, List<ArgumentChain<?>>> chainsMap = (Map<StringNode<?>, List<ArgumentChain<?>>>) chains.get(this.commandExecutor);
                        System.out.println("-------------------------------");
                        for (Map.Entry<StringNode<?>, List<ArgumentChain<?>>> entry : chainsMap.entrySet()) {
                            System.out.println(entry.getKey().name());
                            for (ArgumentChain<?> chain : entry.getValue()) System.out.println(" - " + chain);
                        }
                        System.out.println("-------------------------------");
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                })
        );
    }

    private void register(final String name, final Function<LineBuilder<ExampleExecutor>, ArgumentNode<ExampleExecutor, ?>> builder) {
        this.commandExecutor.register(this.string(name).then(builder.apply(this.line())));
    }

    private void onTextChange() {
        try {
            String input = this.input.getText();
            Set<Completion> completions = this.commandExecutor.completions(ExampleExecutor.INSTANCE, input);
            this.setOutput(
                    Arrays.toString(completions.stream().map(c -> c.getStart() + ":" + c.getCompletion()).toArray(String[]::new)),
                    ""
            );

            String out = this.commandExecutor.execute(ExampleExecutor.INSTANCE, input);
            this.addOutput(
                    "Successfully executed command",
                    "Output: " + out
            );
        } catch (CommandExecutionException e) {
            this.addOutput(
                    "Command not found exception",
                    "Command: " + e.getCommand(),
                    ""
            );
            if (e.getMostLikelyChains() == null || e.getMostLikelyChains().isEmpty()) {
                this.addOutput(
                        "No likely chains found"
                );
            } else {
                for (ParseResult.FailedChain<?> failedChain : e.getMostLikelyChains()) {
                    this.addOutput(
                            "Likely chain",
                            failedChain.getArgumentChain(),
                            "Chain exception:",
                            " - Reason: " + failedChain.getExecutionException().getReason(),
                            " - Execution Index: " + failedChain.getExecutionException().getExecutionIndex(),
                            " - Reader Cursor: " + failedChain.getExecutionException().getReaderCursor(),
                            " - Argument Name: " + failedChain.getExecutionException().getArgumentName(),
                            " - Extra Data: " + failedChain.getExecutionException().getExtraData(),
                            " - Cause: " + failedChain.getExecutionException().getCause(),
                            ""
                    );
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
            this.addOutput(
                    "Unknown exception",
                    "Exception: " + t.getClass().getSimpleName(),
                    "Message: " + t.getMessage()
            );
        }
    }

    private void setOutput(final String... lines) {
        this.output.setText(String.join("\n", lines));
        this.output.setCaretPosition(0);
    }

    private void addOutput(final Object requiredLine, final Object... lines) {
        String[] allLines = new String[lines.length + 1];
        allLines[0] = this.toString(requiredLine);
        for (int i = 0; i < lines.length; i++) allLines[i + 1] = this.toString(lines[i]);
        this.output.setText(this.output.getText() + "\n" + String.join("\n", allLines));
        this.output.setCaretPosition(0);
    }

    private String toString(final Object o) {
        if (o == null) return "<null>";
        return o.toString();
    }

}
