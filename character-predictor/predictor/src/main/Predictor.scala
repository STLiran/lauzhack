package main

/**
  * Created by blueur on 19/11/16.
  */
class Predictor {
  private val probs = new ProbabilitiesDatabase(10)
  private val alphabet = "abcdefghijklmnopqrstuvwxyz -'"
  private val n = 5

  probs.initialize("big.txt")

  def computeProbability(prev: String, char: Char): Double = {
    probs.getNgramProbabilities(prev + char)/probs.getNgramProbabilities(prev)
  }

  def getNextChar(text: String): List[(Char, Double)] = {
    /* Not enough chars to do a prediction */
    if (text.length < 1) {
      return List(('\0', 0.0))
    }

    val last = text.takeRight(n - 1).toLowerCase()

    alphabet.map(c => (c, computeProbability(last, c)))
            .sortWith((a, b) => a._2 > b._2)
            .toList
  }
}

object Main {
  def main(argv: Array[String]): Unit = {
    val predictor = new Predictor()

    var read = ' '
    var text = ""

    while (read != '\r' && read != '\n') {
      read = io.StdIn.readChar()
      text += read

      //print("\r%s+%s".format(text, predictor.getNextChar(text).take(1)))
      predictor.getNextChar(text).foreach(cp => print("%c(%.2f) ".format(cp._1, cp._2)))
      println('\n' + text)
    }
  }
}