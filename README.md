# S-Console
A simple to use java based console. Simply add it to your project.

Start listening for user input:


```
        ConsoleRunner.start();
```

Map functions to method calls with optional flag arguments:

```
ConsoleRunner.mapToFunction("time", flagArgs -> printTime());
        ConsoleRunner.mapToFunction("print", flagArgs -> {
            for (String flag : flagArgs) System.out.println(flag + " ");
        });
```

Stop listening for user input when you are done: `Console.stop()`

```
        ConsoleRunner.stop();
```
