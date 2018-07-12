# S-Console
A simple class of static members to map commands that can be delivered in the console for use within a controller class. Listens to System.in input and runs any commands specified. Flags are checked by the caller and stored using `ConsoleRunner.mapToFunction("command", flagArgs -> { if (flagArgs[0].equals("s")) someFunction(); })` It is up to the developer to manage command entries. Commands are null checked prior to calling `Consumer.accept(flags)` to avoid unnecessary NPEs.

Start listening for user input:


```
        ConsoleRunner.start();
```

Console arguments are added in the following format:

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

Reset the console back to its initial state:

```
        ConsoleRunner.reset();
```

Stop listening for user input when you are done:

```
        ConsoleRunner.stop();
```

Change the interval between console line reads:

```
        ConsoleRunner.setSleepInterval(500);
```
