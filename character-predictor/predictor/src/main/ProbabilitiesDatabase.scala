package main

import scala.io.Source

import scala.collection.mutable.HashMap

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
    println("Loading " + filename)
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
          val n = queue.size
          for (i <- 0 until n) {
            addNgram(ngram.substring(i, n))
          }
        } else {
          queue.dequeueAll(_ => true)
        }
      })
    })
    println("Loaded " + filename)
  }

  def getNgramProbabilities(ngram: String): Double = {
    val n: Int = ngram.length()
    if (n > maxN) {
      throw new IllegalArgumentException("the ngram must not be longer than " + maxN);
    } else {
      counts.get(ngram) match {
        case Some(ngramCount) =>
          maxCounts.get(n) match {
            case Some(maxCount) =>
              ngramCount.toDouble / maxCount.toDouble
            case None =>
              throw new IllegalStateException("Not initialized")
          }
        case None => throw new IllegalStateException("Not initialized")
      }
    }
  }

}
