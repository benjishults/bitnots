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

I use kscript

```bash
kscript common/test/scripts/EnhancedTests.kts
```

I'm still playing around with enhancements and there are still some manual steps but it's a promising direction.

The intention is that the system will track progress and notice regressions.
