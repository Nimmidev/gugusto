#!/bin/sh

mkdir -p build/bin
javac -target 1.8 -source 1.8 -d build $(find src -name '*.java')
jar cMf build/bin/game.jar -C resources . -C build .
