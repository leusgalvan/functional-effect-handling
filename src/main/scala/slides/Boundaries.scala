package slides

import cats.effect._
import cats.effect.implicits._
import cats._
import cats.data.StateT
import cats.implicits._
import cats.effect.unsafe.implicits.global

import scala.concurrent.duration.DurationInt

object Boundaries {
  def main(args: Array[String]): Unit = {
    val io = for {
      fa <- Monad[IO].iterateForeverM(0)(n => IO.println(n) *> (n+1).pure[IO]).start
      fb <- (IO.sleep(1.seconds) *> fa.cancel).start
      _ <- fa.join
      _ <- fb.join
    } yield ()
    io.unsafeRunSync()
    Concurrent[IO]
  }
}
