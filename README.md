# bitnots
Theorem Prover

Currently, in order for some tests to pass, you must have the [TPTP](http://www.cs.miami.edu/~tptp/) library installed and you must pass in 

    -Dconfig=<folder containing your tptp.properties file>

See `config/tptp.properties` for a sample config file.

Also, I had to comment out the formula named `single_quoted` in `Problems/SYN/SYN000+1.p` and the multi-line `/* */` comment at the end because my parser could not deal.

There is an [issue](https://github.com/benjishults/bitnots/issues/7) to make this optional.
