# Humanbenchmark bots made in Java
Try out the tests here: [https://humanbenchmark.com](https://humanbenchmark.com)

## Tools used
* JDK 17
* JavaFX 17
* Selenium 4.17.0
* Gradle 7.5.1

## Bots
| Test Name | Run command | Requires Selenium
--- | --- | ---
| [Aim Trainer](https://humanbenchmark.com/tests/aim) | `./gradlew :AimTest:run` | No
| [Chimp Test](https://humanbenchmark.com/tests/chimp) | `./gradlew :ChimpTest:run` | Yes
| [Sequence Memory](https://humanbenchmark.com/tests/sequence) | `./gradlew :MemoryTest:run` | No
| [Number Memory](https://humanbenchmark.com/tests/number-memory)  | `./gradlew :NumberMemory:run` | Yes
| [Reaction Time](https://humanbenchmark.com/tests/reactiontime) | `./gradlew :ReactionTest:run` | No
| [Typing](https://humanbenchmark.com/tests/typing) | `./gradlew :TypingTest:run` | Yes
| [Verbal Memory](https://humanbenchmark.com/tests/verbal-memory) | `./gradlew :VerbalTest:run` | Yes
| [Visual Memory](https://humanbenchmark.com/tests/memory) | `./gradlew :VisualMemory:run` | No

If on Windows, replace `./gradlew` with `.\gradlew.bat`

## Important notes
* Those bots are configured to run, based on *my* settings, be sure to change the variables marked by the comment `// CHANGE` according to *your* settings.
* The "Visual Memory" bot has a static variable named `DEBUG`, you can change the value to true so that you can move the window around and configure properly the screenshot area (when false the bot runs faster).
* Be sure to change the path of the chrome driver for the bots that use Selenium.

## Download selenium
Some bots use selenium to control the browser in order to get the current page source code.  
If you want to use the bots marked with `Yes`, then you have to follow these steps:
* Download from [here](https://www.selenium.dev/downloads/) the latest Selenium zip file for Java.
* Put the contents in a folder named `jars`, located in the root directory of this repository. (Be sure to have also a `lib` directory inside the `jars` one)
* The bots that use selenium will automatically find all the jars.

## Some test results
...