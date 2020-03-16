# bitnots Theorem Prover

[![](https://jitpack.io/v/benjishults/bitnots.svg)](https://jitpack.io/#benjishults/bitnots)

## Build

```bash
mvn install
```

If you want to run tests against [TPTP](http://www.cs.miami.edu/~tptp/), you must have the  [TPTP](http://www.cs.miami.edu/~tptp/) library
installed locally and [this file](config/tptp.properties) must define `tptp.base.folder` to point to the local location
of the library.

Once that's ready, run

```bash
mvn install -PlocalTptp
```

That will run only tests that need TPTP.

## Run UI

```bash
mvn clean install
```

Then hit the `javafx:run` target in `proof-service` to start a UI.  Doesn't do much so far.

Will create a folder named `.bitnots` in your home directory for your personal preferences.

## Run tests

Be sure to edit the `.properties` files in the `config` folder.

```bash
mvn clean install
```

Some tests have problems if you use JVM 8.  JVM 11 works well.  (I use 11.J9.)

## Enhanced testing

I'm working on making it easier to find the limits of what the prover can do and find regressions.  This 
work is in `common/test`.

My process is to edit the file `common/test/src/main/kotlin/com/benjishults/bitnots/test/EnhancedTests.kts`
then run it with kscript:

```bash
kscript common/test/src/main/kotlin/com/benjishults/bitnots/test/EnhancedTests.kts /home/benji/repos/benjishults/bitnots/config
```

The last argument is the path to your config folder (that contains the `tptp.properties` file.) 

If you edit any code other than `common/test/src/main/kotlin/com/benjishults/bitnots/test/EnhancedTests.kts`,
be sure to do a `mvn install` before running `kscript` again.

I'm still playing around with enhancements and there are still some manual steps but it's a promising direction.
