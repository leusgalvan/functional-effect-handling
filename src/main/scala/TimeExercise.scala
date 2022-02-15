import cats._
import cats.implicits._
import cats.effect._
import cats.effect.implicits._

import java.time.LocalDateTime
import scala.concurrent.duration.FiniteDuration

object TimeExercise extends IOApp {
  def tomorrow(): IO[FiniteDuration] = ???
  def tomorrowDateTime(): IO[LocalDateTime] = ???

  override def run(args: List[String]): IO[ExitCode] = {
    IO.pure(ExitCode.Success)
  }
}