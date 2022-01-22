import cats._
import cats.effect._
import cats.effect.unsafe.implicits.global
import cats.implicits._

import scala.concurrent.duration.{FiniteDuration, SECONDS}

object Time extends IOApp {
  case class Token(value: String, expirationTimeInMillis: Long) {
    def isExpired(): IO[Boolean] = {
      IO.realTime.map(_.toMillis > expirationTimeInMillis)
    }
  }

  def measure[A](ioa: IO[A]): IO[FiniteDuration] = {
    for {
      start <- IO.monotonic
      _ <- ioa
      end <- IO.monotonic
    } yield (end - start)
  }

  override def run(args: List[String]): IO[ExitCode] = {
    val program = (1 to 10000000).toList.traverse_ { i =>
      IO.println(i)
    }
    measure(program)
      .map(_.toMillis)
      .flatTap(m => IO.println(s"Elapsed: $m millis"))
      .as(ExitCode.Success)
  }
}