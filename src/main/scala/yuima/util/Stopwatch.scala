package yuima.util

object Stopwatch {
  def apply() = new Stopwatch
}

class Stopwatch(tTotal: Long = System.nanoTime) extends Logging {
  loggingLevel = LoggingLevel.LOG
  var tStart = System.nanoTime
  var tStop = tStart
  var tSplit = tStart

  def start(processName: String = "process") = {
    log("%s started.".format(processName));
    tStart = System.nanoTime
  }

  def stop() = {
    tStop = System.nanoTime
    log("Process Time (ns): %1$,3d".format(tStop - tStart))
  }

  def time[T](result: T, name: String = "process") = {
    start(name)
    println(result)
    stop
  }

  def split() = {
    tSplit = System.nanoTime
  }

  def read = "Process Time (ms): %1$,3d".format((tStop - tStart) / 1000000)

  def readTotal = "Total Time (ms): %1$,3d".format((System.nanoTime - tTotal) / 1000000)

  def readSplit = {
    val newT = System.nanoTime
    val time = newT - tSplit
    tSplit = newT
    "Proccess Time (ms): %1$,3d".format(time / 1000000)
  }

  def benchmark(multi: Int)(f: => Any) = {
    val start = System.nanoTime
    for (i <- 1 to multi) {
      val result = f
    }
    val end = System.nanoTime
    end - start
  }

  def benchmark(times: Int, multi: Int)(f: => Any): String = {
    val results = for (i <- 1 to times) yield { benchmark(multi)(f) }
    "Proccess Time (ns): %s".format(results.mkString(", "))
  }
}