package yuima.util

import java.text.DecimalFormat

object MemoryView {
  val KB = 1024
  val f1 = new DecimalFormat("#,###KB")
  val f2 = new DecimalFormat("##.#")

  def getMemoryInfo = {
    val free = Runtime.getRuntime.freeMemory() / KB
    val total = Runtime.getRuntime.totalMemory() / KB
    val max = Runtime.getRuntime.maxMemory() / KB
    val used = total - free;
    val ratio = used * 100 / max.asInstanceOf[Double]
    "Memory: total=" + f1.format(total) + "、" + "used=" + f1.format(used) + " (" + f2.format(ratio) + "%)、" + "max=" + f1.format(max)
  }
}