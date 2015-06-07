#!/bin/bash

if [ -z "$1" ]
  then
    echo "Please input your email password!"
    exit
fi

now=$(date +%Y%m%d-%Hh%Mm%Ss)

num_lines_1m=47660
num_lines_1g=$(( num_lines_1m * 1024 ))

num_lines=10000
max_exp_times=10
pers="0.1 0.2 0.3 0.4 0.5"

cd ..

python exp_update/exp_updateData3.py ${num_lines} ${max_exp_times} ${pers}

