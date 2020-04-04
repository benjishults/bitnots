# bitnots Theorem Prover

[![](https://jitpack.io/v/benjishults/bitnots.svg)](https://jitpack.io/#benjishults/bitnots)

## Build

```bash
mvn clean install
```

If you want to run tests against [TPTP](http://www.cs.miami.edu/~tptp/), you must have the  [TPTP](http://www.cs.miami.edu/~tptp/) library
installed locally and [this file](config/tptp.properties) must define `tptp.base.folder` to point to the local location
of the library.

Once that's ready, run

```bash
mvn install -P localTptp
```

That will run tests that need TPTP.

Some tests have problems if you use JVM 8.  JVM 11 and higher work well.

## Run UI

In the appropriate app folder, run

`mvn javafx:run`

You may need to enable the `localTptp` profile the first time you run an app that uses TPTP.

`mvn javafx:run -P localTptp`

The UI will create a folder named `.bitnots` in your home directory for your personal preferences.

To debug

`mvn javafx:run@debug -P localTptp`

## Enhanced testing

I'm working on making it easier to find the limits of what the prover can do and find regressions.  This 
work is in `common/test` now and moving toward a UI in `app/regression`.

My process is to edit the file `common/test/src/main/kotlin/com/benjishults/bitnots/test/EnhancedTests.kts`
then run it with kscript:

```bash
kscript common/test/src/main/kotlin/com/benjishults/bitnots/test/EnhancedTests.kts /home/benji/repos/benjishults/bitnots/config
```

The last argument is the path to your config folder (that contains the `tptp.properties` file.) 

If you edit any code other than `common/test/src/main/kotlin/com/benjishults/bitnots/test/EnhancedTests.kts`,
be sure to do a `mvn install` before running `kscript` again.

I'm still playing around with enhancements and there are still some manual steps but it's a promising direction.
