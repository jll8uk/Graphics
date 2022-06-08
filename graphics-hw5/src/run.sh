#!/bin/bash

make build
make springs file=${1}
cp ${2}* ../../graphics-hw3/src
cd ../../graphics-hw3/src
./build.sh ${2}
#./animate.sh ${2}
./compare.sh ${2} ${3} 
cp anim.png  ../../graphics-hw5/src
cp ${2}.gif ../../graphics-hw5/src/gifs
cd ../../graphics-hw5/src
rm ${2}*.txt
