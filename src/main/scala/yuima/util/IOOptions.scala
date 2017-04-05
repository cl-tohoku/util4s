package yuima.util

import java.io.File

/** @author Yuichiroh Matsubayashi
  *         Created on 2016/02/24.
  */
trait IOOptions extends OptionParser {
  protected var _in: File
  protected var _out: File

  override def options: Seq[CommandLineElement] = Seq(
    Opt[File]("i", "input")("input") { file => _in = file },
    Opt[File]("o", "output")("output") { file => _out = file }
  )

  def in = _in

  def out = _out
}

object IOOptions {
  def apply(in: File = null, out: File = null) = new IOOptions {
    override protected var _in: File = in
    override protected var _out: File = out
  }
}