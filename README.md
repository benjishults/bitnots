# bitnots
Theorem Prover

Currently, in order for some tests to pass, you must have the [TPTP](http://www.cs.miami.edu/~tptp/) library installed
and you must pass in 

    -Dconfig=<folder containing your tptp.properties file>

See `config/tptp.properties` for a sample config file.

Also, I had to comment out the formula named `single_quoted` in `Problems/SYN/SYN000+1.p` and the multi-line `/* */`
comment at the end because my parser could not deal.

There is an [issue](https://github.com/benjishults/bitnots/issues/7) to make this optional.

## Run UI

```bash
mvn clean install
```

Then hit the `javafx:run` target in `proof-service` to start a UI.  Doesn't do much so far.

Will create a folder named `.bitnots` in your home directory for your personal preferences.

## Run tests

```bash
mvn clean install
```

Some tests have problems if you use JVM 8.  JVM 11 works well.

## Enhanced testing

I'm working on making it easier to find the limits of what the prover can do and find regressions.  This 
work is in `common/test`.

My process is to edit the file `common/test/src/main/kotlin/com/benjishults/bitnots/test/EnhancedTests.kts`
then run it with kscript:

```bash
kscript common/test/src/main/kotlin/com/benjishults/bitnots/test/EnhancedTests.kts /home/benji/repos/benjishults/bitnots/common/tptp-parser/src/main/resources
```

The last argument is the path to your config folder (that contains the `tptp.properties` file.) 

If you edit any code other than `common/test/src/main/kotlin/com/benjishults/bitnots/test/EnhancedTests.kts`,
be sure to do a `mvn install` before running `kscript` again.

I'm still playing around with enhancements and there are still some manual steps but it's a promising direction.
