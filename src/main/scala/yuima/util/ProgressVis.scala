package yuima.util

/** prints a certain character to a standard error output when it executes the show() method several times.
  *
  * @author Yuichiroh Matsubayashi
  *         Created on 2016/01/14.
  * @param interval the number of execution times needed to printing one character
  * @param lineMax  maximum char length per line
  */
class ProgressVis(interval: Int, lineMax: Int, char: Char) {
  var count = 0
  var era = 0

  def init() = {
    count = 0
    era = 0
  }

  def show() {
    count += 1
    if (count % interval == 0) {
      Console.err.print(char)
      count = 0
      era += 1
      if (era % lineMax == 0) {
        Console.err.println()
        era = 0
      }
    }
  }

  def end() {
    count = 0
    era = 0
    Console.err.println()
  }
}

object ProgressVis {
  def apply(interval: Int, line: Int, char: Char = '.') = new ProgressVis(interval, line, char)
}