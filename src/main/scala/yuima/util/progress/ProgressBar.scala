package yuima.util.progress

import java.time.format.DateTimeFormatter
import java.time.{Duration, LocalDateTime, LocalTime, Period}

import org.jline.terminal.TerminalBuilder

import scala.collection.GenTraversableOnce
import scala.collection.generic.FilterMonadic
import scala.collection.immutable.List._
import scala.collection.mutable.ArrayBuffer

/** @author Yuichiroh Matsubayashi
  *         Created on 2016/07/01.
  */

import yuima.util.progress.InfoType._

class ProgressBar[A, CC[X] <: TraversableOnce[X]](coll: CC[A],
                                                  total: Int,
                                                  name: String = "",
                                                  maxWidth: Int = 100,
                                                  infoTypes: List[InfoType.Value] = List(InfoType.NAME,
                                                                                         InfoType.MESSAGE,
                                                                                         InfoType.PARCENT,
                                                                                         InfoType.BAR,
                                                                                         InfoType.COUNTER,
                                                                                         InfoType.TIME,
                                                                                         InfoType.ETA,
                                                                                         InfoType.SPEED),
                                                  startChar: String = "[",
                                                  doneChar: String = "=",
                                                  currentChar: String = ">",
                                                  remainingChar: String = " ",
                                                  endChar: String = "]") {
  private val timeStart = LocalDateTime.now()
  private val counterDigits = digits(total)
  private val (prefixInfo, rest) = infoTypes.span(_ != BAR)
  private val unit = (total / 100) max 1
  private val secondOfDay = 24 * 60 * 60

  ProgressBar.bars.append(this)

  var message = ""
  private var numIter = 1
  private var firstTime = true
  private var time = Duration.between(timeStart, LocalDateTime.now())

  private var _status = ""

  def status: String = _status

  def foreach[U](f: A => U): Unit = coll.foreach(count[A] _ andThen f andThen postProcess)

  def map[B](f: A => B): CC[B] = coll.map(count[A] _ andThen f andThen postProcess).asInstanceOf[CC[B]]

  def filter(f: A => Boolean): CC[A] = coll.filter(count[A] _ andThen f andThen postProcess).asInstanceOf[CC[A]]

  def withFilter(f: A => Boolean): FilterMonadic[A, CC[A]] = coll.withFilter(
    count[A] _ andThen f andThen postProcess).asInstanceOf[FilterMonadic[A, CC[A]]]

  def flatMap[B](f: A => GenTraversableOnce[B]): CC[B] = coll.flatMap(
    count[A] _ andThen f andThen postProcess).asInstanceOf[CC[B]]

  private def count[B](b: B) = {
    if (numIter % unit == 0 || numIter == total || numIter == 1) {
      time = Duration.between(timeStart, LocalDateTime.now())
      create()
      ProgressBar.show()
    }
    b
  }

  private def create() = {
    val terminalWidth = TerminalBuilder.terminal().getWidth

    def typeToString(t: InfoType.Value) = t match {
      case NAME => name + ":"
      case MESSAGE => message
      case PARCENT => percent
      case COUNTER => counter
      case TIME => elapsedTime
      case ETA => timeLeft
      case SPEED => speed
      case BAR => throw new IllegalStateException("InfoType.BAR is not expected here.")
    }

    def percent = f"${ numIter.toDouble / total * 100 }%3.0f %%"

    def speed = f"${ numIter.toDouble / (time.toNanos / 1000000000.0) }%,.0f its/sec"

    def counter = (s"%,${ counterDigits }d / %,${ counterDigits }d").format(numIter, total)

    def elapsedTime = "PAST:" + durationString(time)

    def timeLeft = {
      val eta = time.dividedBy(numIter).multipliedBy(total - numIter)
      "ETA:" + durationString(eta)
    }

    def durationString(duration: Duration) = {
      val days = duration.toDays
      val period =
        if (days > 0) Period.ofDays(duration.toDays.toInt).toString.drop(1)
        else ""
      val remainder = duration.minusDays(days).withNanos(0)
      val dateTime = LocalTime.ofSecondOfDay(remainder.getSeconds).format(DateTimeFormatter.ofPattern("HH:mm:ss"))
      period + dateTime
    }

    def bar(prefix: String, suffix: String) = {
      val width = ((maxWidth min terminalWidth) - (prefix + suffix).length - 4) max 1

      val progress = Math.ceil((numIter.toFloat / total) * width).toInt
      val remaining = width - progress
      startChar + {
        if (numIter == total) doneChar * progress
        else doneChar * (progress - 1) + currentChar
      } +
      remainingChar * remaining +
      endChar
    }

    lazy val prefix = prefixInfo.map(typeToString).mkString(" ")
    lazy val (body, suffix) = rest match {
      case Nil => ("", "")
      case head :: tail =>
        val suf =
          if (tail.nonEmpty) tail.map(typeToString).mkString(" ")
          else ""
        (bar(prefix, suf), suf)
    }

    val str = Seq(prefix, body, suffix).mkString(" ")
    val tailSpacer = " " * (terminalWidth - str.length)

    _status = str + tailSpacer
  }

  private def postProcess[B](b: B) = {
    numIter += 1
    if (numIter > total)
      ProgressBar.bars = ProgressBar.bars.filterNot(_ eq this)
    if (ProgressBar.bars.isEmpty) Console.err.println()
    b
  }

  private def digits(num: Int, d: Int = 1): Int = num / 10 match {
    case 0 => d
    case n => digits(n, d + 1)
  }
}

object ProgressBar {

  private var bars = ArrayBuffer[ {def status: String}]()
  private var last = 0

  def show(): Unit = {
    val size = bars.size
    val backwards =
      if (last != size) {
        val b = last - 1
        last = size
        b
      }
      else size - 1

    if (backwards > 0)
      Console.err.print(s"\u001B[${ backwards }A\r")
    else
      Console.err.print("\r")

    Console.err.print(bars.map(_.status).mkString("\n"))
    Console.err.flush()
  }

  import InfoType._

  def apply[A, CC[X] <: TraversableOnce[X]](coll: CC[A], name: String, maxWidth: Int,
                                            format: String): ProgressBar[A, CC] = {
    val Array(startChar, doneChar, currentChar, remainingChar, endChar) = format.split("")

    if (coll.isTraversableAgain) {
      new ProgressBar(coll, coll.size, name, maxWidth,
                      List(NAME, MESSAGE, BAR, COUNTER, TIME, ETA, SPEED),
                      startChar, doneChar, currentChar, remainingChar, endChar)
    }
    else {
      val (a, b) = coll.toIterator.duplicate
      new ProgressBar(a.asInstanceOf[CC[A]], b.size, name, maxWidth,
                      List(NAME, MESSAGE, BAR, COUNTER, TIME, ETA, SPEED),
                      startChar, doneChar, currentChar, remainingChar, endChar)
    }
  }

  def apply[A, CC[A] <: TraversableOnce[A]](coll: CC[A], name: String): ProgressBar[A, CC] = {
    if (coll.isTraversableAgain) new ProgressBar(coll, coll.size, name)
    else {
      val (a, b) = coll.toIterator.duplicate
      new ProgressBar(a.asInstanceOf[CC[A]], b.size, name)
    }
  }

  def apply[A, CC[A] <: TraversableOnce[A]](coll: CC[A]): ProgressBar[A, CC] = {
    if (coll.isTraversableAgain) new ProgressBar(coll, coll.size)
    else {
      val (a, b) = coll.toIterator.duplicate
      new ProgressBar(a.asInstanceOf[CC[A]], b.size)
    }
  }
}