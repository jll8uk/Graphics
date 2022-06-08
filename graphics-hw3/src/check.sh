#!/bin/bash

while read p; do
  len=${#p};
  s=${p:0:$len-4};
  ./compare.sh $s;
done <implemented.txt


