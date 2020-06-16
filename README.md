![CI](https://github.com/anvo/sds/workflows/CI/badge.svg?branch=develop)
# Supreme Dedicated Server (SDS)
Supreme Dedicated Server (SDS) is a dedicated server for Supreme Snowboarding
which allows to play the game via Internet.

The original server that is included in the game is restricted to play games
on a local LAN only. It uses UDP broadcasting to announce the server, which 
is not possible via Internet. SDS started as a simple approach to forward the
UDP broadcasts to other hosts but - with some free time during Corona - it
turned into the implementation of a complete dedicated server.

SDS consists of two parts: the dedicated server that is running on a server
accessible via the Internet and a small executable (client-announce) that 
announces this server to the original game running on the client. To play 
via Internet, the original version of the Supreme Game is required. 
Using the client-announce executable, no modifications are needed on 
the client. 

## Dedicated Server

The Dedicated Server is running on a server in the Internet. It is accepting
incoming connections from the original game, handles all the game logic, 
data transfer, etc.

### Building
Gradle is used to build SDS. To produce a single jar which can be deployed
on a server, use the following command:
> /gradlew shadowJar

The resulting jar file is available in the folder: `build/libs/`

### Running
To run SDS, you need to execute the following command:
> java -jar sds.jar

A short help is available using
> java -jar sds.jar --help

Like the original server, SDS also supports a LAN mode. In this mode, SDS will
announce itself via UDP broadcast. No client-announce executable is required
in this case. SDS can be started in LAN mode using
> java -jar sds.jar --lan

By default, SDS will start in Internet mode and only reacts to clients using
the client-announce executable.

## Client-Announce
The client-announce executable is required in order to make SDS available to the
original Game client. It needs to run on the same machine that also executes the
Game.

### Building
Since the Game is intended to run on Windows, also the client-announce executable
needs to be running on Windows. Since I am mostly familiar on Linux, I have not
much clue on building on a Windows machine. 

Fortunately, you can use Linux to cross compile for Windows. The following script
will produce both, 32bit and 64bit binaries:
> client-announce/build.sh

I am happy to accept a PR to improve the situation.

### Running
The client-announce executable can either be executed using the command line CMD
on Windows or via double-click. It will ask for a hostname where the dedicated
server is running. You can enter either a hostname or a raw IP. 

The client-announce executable needs to be running in parallel to the original
Game client. 

## Status
SDS is a proof of concept that I have created in some free time during Corona.
It is neither fully tested nor of high quality code. It started as a completely
different project and then turned into a full fledged dedicated server. But I
am happy that it works :) 