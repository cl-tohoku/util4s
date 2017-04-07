package yuima.util

import java.io.{File, PrintStream}
import java.math.MathContext

import yuima.util.progress.ProgressBar

import scala.collection.Traversable

/** Definition of new control structures.
  *
  * @author Yuichiroh Matsubayashi
  *         Created on 2016/02/17.
  */
object Controls {
  /** repeat operation n times.
    *
    * @param n  A number of reputation.
    * @param op An operation.
    */
  def repeat(n: Int)(op: => Unit) {
    for (i <- 0 until n) op
  }

  def leafFiles(path: String): Seq[File] = leafFiles(new File(IO.expand(path)))

  def leafFiles(path: File)(implicit fileFilter: File => Boolean = f => true): Seq[File] = {
    if (path.isDirectory) path.listFiles().sorted.flatMap(leafFiles)
    else Array(path).filter(fileFilter)
  }

  def withLogFile[A](file: String)(op: => A): A = withLogFile(IO.Out.ps(file))(op)

  def withLogFile[A](log: PrintStream)(op: => A): A = Console.withOut(log) { Console.withErr(log)(op) }

  def withLogFile[A](file: File)(op  : => A): A = withLogFile(IO.Out.ps(file))(op)

  implicit class RichDouble(value: Double) {
    def round(digit: Int): Double = BigDecimal(value, new MathContext(digit)).toDouble
  }

  /** utilizes try-with-resource syntax. */
  implicit class Using[A <: AutoCloseable](resource: A) {
    def foreach[B](op: A => B): Unit = {
      try op(resource)
      catch { case e: Exception => throw e }
      finally resource.close()
    }

    def map[B](op: A => B): B = {
      try op(resource)
      catch { case e: Exception => throw e }
      finally resource.close()
    }
  }

  implicit class FileName(val file: File) extends AnyVal {
    def extension: String = {
      val name = file.getName
      name.substring(name.lastIndexOf('.') + 1, name.length)
    }

    def basename: String = {
      val name = file.getName
      name.substring(0, name.lastIndexOf('.'))
    }
  }

  implicit class Repeating(val num: Int) extends AnyVal {
    def times[A](op: => A): Seq[A] = {
      for (_ <- 0 until num) yield op
    }
  }

  implicit class WithPB[A, CC[X] <: Traversable[X]](val collection: CC[A]) extends AnyVal {
    def withProgressBar = ProgressBar(collection)

    def withProgressBar(name: String) = ProgressBar(collection, name)
  }

}
