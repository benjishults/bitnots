# bitnots Theorem Prover

[![](https://jitpack.io/v/benjishults/bitnots.svg)](https://jitpack.io/#benjishults/bitnots)

## Build

### Build without TPTP tests

```bash
mvn clean install
```

### Build without TPTP tests

If you want to run tests against [TPTP](http://www.cs.miami.edu/~tptp/), you must 

1. have the  [TPTP](http://www.cs.miami.edu/~tptp/) library
installed locally
2. the `tptp.base.folder` system property must be defined when you run tests
   1. an easy way to have the property defined is to set it in [this file](config/tptp.properties)
   2. you can store that `tptp.properties` configuration file elsewhere by redefining the system property
   `config` to point to the folder containing that file.

Once that's ready, run

```bash
mvn install -P localTptp
```

That will run tests that need TPTP.

Some tests have problems if you use JVM 8.  JVM 11 and 14 are tested and work well.  (I use the J9 versions.)

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
