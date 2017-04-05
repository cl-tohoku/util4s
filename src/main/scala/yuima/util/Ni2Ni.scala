package yuima.util

import scala.io.Source

/** @author Yuichiroh Matsubayashi
  *         Created on 2015/11/18.
  */
object Ni2Ni {
  def main(args: Array[String]): Unit = {
    Source.stdin.getLines().map(replace) foreach println
  }

  def replace(line: String) =
    line.replaceAll("二=>二", "ニ=>ニ")
      .replaceAll("ガ=>二", "ガ=>ニ")
      .replaceAll("ヲ=>二", "ヲ=>ニ")
      .replaceAll("二=>ガ", "ニ=>ガ")
      .replaceAll("二=>ヲ", "ニ=>ヲ")
      .replaceAll("二=>ニ", "ニ=>ニ")
      .replaceAll("ニ=>二", "ニ=>ニ")
      .replaceAll("\\|二", "\\|ニ")
}
