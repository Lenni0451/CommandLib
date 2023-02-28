# CommandLib
A command lib with a simple and powerful API.

## Releases
To use CommandLib with Gradle/Maven, you can follow the instructions on [maven central](https://central.sonatype.com/search?q=net.lenni0451%20CommandLib).\
You should always check for the latest version and use it instead of the one in the examples.

### Gradle template
```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation "net.lenni0451:CommandLib:1.0.0"
}
```

### Maven template
```xml
<repository>
    <id>central</id>
    <name>Maven Central</name>
    <url>https://repo1.maven.org/maven2</url>
</repository>

<dependency>
    <groupId>net.lenni0451</groupId>
    <artifactId>CommandLib</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Manual download
You can also download the latest version of CommandLib from my [Jenkins](https://build.lenni0451.net/job/CommandLib/) server.

## Usage
### CommandExecutor
To use CommandLib you need to create a `CommandExecutor` instance:
```java
CommandExecutor<Executor> executor = new CommandExecutor<>();
```
You can optionally pass a `ArgumentComparator` to the constructor to change the case sensitivity of the arguments.\
The default comparator is `ArgumentComparator.CASE_INSENSITIVE`.

The type argument of the `CommandExecutor` specifies the type of the executor. The executor has to be passed when executing or completing a command.

### Registering commands
To register a command you need to create a `ArgumentNode` and add it to the executor.\
The following nodes are available:
| Node            | Description                                                                   |
| --------------- | ----------------------------------------------------------------------------- |
| StringNode      | Matches a string (case-sensitivity depends on the comparator of the executor) |
| TypedNode       | Parses and matches a given argument type                                      |
| ListNode        | Parses and matches the given argument type multiple times as a list           |
| StringArrayNode | Splits the arguments into a string array to handle them directly              |

The `ArgumentBuilder` interface contains useful methods to easily create nodes.

This example shows how to create and register a command that prints the given string:
```java
this.commandExecutor.register(
        this.string("test").then(this.typed("arg", StringType.string()).executes(c -> {
            System.out.println("Test: " + c.getArgument("arg"));
        }))
);
```

### Completions
Argument nodes can also provide completions for the given input.
```java
Set<Completion> completions = this.commandExecutor.completions(executor, input);
```
A `Completion` contains the index of the completion in the input and the completion itself.\
Example on how to fill completions;
```java
String input = "test inp"; // The input the user types
Completion completion = new Completion(5, "input"); // Get the completion from somewhere
input = input.substring(0, completion.getStart()) + completion.getCompletion();
```
The output of this example would be `test input`.

### Executing commands
To execute a command you need to pass the executor and the input to the executor.
```java
Object output = this.commandExecutor.execute(executor, input);
```
The output is the return value of the executed command. If the command did not return anything, `null` is returned.
