package yuima.util

sealed abstract class Color(code: String) {
  override def toString: String = code
}

object Color {

  case object BLACK extends Color(Console.BLACK)

  case object RED extends Color(Console.RED)

  case object GREEN extends Color(Console.GREEN)

  case object YELLOW extends Color(Console.YELLOW)

  case object BLUE extends Color(Console.BLUE)

  case object MAGENTA extends Color(Console.MAGENTA)

  case object CYAN extends Color(Console.CYAN)

  case object WHITE extends Color(Console.WHITE)

  case object BLACK_B extends Color(Console.BLACK_B)

  case object RED_B extends Color(Console.RED_B)

  case object GREEN_B extends Color(Console.GREEN_B)

  case object YELLOW_B extends Color(Console.YELLOW_B)

  case object BLUE_B extends Color(Console.BLUE_B)

  case object MAGENTA_B extends Color(Console.MAGENTA_B)

  case object CYAN_B extends Color(Console.CYAN_B)

  case object WHITE_B extends Color(Console.WHITE_B)

  case object RESET extends Color(Console.RESET)

  case object BOLD extends Color(Console.BOLD)

  case object UNDERLINED extends Color(Console.UNDERLINED)

  case object BLINK extends Color(Console.BLINK)

  case object REVERSED extends Color(Console.REVERSED)

  case object INVISIBLE extends Color(Console.INVISIBLE)

}
