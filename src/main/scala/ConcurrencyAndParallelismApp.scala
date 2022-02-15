import cats._
import cats.effect._
import cats.implicits._
import cats.effect.implicits._

import scala.concurrent.duration.DurationInt

object ConcurrencyAndParallelismApp extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    case class Image(bytes: List[Byte])

    def fetchHttp(n: Int): IO[List[Image]] =
      IO.raiseError(new Exception("boom"))

    def fetchDb(n: Int): IO[List[Image]] =
      IO.sleep(100.seconds) *> IO.raiseError(new Exception("db boom"))

    val n = 50
    IO.race(fetchHttp(n), fetchDb(n)).map {
      case Right(dbImgs) => s"Db won: $dbImgs"
      case Left(httpImgs) => s"Http won: $httpImgs"
    }.flatTap(IO.println).as(ExitCode.Success)
  }
}
