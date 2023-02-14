import net.lenni0451.commandlib.ArgumentChain;
import net.lenni0451.commandlib.CommandExecutor;
import net.lenni0451.commandlib.exceptions.ChainExecutionException;
import net.lenni0451.commandlib.exceptions.CommandNotFoundException;
import net.lenni0451.commandlib.types.IntegerArgumentType;
import net.lenni0451.commandlib.utils.ArgumentBuilder;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

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
    }

    private void addElements() {
        JPanel root = new JPanel();
        root.setLayout(null);

        this.input.setBounds(10, 10, 470, 30);
        this.input.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                UITest.this.onTextChange(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                UITest.this.onTextChange(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                UITest.this.onTextChange(e);
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
    }

    private void onTextChange(final DocumentEvent event) {
        if (this.input.getText().isEmpty()) {
            this.setOutput();
            return;
        }
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
                this.addOutput("No likely chains found");
            } else {
                for (Map.Entry<ArgumentChain<?>, ChainExecutionException> entry : e.getMostLikelyChains().entrySet()) {
                    this.addOutput(
                            "Likely chain",
                            entry.getKey(),
                            "Chain exception:",
                            entry.getValue().getReason(),
                            entry.getValue().getExecutionIndex(),
                            entry.getValue().getReaderCursor(),
                            entry.getValue().getArgumentName(),
                            entry.getValue().getExtraData(),
                            entry.getValue().getCause(),
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
    }

    private void addOutput(final Object requiredLine, final Object... lines) {
        String[] allLines = new String[lines.length + 1];
        allLines[0] = this.toString(requiredLine);
        for (int i = 0; i < lines.length; i++) allLines[i + 1] = this.toString(lines[i]);
        this.output.setText(this.output.getText() + "\n" + String.join("\n", allLines));
    }

    private String toString(final Object o) {
        if (o == null) return "<null>";
        return o.toString();
    }

}
