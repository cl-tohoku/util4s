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

  def withLogFile[A](log: PrintStream)(op: => A): A = withRedirectingTo(log, log)(op)

  def withRedirectingTo[A](out: PrintStream, err: PrintStream)(op: => A): A =
    Console.withOut(out) { Console.withErr(err)(op) }

  def withLogFile[A](file: File)(op: => A): A = withLogFile(IO.Out.ps(file))(op)

  def withRedirectingTo[A](out: String, err: String)(op: => A): A =
    withRedirectingTo(IO.Out.ps(out), IO.Out.ps(err))(op)

  def withRedirectingTo[A](out: File, err: File)(op: => A): A = withRedirectingTo(IO.Out.ps(out), IO.Out.ps(err))(op)

  def withRedirectingTo[A](out: String, err: File)(op: => A): A = withRedirectingTo(IO.Out.ps(out), IO.Out.ps(err))(op)

  def withRedirectingTo[A](out: File, err: String)(op: => A): A = withRedirectingTo(IO.Out.ps(out), IO.Out.ps(err))(op)

  def withRedirectingTo[A](out: String, err: PrintStream)(op: => A): A = withRedirectingTo(IO.Out.ps(out), err)(op)

  def withRedirectingTo[A](out: File, err: PrintStream)(op: => A): A = withRedirectingTo(IO.Out.ps(out), err)(op)

  def withRedirectingTo[A](out: PrintStream, err: String)(op: => A): A = withRedirectingTo(out, IO.Out.ps(err))(op)

  def withRedirectingTo[A](out: PrintStream, err: File)(op: => A): A = withRedirectingTo(out, IO.Out.ps(err))(op)

  implicit class RichDouble(value: Double) {
    def round(digit: Int): Double = BigDecimal(value, new MathContext(digit)).toDouble
  }

  /** utilizes try-with-resource syntax. */
  implicit class Using[A <: AutoCloseable](val resource: A) extends AnyVal {
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

  implicit class WithPBIterable[A, CC[A] <: Iterator[A]](val collection: CC[A]) extends AnyVal {
    def withProgressBar = {
      val (a, b) = collection.duplicate
      ProgressBar(a, b.size)
    }

    def withProgressBar(length: Int) = ProgressBar(collection, length)

    def withProgressBar(name: String, length: Int) = ProgressBar(collection, length, name)
  }

  implicit class WithPBTraversable[A, CC[A] <: Traversable[A]](val collection: CC[A]) extends AnyVal {
    def withProgressBar = ProgressBar(collection, collection.size)

    def withProgressBar(name: String) = ProgressBar(collection, collection.size, name)
  }
}
