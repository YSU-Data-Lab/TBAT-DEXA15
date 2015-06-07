#!/bin/bash

if [ -z "$1" ]
  then
    echo "Please input your email password!"
    exit
fi

num_lines_1m=47660
num_lines_1g=$(( num_lines_1m * 1024 ))
#num_lines=${num_lines_1m}
num_lines=500
max_exp_times=10
max_run_times=1
pers="0.1 0.2 0.3 0.4 0.5"

cd ..

for (( i=1; i<=max_run_times; i++ ))
do
    now=$(date +%Y%m%d-%Hh%Mm%Ss)
    python exp_update/exp_updateData3.py ${num_lines} ${max_exp_times} ${pers}
    cp data/result.txt data/result-l${num_lines}-expt-${max_exp_times}${now}.txt
done
