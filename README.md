# ConsoleRunner
A simple lightweight library of static functions to map commands to methods. For use within console heavy applications to provide basic console commands and execution flow control using flags.

Listens to System.in and runs any commands entered.

```
        ConsoleRunner.mapToFunction("command", flagArgs -> { 
                if (flagArgs[0].equals("s")) someFunction(); 
        })
``` 

Command entried can be added, removed, reset or destroyed all together.

Commands are null checked prior to calling `Consumer.accept(flags)` to avoid unnecessary NPEs.

Start listening for user input:

```
        ConsoleRunner.start();
```

Console commands are read in the following format:

```
<command> -<flag1> -<flag2> -<etc...>

example: print -hello -hi -hey
```

Map functions to method calls with optional flag arguments:

```
        ConsoleRunner.mapToFunction("time", flagArgs -> printTime());
        
        ConsoleRunner.mapToFunction("print", flagArgs -> {
            for (String flag : flagArgs) System.out.println(flag);
        });
        
        ConsoleRunner.mapToFunction("exit", flagArgs -> {
            for (String flag : flagArgs) {
                if (flag.equals("y")) exit();
            }
        });
        
        ConsoleRunner.mapToFunction("printstrings", ConsoleTester::printStrings);
```

Remove mapped commands:

```
        ConsoleRunner.removeMapToFunction("time");
```

Stop listening for user input when you are done:

```
        ConsoleRunner.stop();
```

Reset the console back to its initial state:

```
        ConsoleRunner.reset();
```

Use 'destroy()' to free up resources:

```
        ConsoleRunner.destroy();
```

Change the interval between console line reads:

```
        ConsoleRunner.setSleepInterval(500);
```
