import scala.collection.mutable.HashMap

val queue = new scala.collection.mutable.Queue[Char]
queue += 'a'
queue += 'e'
queue += 'q'
queue.toIterable.mkString("")
queue.size

var counts = Map.empty[String, Long]
val maxCounts = HashMap.empty[Int, Long]

counts.+=(("bleu", 3))
counts.+=(("bleu", 6))

counts

"rot".substring(0, 1)