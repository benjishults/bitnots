//COMPILER_OPTS -jvm-target 11 -Xjsr305=strict
//KOTLIN_OPTS -J-Xmx16g
@file:DependsOn("com.benjishults.bitnots:util:0.0.1-SNAPSHOT")
@file:DependsOn("com.benjishults.bitnots:language:0.0.1-SNAPSHOT")
@file:DependsOn("com.benjishults.bitnots:theory:0.0.1-SNAPSHOT")
@file:DependsOn("com.benjishults.bitnots:inference:0.0.1-SNAPSHOT")
@file:DependsOn("com.benjishults.bitnots:tableau:0.0.1-SNAPSHOT")
@file:DependsOn("com.benjishults.bitnots:prover:0.0.1-SNAPSHOT")
@file:DependsOn("com.benjishults.bitnots:tableau-prover:0.0.1-SNAPSHOT")
@file:DependsOn("com.benjishults.bitnots:parser:0.0.1-SNAPSHOT")
@file:DependsOn("com.benjishults.bitnots:tptp-parser:0.0.1-SNAPSHOT")
@file:DependsOn("com.benjishults.bitnots:test:0.0.1-SNAPSHOT")
@file:DependsOn("io.micrometer:micrometer-core:1.3.2")
@file:DependsOn("org.apache.commons:commons-csv:1.7")
@file:DependsOn("org.apache.commons:commons-csv:1.7")
@file:DependsOn("org.jetbrains.kotlin:kotlin-main-kts:1.3.61")
@file:DependsOn("org.jetbrains.kotlin:kotlin-scripting-common:1.3.61")
@file:DependsOn("org.jetbrains.kotlin:kotlin-script-runtime:1.3.61")
@file:DependsOn("org.jetbrains.kotlin:kotlin-scripting-jvm:1.3.61")
@file:DependsOn("org.jetbrains.kotlin:kotlin-scripting-jvm-host:1.3.61")

// @file:param("configFolder", String::class)

import com.benjishults.bitnots.test.createResultsForAllFof
import org.jetbrains.kotlin.script.util.DependsOn

System.setProperty("config", args[0])

createResultsForAllFof()
