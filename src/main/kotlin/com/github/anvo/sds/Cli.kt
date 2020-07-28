package com.github.anvo.sds

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
fun main(args: Array<String>) {

    var lan = false

    val it = args.iterator()
    while(it.hasNext()) {
        when (val arg = it.next()) {
            "-h", "--help" -> {
                return help()
            }
            "--lan" -> {
                lan = true
            }
            "--log" -> {
                if(!it.hasNext()) {
                    println("No log category given.")
                    return
                }
                Log.levels.clear()
                Log.levels.addAll(loglevel(it.next()))
            }
            "-v", "--version" -> {
                return version()
            }
            else -> {
                println("Invalid argument: $arg")
                return help()
            }
        }
    }
    /*print*/ version()
    DedicatedServer().run(lan)
}

fun help() {
    println("""
    |Usage: java -jar sds.jar [-v | --version] [-h | --help] [--lan]
    |                           [--log server[,game][,packet][,traffic]]
    |   
    |  --lan        start the server in lan mode; see below
    |  --log        log messages from the provided category: server,
    |               game, packet, traffic. Default: server,game
    |  
    | Internet vs Lan mode:
    | The server will start in internet mode by default. In internet mode, 
    | the server will only announce itself when a new connection is created 
    | via the separate client-announce program. If the server is started in
    | lan mode, it will announce itself via UDP broadcast in the local network
    | in the same way as the original server does.
    """.trimMargin())
}

@ExperimentalStdlibApi
fun version() {
    println("Supreme Dedicated Server v${DedicatedServer.VERSION}")
}

fun loglevel(levels:String): Set<Log.Level> {
    return levels.split(',').map {
        when(it) {
            "traffic" -> Log.Level.TRAFFIC
            "packet" -> Log.Level.PACKET
            "game" -> Log.Level.GAME
            else -> Log.Level.SERVER
        }
    }.toSet()
}