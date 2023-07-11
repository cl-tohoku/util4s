package yuima.util

import java.io.File

import scala.collection.mutable
import scala.reflect.ClassTag

/** @author Yuichiroh Matsubayashi
  *         Created on 2016/02/17.
  */

trait CommandLineElement {
  def parse: PartialFunction[(List[String], OptionParser), Unit]
  def usage: String
}

case class Arg[A](descreption: String, argNum: Int = 1)
                 (val op: A => Unit)(implicit arg: CArg[A]) extends CommandLineElement {
  val parse: PartialFunction[(List[String], OptionParser), Unit] = arg.parse(op)

  // TODO: formatting
  val usage: String = descreption
}

trait CArg[A] {
  def parse: (A => Unit) => PartialFunction[(List[String], OptionParser), Unit]
}

object CArg {
  implicit val argInt: CArg[Int] = args(parseSingleArg[Int](_.toInt))
  implicit val argDouble: CArg[Double] = args(parseSingleArg[Double](_.toDouble))
  implicit val argFloat: CArg[Float] = args(parseSingleArg[Float](_.toFloat))
  implicit val argLong: CArg[Long] = args(parseSingleArg[Long](_.toLong))
  implicit val argBigInt: CArg[BigInt] = args(parseSingleArg[BigInt](str => BigInt(str)))
  implicit val argBigDecimal: CArg[BigDecimal] = args(parseSingleArg[BigDecimal](str => BigDecimal(str)))
  implicit val argString: CArg[String] = args(parseSingleArg[String](identity))
  implicit val argFile: CArg[File] = args(parseSingleArg[File](str => new File(str)))

  def args[A](_parse: (A => Unit) => PartialFunction[(List[String], OptionParser), Unit])
  : CArg[A] = new CArg[A] {
    val parse: ((A) => Unit) => PartialFunction[(List[String], OptionParser), Unit] = _parse
  }

  def parseSingleArg[A](build: String => A)
                       (op: A => Unit): PartialFunction[(List[String], OptionParser), Unit] = {
    case (arg :: tail, parser) if !arg.matches("--?[-A-z]+") =>
      op(build(arg))
      parser.parse(tail)
  }
}

class Opt[A: ClassTag](val abbr       : Option[String] = None,
                       val full       : String,
                       val needed     : Boolean = false,
                       val description: String = "",
                       val argNum     : Int = -1,
                       val validator  : Option[Validator[A]] = None,
                       val op         : A => Unit)(implicit optArg: OptArg[A]) extends CommandLineElement {
  // TODO: formatting
  val usage: String = description

  val parse: PartialFunction[(List[String], OptionParser), Unit] = optArg.parse(abbr, full, argNum, validator, op)

  def required() = new Opt[A](abbr, full, needed = true, description, argNum, validator, op)

  def optional() = new Opt[A](abbr, full, needed = true, description, argNum, validator, op)

  def text(str: String) = new Opt[A](abbr, full, needed, description = str, argNum, validator, op)

  def args(n: Int) = new Opt[A](abbr, full, needed, description, argNum = n, validator, op)

  override def toString: String = s"Opt[${ typeName }](${ abbr.get })"

  def typeName(implicit tag: ClassTag[A]): String = tag.runtimeClass.getSimpleName
}

object Opt {
  def apply[A: ClassTag](full: String)
                        (description: String)
                        (op: A => Unit)
                        (implicit optArg: OptArg[A]): Opt[A] = {
    apply(null, full)(description)(op)
  }

  def apply[A: ClassTag](abbr: String, full: String)
                        (description: String, required: Boolean = false, argNum: Int = -1,
                         validator: Validator[A] = null)
                        (op: A => Unit)
                        (implicit optArg: OptArg[A]): Opt[A] = {
    val fullName = "--" + full
    val maybeAbbrevation = Some("-" + abbr)
    val maybeValidator = Option(validator)
    new Opt[A](maybeAbbrevation, fullName, required, description, argNum, maybeValidator, op)
  }
}

trait OptArg[A] {
  //  def build: List[String] => A
  def parse: (Option[String], String, Int, Option[Validator[A]], A => Unit) => PartialFunction[(List[String], OptionParser), Unit]
}

object OptArg {
  implicit val argUnit: OptArg[Unit] = args(parseNoArgOption)

  implicit val argInt: OptArg[Int] = args[Int](parseSingleArgOption[Int](_.toInt))
  implicit val argDouble: OptArg[Double] = args(parseSingleArgOption[Double](_.toDouble))
  implicit val argFloat: OptArg[Float] = args(parseSingleArgOption[Float](_.toFloat))
  implicit val argLong: OptArg[Long] = args(parseSingleArgOption[Long](_.toLong))
  implicit val argBigInt: OptArg[BigInt] = args(parseSingleArgOption[BigInt](str => BigInt(str)))
  implicit val argBigDecimal: OptArg[BigDecimal] = args(parseSingleArgOption[BigDecimal](str => BigDecimal(str)))
  implicit val argString: OptArg[String] = args(parseSingleArgOption[String](identity))
  implicit val argFile: OptArg[File] = args(parseSingleArgOption[File](str => new File(IO.expand(str))))

  //  implicit val argIntArray: OptArg[Array[Int]] = args(_.map(_.toInt).toArray)
  //    implicit val argDoubleArray: OptArg[Array[Double]] = args(_.map(_.toDouble).toArray)
  implicit val argDoubleArray: OptArg[Array[Double]] = args(parseMultiArgOption(_.map(_.toDouble).toArray))
  //  implicit val argFloatArray: OptArg[Array[Float]] = args(_.map(_.toFloat).toArray)
  //  implicit val argLongArray: OptArg[Array[Long]] = args(_.map(_.toLong).toArray)
  //  implicit val argBigIntArray: OptArg[Array[BigInt]] = args(_.map(s => BigInt(s)).toArray)
  //  implicit val argBigDecimalArray: OptArg[Array[BigDecimal]] = args(_.map(s => BigDecimal(s)).toArray)
  //
  //  implicit val argStringArray: OptArg[Array[String]] = args(_.toArray)

  implicit val argBoolean: OptArg[Boolean] = args(parseSingleArgOption[Boolean] {
    _.toLowerCase match {
      case "true" => true
      case "false" => false
      case "yes" => true
      case "no" => false
      case "1" => true
      case "0" => false
      case s =>
        throw new IllegalArgumentException("'" + s + "' is not a boolean.")
    }
  })

  def args[A](_parse: (Option[String], String, Int, Option[Validator[A]], A => Unit) => PartialFunction[(List[String], OptionParser), Unit])
  : OptArg[A] =
    new OptArg[A] {
      val parse: (Option[String], String, Int, Option[Validator[A]], (A) => Unit) => PartialFunction[(List[String], OptionParser), Unit] = _parse
    }

  def parseNoArgOption(abbr: Option[String], full: String,
                       argNum: Int, validator: Option[Validator[Unit]], op: Unit => Unit)
  : PartialFunction[(List[String], OptionParser), Unit] = {
    case (opt :: tail, parser) if prefixMatch(opt, abbr) && !parser.exclusiveFlag(full) =>
      op(Unit)
      val key = abbr.get
      //      parser.parse(opt :: tail)
      setExclusiveFlag(full, parser)
    case (opt :: tail, parser) if (opt == full || abbr.contains(opt)) && !parser.exclusiveFlag(full) =>
      op(Unit)
      //      parser.parse(tail)
      setExclusiveFlag(full, parser)
  }

  def prefixMatch(str: String, abbr: Option[String]): Boolean = abbr match {
    case Some(key) => str.startsWith(key)
    case None => false
  }

  def parseSingleArgOption[A](build: String => A)
                             (abbr: Option[String], full: String,
                              argNum: Int, validator: Option[Validator[A]], op: A => Unit)
  : PartialFunction[(List[String], OptionParser), Unit] = {
    case (opt :: arg :: tail, parser) if (opt == full || abbr.contains(opt)) && !parser.exclusiveFlag(full) &&
                                         !arg.matches("--?[-A-z]") =>
      validator match {
        case Some(v) if v.validate(build(arg)) =>
          println(v.errMsg)
          System.exit(1)
        case _ =>
          op(build(arg))
          //          parser.parse(tail)
          setExclusiveFlag(full, parser)
      }
  }

  def setExclusiveFlag(full: String, parser: OptionParser): Unit = {
    parser.exclusiveSets.foreach { s =>
      if (s.contains(full)) s.foreach(key => parser.exclusiveFlag(key) = true)
    }
  }

  def parseMultiArgOption[A](build: List[String] => A)
                            (abbr: Option[String], full: String, argNum: Int, validator: Option[Validator[A]],
                             op  : A => Unit)
  : PartialFunction[(List[String], OptionParser), Unit] = {
    def parseOptArgs(list: List[String], argNum: Int,
                     args: List[String] = Nil): (List[String], List[String]) = list match {
      case Nil => (args.reverse, Nil)
      case arg :: tail if argNum != 0 &&
                          !arg.matches("--?[A-z]+") =>
        parseOptArgs(tail, argNum - 1, arg :: args)
      case rest =>
        (args.reverse, rest)
    }

    {
      case (opt :: tail, parser) if (opt == full || abbr.contains(opt)) && !parser.exclusiveFlag(full) =>
        val (args, rest) = parseOptArgs(tail, argNum)
        validator match {
          case Some(v) if v.validate(build(args)) =>
            println(v.errMsg)
            System.exit(1)
          case _ =>
            op(build(args))
        }
    }
  }
}

trait OptionParser {
  protected lazy val _parse: PartialFunction[(List[String], OptionParser), Unit] = compose(options.map(_.parse))

  val exclusiveSets: Seq[Set[String]] = Seq[Set[String]]()
  val exclusiveFlag: mutable.Map[String, Boolean] = collection.mutable.Map[String, Boolean]().withDefaultValue(false)

  val parseUnknownOption: PartialFunction[(List[String], OptionParser), Unit] = {
    case (Nil, _) =>
    case (option, _) =>
      println("Unknown option " + option.mkString(" "))
      System.exit(1)
  }

  def parse(args: List[String]): OptionParser = {
    optionStrs(args).foreach(list => (_parse orElse parseUnknownOption) (list, this))
    this
  }

  def andThen(that: OptionParser) = {
    val u = usage
    val p = _parse
    val o = options
    val es = exclusiveSets
    val ef = exclusiveFlag
    new OptionParser {
      override val usage: String = Seq(u, that.usage).mkString("\n")
      override val options: Seq[CommandLineElement] = o ++ that.options
      override val exclusiveSets: Seq[Set[String]] = es ++ that.exclusiveSets
      override val exclusiveFlag: mutable.Map[String, Boolean] =
        collection.mutable.Map[String, Boolean]().withDefaultValue(false) ++
        (ef.toList ++ that.exclusiveFlag.toList).groupBy(_._1).map { case (key, kvList) =>
          (key, kvList.exists(_._2))
        }

      override lazy val _parse: PartialFunction[(List[String], OptionParser), Unit] =
        p orElse that._parse
    }
  }

  def options: Seq[CommandLineElement] = Seq.empty[CommandLineElement]

  def usage: String = options.map(_.usage).mkString("\n")

  private[this] def optionStrs(strs: List[String]): List[List[String]] = {
    if (strs.isEmpty) Nil
    else {
      val opt = strs.head
      val (args, rest) = strs.tail.span(arg => !arg.matches("-[-A-z]+"))
      (opt :: args) :: optionStrs(rest)
    }
  }

  private[this] def compose(pfs: Seq[PartialFunction[(List[String], OptionParser), Unit]]) = pfs.reduceLeft(
    (pf1, pf2) => pf1 orElse pf2)
}

case class Validator[A](validate: A => Boolean, errMsg: String)