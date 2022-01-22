package slides

import cats.effect._
import cats.effect.unsafe.implicits.global
import cats.implicits._

import java.security.SecureRandom
import java.time.{LocalDate, ZonedDateTime}
import scala.concurrent.duration.FiniteDuration

object Time2 {
  case class Token(value: String, expirationTimeMillis: Long) {
    def isExpired(): IO[Boolean] =
      IO.realTime.map(_.toMillis > expirationTimeMillis)
    def expirationDate(): LocalDate =
      LocalDate.ofEpochDay(expirationTimeMillis)
  }

  def timeIt[A](expr: IO[A]): IO[FiniteDuration] = {
    for {
      start <- IO.monotonic
      _     <- expr
      end   <- IO.monotonic
    } yield end - start
  }

  def main(args: Array[String]): Unit = {
    timeIt(IO(new SecureRandom().nextInt())).map(_.toNanos).flatMap(IO.println).unsafeRunSync()
  }
}
