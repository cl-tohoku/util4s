/*
 * Copyright (c) 2017.  Yuichiroh Matsubayashi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package yuima.util

import java.io.{File, PrintStream}
import java.math.MathContext

import yuima.util.progress.ProgressBar

package object control {
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

  implicit class WithPB[A, CC[A] <: TraversableOnce[A]](val collection: CC[A]) extends AnyVal {
    def withProgressBar = ProgressBar(collection)

    def withProgressBar(name: String) = ProgressBar(collection, name)
  }

}
