package com.jeffharwell.warcparserbenchmark.run

import com.jeffharwell.commoncrawl.warcparser.{Parser, WARCRecord}
import com.jeffharwell.warcparserbenchmark.WarcParserBenchmark
import org.apache.commons.math3.stat.descriptive.moment.{Mean, StandardDeviation}
import org.netpreserve.jwarc.{WarcRecord => JWARCWarcRecord}
import org.netpreserve.jwarc.{WarcReader => JWARCWarcReader}
import org.jwat.warc.{WarcRecord => JWATWarcRecord}
import org.jwat.warc.{WarcReader => JWATWarcReader}
import org.jwat.warc.{WarcReaderFactory => JWATWarcReaderFactory}

import java.io.{BufferedInputStream, FileInputStream}
import java.net.URL
import java.nio.file.{Files, Paths}
import java.io.File
import scala.collection.JavaConversions.asScalaIterator
import scala.util.Random

object ParseRecords {
  def parse(t: String, bis: BufferedInputStream, warmup: Boolean = false): (Int, Double) = {
    if (t == "jwat") {
      var new_records: Int = 0

      if (warmup) {
        val reader: JWATWarcReader = JWATWarcReaderFactory.getReaderCompressed(bis)
        val parser = reader.iterator()
        parser.foreach((wc: JWATWarcRecord) => new_records += 0)
      }

      // Parse the entire file and measure the time
      val t1 = System.nanoTime()
      val reader: JWATWarcReader = JWATWarcReaderFactory.getReaderCompressed(bis)
      val parser = reader.iterator()
      parser.foreach((wc: JWATWarcRecord) => new_records += 1)
      val t2 = System.nanoTime()
      val time_in_ms: Double = (t2 - t1) / scala.math.pow(10, 6)
      Tuple2(new_records, time_in_ms)
    } else if (t == "jwarc") {
      var new_records: Int = 0

      if (warmup) {
        val parser = new JWARCWarcReader(bis).iterator()
        parser.foreach((wc: JWARCWarcRecord) => new_records += 0)
      }

      // Parse the entire file and measure the time
      val t1 = System.nanoTime()
      val parser = new JWARCWarcReader(bis).iterator()
      parser.foreach((wc: JWARCWarcRecord) => new_records += 1)
      val t2 = System.nanoTime()
      val time_in_ms: Double = (t2 - t1) / scala.math.pow(10, 6)
      Tuple2(new_records, time_in_ms)
    } else {
      // By default use the WarcParser Parser
      var new_records: Int = 0

      if (warmup) {
        val parser = Parser(bis)
        parser.foreach((wc: WARCRecord) => new_records += 0)
      }

      // Parse the entire file and measure the time
      val t1 = System.nanoTime()
      val parser = Parser(bis)
      parser.foreach((wc: WARCRecord) => new_records += 1)
      val t2 = System.nanoTime()
      val time_in_ms: Double = (t2 - t1) / scala.math.pow(10, 6)
      Tuple2(new_records, time_in_ms)

    }
  }
}

object BenchmarkParsers {
  def main(args: Array[String]): Unit = {
    val cc_url: String = "https://data.commoncrawl.org"
    val s3_wet_paths = List(
      "crawl-data/CC-MAIN-2016-50/segments/1480698542246.21/wet/CC-MAIN-20161202170902-00482-ip-10-31-129-80.ec2.internal.warc.wet.gz",
      "crawl-data/CC-MAIN-2016-50/segments/1480698542668.98/wet/CC-MAIN-20161202170902-00417-ip-10-31-129-80.ec2.internal.warc.wet.gz",
      "crawl-data/CC-MAIN-2016-50/segments/1480698542414.34/wet/CC-MAIN-20161202170902-00335-ip-10-31-129-80.ec2.internal.warc.wet.gz",
      "crawl-data/CC-MAIN-2016-50/segments/1480698541864.24/wet/CC-MAIN-20161202170901-00456-ip-10-31-129-80.ec2.internal.warc.wet.gz",
      "crawl-data/CC-MAIN-2016-50/segments/1480698541697.15/wet/CC-MAIN-20161202170901-00124-ip-10-31-129-80.ec2.internal.warc.wet.gz",
      "crawl-data/CC-MAIN-2016-50/segments/1480698542665.72/wet/CC-MAIN-20161202170902-00409-ip-10-31-129-80.ec2.internal.warc.wet.gz"
    )
    val local_wet_paths = List(
      "/home/jharwell/wet_archives/CC-MAIN-20161202170902-00482-ip-10-31-129-80.ec2.internal.warc.wet.gz",
      "/home/jharwell/wet_archives/CC-MAIN-20161202170902-00417-ip-10-31-129-80.ec2.internal.warc.wet.gz",
      "/home/jharwell/wet_archives/CC-MAIN-20161202170902-00335-ip-10-31-129-80.ec2.internal.warc.wet.gz",
      "/home/jharwell/wet_archives/CC-MAIN-20161202170901-00456-ip-10-31-129-80.ec2.internal.warc.wet.gz",
      "/home/jharwell/wet_archives/CC-MAIN-20161202170901-00124-ip-10-31-129-80.ec2.internal.warc.wet.gz",
      "/home/jharwell/wet_archives/CC-MAIN-20161202170902-00409-ip-10-31-129-80.ec2.internal.warc.wet.gz"
    )

    val parser_types: List[String] = List("jwarc", "jwat", "WARCParser")

    /*
     * Parse the list of local files and benchmark performance
     */

    parser_types.foreach(ptype => {
      println(s"Benchmarking Parser: ${ptype}")
      // Initialize our collection for this loop
      val times: scala.collection.mutable.ListBuffer[Double] = scala.collection.mutable.ListBuffer()
      var records: Int = 0

      // Do each collection of files a few times
      for (i <- 1 to 1) {
        local_wet_paths.foreach(filename => {
          // Should run through the BufferedInputStream once to warm up the disk cache
          // Open the file and create a BufferedInputStream
          val bis1 = new BufferedInputStream(
            new FileInputStream(new File(filename))
          )
          var value = bis1.read()
          while (value >= 0) {
            value = bis1.read()
          }

          // now do it for real
          val bis = new BufferedInputStream(
            new FileInputStream(new File(filename))
          )

          val ret_tuple = ParseRecords.parse(ptype, bis, false)
          val new_records: Int = ret_tuple._1
          val time_in_ms: Double = ret_tuple._2

          times += time_in_ms
          records += new_records
          println(s"  ${ptype}: Records in Archive: ${new_records}, ${new_records.toDouble / (time_in_ms)} records/ms")
          Thread.sleep(20000 + Random.nextInt(20000))
        })
        Thread.sleep(20000 + Random.nextInt(20000))
      }
      val std = new StandardDeviation()
      val mean = new Mean()
      println(s"${ptype}: Mean Parse Time (ms): ${mean.evaluate(times.toArray)}")
      println(s"${ptype}: Standard Deviation (ms): ${std.evaluate(times.toArray)}")
      println(s"${ptype}: Mean Parse Time (ms) / Record: ${mean.evaluate(times.toArray) / records}")
      println(s"${ptype}: Parsed ${records} records during benchmark run")
    })

    /*
     * Benchmark a set of files over the network
     */

    parser_types.foreach(ptype => {
      println(s"Benchmarking Parser: ${ptype}")
      // Initialize our collection for this loop
      val times: scala.collection.mutable.ListBuffer[Double] = scala.collection.mutable.ListBuffer()
      var records: Int = 0
      var new_records: Int = 0

      // Do each collection of files a few times
      for (i <- 1 to 1) {
        s3_wet_paths.foreach(wet_path => {
          val url = new URL(s"$cc_url/$wet_path")
          val urlconnection = url.openConnection()
          urlconnection.setReadTimeout(60000)
          urlconnection.setConnectTimeout(60000)
          val bis = new BufferedInputStream(urlconnection.getInputStream)
          val ret_tuple = ParseRecords.parse(ptype, bis)
          val new_records: Int = ret_tuple._1
          val time_in_ms: Double = ret_tuple._2

          times += time_in_ms
          records += new_records
          println(s"  ${ptype}: Records in Archive: ${new_records}, ${new_records.toDouble / (time_in_ms)} records/ms")
          Thread.sleep(20000 + Random.nextInt(20000))
        })
        Thread.sleep(20000 + Random.nextInt(20000))
      }
      val std = new StandardDeviation()
      val mean = new Mean()
      println(s"${ptype}: Mean Parse Time (ms): ${mean.evaluate(times.toArray)}")
      println(s"${ptype}: Standard Deviation (ms): ${std.evaluate(times.toArray)}")
      println(s"${ptype}: Mean Parse Time (ms) / Record: ${mean.evaluate(times.toArray) / records}")
      println(s"${ptype}: Parsed ${records} records during benchmark run")
    })
  }
}
