# ζetaConsole
A simple lightweight single-class API to map commands to functions for use within console based applications. Provides basic console commands and execution control using flags.

Listens to input stream and runs any commands entered:

```
        consoleRunner.mapToFunction("command", flagArgs -> { 
                if (flagArgs[0].equals("s")) someFunction(); 
        })
``` 

Command entries can be added, removed, or reset.

Mapped methods for commands are null checked prior to calling `Consumer.accept(flags)` to avoid unnecessary NPEs.

Create a new instance:

```
        ConsoleRunner consoleRunner = new ConsoleRunner("MyConsoleRunner", System.in);
```



Start listening for user input:

```
        consoleRunner.start();
```

Console commands are read in the following format:

```
<command> -<flag1> -<flag2> -<etc...>
```
example: print -hello -hi -hey

Map functions to method calls with optional flag arguments:

```
        consoleRunner.mapToFunction("time", flagArgs -> printTime());
        
        consoleRunner.mapToFunction("print", flagArgs -> {
            for (String flag : flagArgs) System.out.println(flag);
        });
        
        consoleRunner.mapToFunction("exit", flagArgs -> {
            for (String flag : flagArgs) {
                if (flag.equals("y")) exit();
            }
        });
        
        consoleRunner.mapToFunction("printstrings", ConsoleTest::printStrings);
```

Remove mapped commands:

```
        consoleRunner.removeMapToFunction("time");
```

Stop listening for user input when you are done:

```
        consoleRunner.stop();
```

Reset the console back to its initial state:

```
        consoleRunner.reset();
```
