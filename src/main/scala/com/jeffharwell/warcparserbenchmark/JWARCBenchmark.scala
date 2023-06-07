package com.jeffharwell.warcparserbenchmark

import org.apache.commons.math3.stat.descriptive.moment.{Mean, StandardDeviation}
import org.netpreserve.jwarc.{WarcReader, WarcRecord}

import java.io.{BufferedInputStream, File, FileInputStream}
import scala.collection.JavaConversions.asScalaIterator

/*
 * Class that does a benchmark on com.jeffharwell.commoncrawl.warcparser.Parser
 * The benchmark is done with no filtering
 */

class JWARCBenchmark(filename: String) {
  val times: scala.collection.mutable.ListBuffer[Double] = scala.collection.mutable.ListBuffer()
  var records: Int = 0

  def runBenchmark(): Unit = {
    // First determine the number of distinct WARC record IDs in the file
    val parser = new WarcReader(new BufferedInputStream(
      new FileInputStream(new File(filename))
    )).iterator()

    val warc_ids = parser.map{x => x.id()}

    println(s"File contains ${warc_ids.toList.size} WARC Record IDs")

    // Now run the full parse 10 times and measure the performance
    var new_records: Int = 0
    for (i <- 1 to 10) {
      new_records = 0
      // Create the parser and load the file
      val parser = new WarcReader(new BufferedInputStream(
        new FileInputStream(new File(filename))
      )).iterator()

      // Parse the entire file and measure the time
      val t1 = System.nanoTime()
      parser.foreach((wc: WarcRecord) => new_records += 1)
      val t2 = System.nanoTime()
      val time_in_ms: Double = (t2 - t1)/scala.math.pow(10,6)
      times += time_in_ms
      records += new_records
      println(s"Records per ms = ${new_records.toDouble / ((t2 - t1)/scala.math.pow(10,6))}")
    }

    val std = new StandardDeviation()
    val mean = new Mean()
    println(s"Mean Parse Time (ms): ${mean.evaluate(times.toArray)}")
    println(s"Standard Deviation (ms): ${std.evaluate(times.toArray)}")
    println(s"Mean Parse Time (ms) / Record: ${mean.evaluate(times.toArray)/new_records}")
    println(s"Records in Archive: ${new_records}")
    println(s"Parsed ${records} records during benchmark run")
  }
}
