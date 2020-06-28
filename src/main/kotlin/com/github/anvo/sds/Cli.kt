package com.github.anvo.sds

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
fun main(args: Array<String>) {

    var lan = false
    var traffic = false

    val it = args.iterator()
    while(it.hasNext()) {
        when (val arg = it.next()) {
            "-h", "--help" -> {
                return help()
            }
            "--lan" -> {
                lan = true
            }
            "-t", "--traffic" -> {
                traffic = true
            }
            "-v", "--version" -> {
                version()
            }
            else -> {
                println("Invalid argument: $arg")
                return help()
            }
        }
    }
    DedicatedServer().run(lan, traffic)
}

fun help() {
    println("""
    |Usage: java -jar sds.jar [-v | --version] [-h | --help] [--lan]
    |                           [--traffic]
    |   
    |  --lan        start the server in lan mode; see below
    |  --traffic    log all traffic from and to all clients
    |  
    | Internet vs Lan mode:
    | The server will start in internet mode by default. In internet mode, 
    | the server will only announce itself when a new connection is created 
    | via the separate client-announce program. If the server is started in
    | lan mode, it will announce itself via UDP broadcast in the local network
    | in the same way as the original server does.
    """.trimMargin())
}

fun version() {
    println("Supreme Dedicated Server v0.2.0-dev")
}