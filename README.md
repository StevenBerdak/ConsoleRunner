# S-Console
A simple to use java based console. Simply add it to your project and call `Console.start()`.


```
        ConsoleRunner.start();
        ConsoleRunner.mapToFunction("time", flagArgs -> printTime());
        ConsoleRunner.mapToFunction("print", flagArgs -> {
            for (String flag : flagArgs) System.out.println(flag + " ");
        });
```

Call `Console.stop()` when you are done to stop listening for user input.

```
        void stopConsole() {
               ConsoleRunner.stop();
        }
```
