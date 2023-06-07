#!/bin/bash
#sbt "runMain com.jeffharwell.warcparserbenchmark.run.BenchmarkJWARCParser /home/jharwell/scratch/corrupt_warcconversion_1.wet.gz"
#sbt "runMain com.jeffharwell.warcparserbenchmark.run.BenchmarkJWARCParser /home/jharwell/scratch/corrupt_warcconversion_2.wet.gz"
sbt "runMain com.jeffharwell.warcparserbenchmark.run.BenchmarkJWARCParser /home/jharwell/scratch/CC-MAIN-20161202170901-00350-ip-10-31-129-80.ec2.internal.warc.wet.gz"
