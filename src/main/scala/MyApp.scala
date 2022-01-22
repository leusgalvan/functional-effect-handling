import cats._
import cats.effect._
import cats.implicits._
import cats.effect.implicits._
import cats.effect.unsafe.implicits.global

import scala.io.StdIn

object MyApp extends IOApp {
  object Console {
    def putStrLn(s: String): IO[Unit] = IO(println(s))
    def readLine(text: String): IO[String] = IO(StdIn.readLine(text))
  }

  import Console._
  // read a line
  // output the line
  // repeat
  def echoForever: IO[Nothing] = {
    val program = for {
      line <- readLine("Enter a line: ")
      _    <- putStrLn(line)
    } yield ()
    program.foreverM
  }

  override def run(args: List[String]): IO[ExitCode] = {
    echoForever.as(ExitCode.Success)
  }
}
