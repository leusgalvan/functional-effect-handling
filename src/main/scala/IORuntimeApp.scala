import cats._
import cats.effect._
import cats.effect.implicits._
import cats.implicits._

object IORuntimeApp extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    IO(ExitCode.Success)
  }
}
