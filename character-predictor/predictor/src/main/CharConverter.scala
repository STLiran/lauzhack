package main

/**
  * Created by Loic on 19.11.2016.
  */
class Char2D {
  private matrix: Array[Array[Boolean]]

  def this(data: Array[Boolean], width: Int, height:Int) {
    this()

  }
}

object CharConverter {
  private val charmap = Map[Char, Char2D]()

  def charToDots(c: Char): Option[Char2D] = charmap.get(c)
}
