package com.jeffharwell.warcparserbenchmark.run

import com.jeffharwell.commoncrawl.warcparser.Parser
import com.jeffharwell.warcparserbenchmark.WarcParserBenchmark
import java.nio.file.{Paths, Files}

object BenchmarkWARCParser {
  def main(args: Array[String]): Unit = {
    val filename: String = if (args.size > 0) {
                      args(0)
                    } else {
                      println("Please specify a file name on the command line")
                      System.exit(1)
                      ""
                    }
    println(s"Looking for filename ${filename}")

    if (!Files.exists(Paths.get(filename))) {
      print(s"Can't find file ${filename}.")
      System.exit(1)
    }
    val wpb = new WarcParserBenchmark(filename)
    wpb.runBenchmark()
  }
}
