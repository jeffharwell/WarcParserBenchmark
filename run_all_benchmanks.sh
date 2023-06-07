#!/bin/bash
#sbt "runMain com.jeffharwell.warcparserbenchmark.run.BenchmarkWARCParser /home/jharwell/scratch/corrupt_warcconversion_1.wet.gz"
#sbt "runMain com.jeffharwell.warcparserbenchmark.run.BenchmarkWARCParser /home/jharwell/scratch/corrupt_warcconversion_2.wet.gz"
sbt "runMain com.jeffharwell.warcparserbenchmark.run.BenchmarkParsers"
