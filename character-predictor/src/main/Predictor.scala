package main

import com.logitech.gaming.LogiLED

/**
  * Created by blueur on 19/11/16.
  */
class Predictor {
  private val probs = new ProbabilitiesDatabase(10)
  private val alphabet = "abcdefghijklmnopqrstuvwxyz -'"
  private val n = 10

  probs.initialize("big.txt")

  def computeProbability(prev: String, char: Char): Double = {
    probs.getNgramProbabilities(prev + char)/probs.getNgramProbabilities(prev)
  }

  def getNextChar(text: String): List[(Char, Double)] = {
    /* Not enough chars to do a prediction */
    if (text.length < 1) {
      return List(('\0', 0.0))
    }

    val last = text.takeRight(n).toLowerCase()

    alphabet.map(c => (c, computeProbability(last, c)))
            .sortWith((a, b) => a._2 > b._2)
            .toList
  }
}

object Main {
  def main(argv: Array[String]): Unit = {
    val keyboard = new KeyboardMessageDisplay()
    val predictor = new Predictor()

    var last = ' '
    var read = ' '
    var text = ""

    LogiLED.LogiLedInit()
    LogiLED.LogiLedSetLighting(0, 0, 0)

    while (read != '\r' && read != '\n') {
      read = io.StdIn.readChar()
      text += read

      var letters = predictor.getNextChar(text)
      var key = letters.head._1

      keyboard.ShowLetter(key, 100, 0, 0)
      keyboard.ShowLetter(last, 100, 0, 0)

      last = key

      letters.foreach(cp => print("%s (%.2f)".format(cp._1, cp._2)))
      println()
    }
  }
}