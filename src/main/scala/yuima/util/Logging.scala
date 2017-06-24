package yuima.util

import scala.collection.mutable

/** Print message to STDERR with various logging levels.
  * The levels compose of ALL, DEBUG, LOG, INFO, WARN, ERROR, NONE.
  *
  * Usage: First, mixing this trait in a target class/object. Then set log level you want to output.
  * Finally, call functions as you like.
  *
  * @author Yuichiroh Matsubayashi
  */
trait Logging {
  /** Logging levels used for Object Debug.
    * ordering is ALL > DEBUG > LOG > INFO > WARN > ERROR > NONE. */

  object LoggingLevel extends Enumeration {
    type LoggingLevel = Value
    val ALL, DEBUG, LOG, INFO, WARN, ERROR, NONE = Value
  }

  import LoggingLevel._

  val EPOCH_LINE = 20

  private val iterationCounter = mutable.Map[String, Int]()
  private val epochCounter = mutable.Map[String, Int]()

  protected var loggingLevel = INFO

  def watchIteration(name: String, periodicity: Int, lineLimit: Int)(printer: String => Unit) {
    val count = (iterationCounter.getOrElse(name, 0) + 1) % periodicity
    if (count == 0) {
      val epoch = (epochCounter.getOrElse(name, 0) + 1) % EPOCH_LINE
      if (epoch == 0) {
        printer("\n")
      }
      printer("$name")
      epochCounter.put(name, epoch)
    }
  }

  def debugln(): Unit = loggingLevel match {
    case ALL => Console.err.println()
    case DEBUG => Console.err.println()
    case _ =>
  }

  def debugln(s: => Any): Unit = loggingLevel match {
    case ALL => Console.err.println(s)
    case DEBUG => Console.err.println(s)
    case _ =>
  }

  def debugln(color: Color)(s: => Any): Unit = loggingLevel match {
    case ALL => Console.err.println(color.toString + s + Console.RESET)
    case DEBUG => Console.err.println(color.toString + s + Console.RESET)
    case _ =>
  }


  def debug(s: => Any): Unit = loggingLevel match {
    case ALL => Console.err.print(s)
    case DEBUG => Console.err.print(s)
    case _ =>
  }

  def logln(): Unit = loggingLevel match {
    case ALL => Console.err.println()
    case DEBUG => Console.err.println()
    case LOG => Console.err.println()
    case _ =>
  }

  def logln(s: => Any): Unit = loggingLevel match {
    case ALL => Console.err.println(Console.BLUE + s + Console.RESET)
    case DEBUG => Console.err.println(Console.BLUE + s + Console.RESET)
    case LOG => Console.err.println(Console.BLUE + s + Console.RESET)
    case _ =>
  }

  def log(s: => Any): Unit = loggingLevel match {
    case ALL => Console.err.print(Console.BLUE + s + Console.RESET)
    case DEBUG => Console.err.print(Console.BLUE + s + Console.RESET)
    case LOG => Console.err.print(Console.BLUE + s + Console.RESET)
    case _ =>
  }

  def info(s: => Any): Unit = loggingLevel match {
    case ALL => Console.err.print(Console.GREEN + s + Console.RESET)
    case DEBUG => Console.err.print(Console.GREEN + s + Console.RESET)
    case LOG => Console.err.print(Console.GREEN + s + Console.RESET)
    case INFO => Console.err.print(Console.GREEN + s + Console.RESET)
    case _ =>
  }

  def infoln(): Unit = loggingLevel match {
    case ALL => Console.err.println()
    case DEBUG => Console.err.println()
    case LOG => Console.err.println()
    case INFO => Console.err.println()
    case _ =>
  }

  def infoln(s: => Any): Unit = loggingLevel match {
    case ALL => Console.err.println(Console.GREEN + s + Console.RESET)
    case DEBUG => Console.err.println(Console.GREEN + s + Console.RESET)
    case LOG => Console.err.println(Console.GREEN + s + Console.RESET)
    case INFO => Console.err.println(Console.GREEN + s + Console.RESET)
    case _ =>
  }

  def warn(s: => Any): Unit = loggingLevel match {
    case ALL => Console.err.print(Console.YELLOW + s + Console.RESET)
    case DEBUG => Console.err.print(Console.YELLOW + s + Console.RESET)
    case LOG => Console.err.print(Console.YELLOW + s + Console.RESET)
    case INFO => Console.err.print(Console.YELLOW + s + Console.RESET)
    case WARN => Console.err.print(Console.YELLOW + s + Console.RESET)
    case _ =>
  }

  def error(s: => Any): Unit = loggingLevel match {
    case ALL => Console.err.print(Console.RED + s + Console.RESET)
    case DEBUG => Console.err.print(Console.RED + s + Console.RESET)
    case LOG => Console.err.print(Console.RED + s + Console.RESET)
    case INFO => Console.err.print(Console.RED + s + Console.RESET)
    case WARN => Console.err.print(Console.RED + s + Console.RESET)
    case ERROR => Console.err.print(Console.RED + s + Console.RESET)
    case _ =>
  }

  def errorln(): Unit = loggingLevel match {
    case ALL => Console.err.println()
    case DEBUG => Console.err.println()
    case LOG => Console.err.println()
    case INFO => Console.err.println()
    case WARN => Console.err.println()
    case ERROR => Console.err.println()
    case _ =>
  }

  def errorln(s: => Any): Unit = loggingLevel match {
    case ALL => Console.err.println(Console.RED + s + Console.RESET)
    case DEBUG => Console.err.println(Console.RED + s + Console.RESET)
    case LOG => Console.err.println(Console.RED + s + Console.RESET)
    case INFO => Console.err.println(Console.RED + s + Console.RESET)
    case WARN => Console.err.println(Console.RED + s + Console.RESET)
    case ERROR => Console.err.println(Console.RED + s + Console.RESET)
    case _ =>
  }
}