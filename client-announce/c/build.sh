#!/bin/bash
# 32bit version
i686-w64-mingw32-gcc -o client-announce-32 client_announce.c -lws2_32 -O3
# 64bit version
x86_64-w64-mingw32-gcc -o client-announce-64 client_announce.c -lws2_32 -O3
