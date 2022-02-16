import cats._
import cats.effect._
import cats.effect.implicits._
import cats.implicits._

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

object TaglessFinalExercise extends IOApp {
  case class Image(bytes: List[Byte])

  trait ImagesService {
    def fetchHttp(n: Int): IO[List[Image]]
    def fetchDb(n: Int): IO[List[Image]]
    def fetchFastest(n: Int): IO[List[Image]]
  }

  object ImagesService {
    def impl = new ImagesService {
      override def fetchHttp(n: Int): IO[List[Image]] = {
        IO.sleep(1.second).flatMap { _ =>
          List.range(0, n).parTraverse { i =>
            IO.blocking(Image(List(i.toByte)))
          }
        }
      }

      override def fetchDb(n: Int): IO[List[Image]] = {
        IO.sleep(2.seconds).flatMap { _ =>
          List.range(0, n).parTraverse { i =>
            IO.blocking(Image(List(i.toByte)))
          }
        }
      }

      override def fetchFastest(n: Int): IO[List[Image]] =
        IO.race(fetchHttp(n), fetchDb(n)).map(_.fold(identity, identity))
    }
  }

  override def run(args: List[String]): IO[ExitCode] = ???
}
