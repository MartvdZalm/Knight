#!/bin/bash

if [ $# -ne 2 ]; then
  echo "Usage: $0 <number_of_iterations> <test_name>"
  exit 1
fi

iterations=$1
test_name=$2
log_dir="logs/stress_testing"
mkdir -p $log_dir

log_file="$log_dir/${test_name}_$(date +'%Y%m%d_%H%M%S').log"

run_test() {
  local i=$1
  echo "Running test iteration $i: $test_name"
  mvn -Dtest=$test_name test
  if [ $? -ne 0 ]; then
    echo "Test failed on iteration $i: $test_name" | tee -a $log_file
    exit 1
  fi
}

{
  for i in $(seq 1 $iterations)
  do
    run_test $i
  done

  echo "All tests passed for $test_name over $iterations iterations"
} 2>&1 | tee -a $log_file
