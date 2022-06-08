#!/bin/bash
for f in ${1}*.txt
do
   make run file=$f
done
