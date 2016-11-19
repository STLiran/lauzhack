package main

import scala.collection.mutable.HashMap
//import scalaj.http._

/**
  * Created by blueur on 19.11.16.
  */
class ProbabilitiesDatabase(maxN: Int) {

  val counts = HashMap.empty[String, Long]
  val maxCounts = HashMap.empty[Int, Long]

  def initialize(): Any = {
    //val response: HttpResponse[String] = Http("http://foo.com/search").param("q","monkeys").asString
    println()
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
              ngramCount / maxCount
            case None =>
              -1.0
          }
        case None => -1.0
      }
    }
  }

}

object Test {
  def main(argv: Array[String]): Unit = {
    val probabilitiesDatabase = new ProbabilitiesDatabase(3);
    println(probabilitiesDatabase.getNgramProbabilities("qwe"))
  }
}