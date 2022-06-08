#!/bin/bash

make build
make springs file=${1}
cp ${2}* ../../graphics-hw3/src
cd ../../graphics-hw3/src
./build.sh ${2}
./animate.sh ${2}
cd ../../graphics-hw5/src

