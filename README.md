# ζetaConsole
A simple lightweight single-class API to map commands to functions for use within console based applications. Provides basic console commands and execution control using options/flags.

Listens to input stream and runs any commands entered:

```
        zetaConsole.mapToFunction("command", options -> { 
                if (options[0].equals("s")) someFunction(); 
        })
``` 

Command entries can be added, removed, or reset.

Mapped methods for commands are null checked prior to calling `Consumer.accept(options)` to avoid unnecessary NPEs.

Create a new instance:

```
        ZetaConsole zetaConsole = new ZetaConsole("MyConsole", System.in);
```



Start listening for user input:

```
        zetaConsole.start();
```

Console commands are read in the following format:

```
<command> <option1> <option2> <etc...>
```
example: print -hello -hi -hey

Map console input to functions with optional arguments:

```
        zetaConsole.mapToFunction("time", options -> printTime());
        
        zetaConsole.mapToFunction("print", options -> {
            for (String options : options) System.out.println(options);
        });
        
        zetaConsole.mapToFunction("exit", options -> {
            for (String options : options) {
                if (options.equals("y")) exit();
            }
        });
        
        zetaConsole.mapToFunction("printstrings", ConsoleTest::printStrings);
```

Remove mapped commands:

```
        zetaConsole.removeMapToFunction("time");
```

Stop listening for user input when you are done:

```
        zetaConsole.stop();
```

Reset the console back to its initial state:

```
        zetaConsole.reset();
```
