import net.lenni0451.commandlib.ArgumentChain;
import net.lenni0451.commandlib.CommandExecutor;
import net.lenni0451.commandlib.builder.ArgumentBuilder;
import net.lenni0451.commandlib.builder.LineBuilder;
import net.lenni0451.commandlib.exceptions.ChainExecutionException;
import net.lenni0451.commandlib.exceptions.CommandNotFoundException;
import net.lenni0451.commandlib.nodes.ArgumentNode;
import net.lenni0451.commandlib.nodes.StringArgumentNode;
import net.lenni0451.commandlib.types.IntegerArgumentType;
import net.lenni0451.commandlib.types.StringArgumentType;

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
        this.commandExecutor.register(
                this.string("gamemode")
                        .then(this.string("survival").executes(() -> System.out.println("Gamemode set to survival")))
                        .then(this.string("creative").executes(() -> System.out.println("Gamemode set to creative")))
        );
        this.commandExecutor.register(
                this.string("gm")
                        .then(this.typed("mode", IntegerArgumentType.integer(0, 1)).executes(c -> {
                            System.out.println("Gamemode set to " + c.getArgument("mode"));
                        }))
        );
        this.commandExecutor.register(
                this.string("list_test")
                        .then(this.list("list", IntegerArgumentType.integer(0, 100)).executes(c -> {
                            System.out.println("List: " + c.getArgument("list"));
                        }))
        );
        this.commandExecutor.register(
                this.string("string_test")
                        .then(this.string("word").then(this.typed("s", StringArgumentType.word()).executes(c -> {
                            System.out.println("Word: " + c.getArgument("s"));
                        })))
                        .then(this.string("string").then(this.typed("s", StringArgumentType.string()).executes(c -> {
                            System.out.println("String: " + c.getArgument("s"));
                        })))
                        .then(this.string("greedy").then(this.typed("s", StringArgumentType.greedyString()).executes(c -> {
                            System.out.println("Greedy: " + c.getArgument("s"));
                        })))
        );
        this.register("line", line -> line
                .arg("arg1", StringArgumentType.word())
                .arg("arg2", this.dynamicType(r -> Integer.parseInt(r.readWordOrString())))
                .executes(c -> {
                    System.out.println(c.getArgument("arg1") + " " + c.getArgument("arg2"));
                }));
        this.register("oline", line -> line
                .arg("arg1", StringArgumentType.word())
                .arg("arg2", this.dynamicType(r -> Integer.parseInt(r.readWordOrString()))).defaultValue(1337)
                .executes(c -> {
                    System.out.println(c.getArgument("arg1") + " " + c.getArgument("arg2"));
                }));
        this.commandExecutor.register(
                this.string("valtest")
                        .then(this.typed("val", StringArgumentType.string()).validator(v -> v.length() == 5).executes(c -> {
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
                this.string("print").executes(() -> {
                    try {
                        Field chains = this.commandExecutor.getClass().getDeclaredField("chains");
                        chains.setAccessible(true);
                        Map<StringArgumentNode<?>, List<ArgumentChain<?>>> chainsMap = (Map<StringArgumentNode<?>, List<ArgumentChain<?>>>) chains.get(this.commandExecutor);
                        System.out.println("-------------------------------");
                        for (Map.Entry<StringArgumentNode<?>, List<ArgumentChain<?>>> entry : chainsMap.entrySet()) {
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
            Set<String> completions = this.commandExecutor.completions(ExampleExecutor.INSTANCE, input);
            this.setOutput(
                    Arrays.toString(completions.stream().sorted().toArray(String[]::new)),
                    ""
            );

            String out = this.commandExecutor.execute(ExampleExecutor.INSTANCE, input);
            this.addOutput(
                    "Successfully executed command",
                    "Output: " + out
            );
        } catch (CommandNotFoundException e) {
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
                for (Map.Entry<ArgumentChain<?>, ChainExecutionException> entry : e.getMostLikelyChains().entrySet()) {
                    this.addOutput(
                            "Likely chain",
                            entry.getKey(),
                            "Chain exception:",
                            " - Reason: " + entry.getValue().getReason(),
                            " - Execution Index: " + entry.getValue().getExecutionIndex(),
                            " - Reader Cursor: " + entry.getValue().getReaderCursor(),
                            " - Argument Name: " + entry.getValue().getArgumentName(),
                            " - Extra Data: " + entry.getValue().getExtraData(),
                            " - Cause: " + entry.getValue().getCause(),
                            ""
                    );
                }
            }
        } catch (Throwable t) {
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
