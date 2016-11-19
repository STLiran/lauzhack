package main

import scala.io.Source
import scala.util.matching.Regex

import scala.collection.mutable.HashMap

/**
  * Created by blueur on 19.11.16.
  */
class ProbabilitiesDatabase(maxN: Int) {

  var counts = HashMap.empty[String, Long]
  var maxCounts = HashMap.empty[Int, Long]
  val alphabet = "abcdefghijklmnopqrstuvwxyz -'"

  def addNgram(ngram: String): Unit = {
    counts.get(ngram) match {
      case Some(count) => counts.+=((ngram, count + 1))
      case None => counts.+=((ngram, 1))
    }
    val n = ngram.length
    maxCounts.get(n) match {
      case Some(count) => maxCounts.+=((n, count + 1))
      case None => maxCounts.+=((n, 1))
    }
  }

  def initialize(filename: String): Any = {
    //val response: HttpResponse[String] = Http("http://foo.com/search").param("q","monkeys").asString
    val queue = new scala.collection.mutable.Queue[Char]
    Source.fromFile(filename).getLines().foreach(line => {
      line.iterator.foreach(char => {
        val c = char.toLower
        if (alphabet.contains(c)) {
          queue += c
          if (queue.size > maxN) {
            queue.dequeue()
          }
          val ngram = queue.toIterable.mkString("")
          for (i <- 1 to queue.size) {
            addNgram(ngram.substring(0, i))
          }
        } else {
          // TODO
        }
      })
    })
  }

  def getNgramProbabilities(ngram: String): Double = {
    val n: Int = ngram.length()
    if (n > maxN) {
      -1.0
    } else {
      counts.get(ngram) match {
        case Some(ngramCount) =>
          maxCounts.get(n) match {
            case Some(maxCount) =>
              ngramCount.toDouble / maxCount.toDouble
            case None =>
              -1.0
          }
        case None => -1.0
      }
    }
  }

}
