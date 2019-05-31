# ζetaConsole
A simple lightweight single-class API to map commands to functions for use within console based applications. Provides basic console commands and execution control using flags.

Listens to input stream and runs any commands entered:

```
        zetaConsole.mapToFunction("command", flagArgs -> { 
                if (flagArgs[0].equals("s")) someFunction(); 
        })
``` 

Command entries can be added, removed, or reset.

Mapped methods for commands are null checked prior to calling `Consumer.accept(flags)` to avoid unnecessary NPEs.

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
<command> -<flag1> -<flag2> -<etc...>
```
example: print -hello -hi -hey

Map console input to functions with optional flag arguments:

```
        zetaConsole.mapToFunction("time", flagArgs -> printTime());
        
        zetaConsole.mapToFunction("print", flagArgs -> {
            for (String flag : flagArgs) System.out.println(flag);
        });
        
        zetaConsole.mapToFunction("exit", flagArgs -> {
            for (String flag : flagArgs) {
                if (flag.equals("y")) exit();
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
