package yuima.util

import java.io._
import java.util.zip.{GZIPInputStream, GZIPOutputStream}

import org.apache.commons.compress.compressors.bzip2.{BZip2CompressorInputStream, BZip2CompressorOutputStream}

import scala.io.{BufferedSource, Codec, Source}

/** @author Yuichiroh Matsubayashi
  *         Created on 2016/02/17.
  */
object IO {

  def expand(path: String): String = {
    if (path == "~" || path.startsWith("~/"))
      System.getProperty("user.home") + path.substring(1)
    else path
  }

  object In {
    def fromFile(path: String)(implicit codec: Codec): BufferedSource = fromFile(new File(expand(path)))

    def fromFile(file: File)(implicit codec: Codec): BufferedSource = Source.fromInputStream(fis(file))(codec)

    def readLines: Iterator[String] = scala.io.Source.stdin.getLines

    def ois(path: String): ObjectInputStream = ois(fis(expand(path)))

    def ois(is: InputStream): ObjectInputStream = new ObjectInputStream(new BufferedInputStream(is))

    def fis(path: String): InputStream =
      if (path.endsWith(".gz")) new GZIPInputStream(new FileInputStream(expand(path)))
      else new FileInputStream(path)

    def ois(file: File): ObjectInputStream = ois(fis(file))

    def ois(): ObjectInputStream = ois(System.in)

    def br(is: InputStream): BufferedReader = new BufferedReader(new InputStreamReader(is))

    def br(file: File): BufferedReader = br(isr(fis(file)))

    def fis(file: File): InputStream =
      if (file.getName.endsWith(".gz")) new GZIPInputStream(new FileInputStream(file))
      else if (file.getName.endsWith(".bz2")) new BZip2CompressorInputStream(new FileInputStream(file))
      else new FileInputStream(file)

    def br(reader: Reader): BufferedReader = new BufferedReader(reader)

    def isr(is: InputStream): InputStreamReader = new InputStreamReader(is)

    def br(path: String): BufferedReader = br(isr(fis(expand(path))))

    def bis(is: InputStream): BufferedInputStream = new BufferedInputStream(is)
  }

  object Out {
    def pwWithSameSubPath(file: File, baseOld: String, baseNew: String): PrintWriter =
      pwWithSameSubPath(file, new File(baseOld), new File(baseNew))

    def pwWithSameSubPath(file: File, baseOld: File, baseNew: File): PrintWriter =
      pw(baseNew + s"${ file.getAbsolutePath diff baseOld.getAbsolutePath }")

    def pw(os: OutputStream): PrintWriter = new PrintWriter(os)

    def pw(file: File): PrintWriter = pw(fos(file))

    def pw(path: String): PrintWriter = pw(fos(expand(path)))

    def ps(os: OutputStream): PrintStream = new PrintStream(os)

    def ps(file: File): PrintStream = ps(fos(file))

    def ps(path:String): PrintStream = ps(fos(expand(path)))

    def bw(path: String): BufferedWriter = bw(osw(fos(expand(path))))

    def bw(writer: Writer): BufferedWriter = new BufferedWriter(writer)

    def bw(file: File): BufferedWriter = bw(osw(fos(file)))

    def bw(stream: OutputStream): BufferedWriter = new BufferedWriter(osw(stream))

    def osw(stream: OutputStream): OutputStreamWriter = new OutputStreamWriter(stream)

    def bos(file: File): BufferedOutputStream = new BufferedOutputStream(fos(file))

    def bos(path: String): BufferedOutputStream = new BufferedOutputStream(fos(expand(path)))

    def fos(path: String): OutputStream = fos(new File(path))

    def fos(file: File): OutputStream =
      if (file.getName.endsWith(".gz")) new GZIPOutputStream(new FileOutputStream(file))
      else if (file.getName.endsWith(".bz2")) new BZip2CompressorOutputStream(new FileOutputStream(file))
      else new FileOutputStream(file)

    def oos(file: File): ObjectOutputStream = new ObjectOutputStream(bos(fos(file)))

    def bos(os: OutputStream): BufferedOutputStream = new BufferedOutputStream(os)

    def oos(path: String): ObjectOutputStream = new ObjectOutputStream(bos(fos(expand(path))))

    def oos(): ObjectOutputStream = new ObjectOutputStream(System.out)

    def fw(file: File): FileWriter = new FileWriter(file)

    def fw(path: String): FileWriter = new FileWriter(path)
  }

}
