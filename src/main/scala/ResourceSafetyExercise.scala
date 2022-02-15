import cats._
import cats.implicits._
import cats.effect._
import cats.effect.implicits._

import java.io._
import java.net.{HttpURLConnection, URL}

object ResourceSafetyExercise extends IOApp {
  def createConnection(targetURL: String): IO[HttpURLConnection] =
    IO.blocking {
      val connection = new URL(targetURL).openConnection().asInstanceOf[HttpURLConnection]
      connection.setRequestMethod("GET")
      connection
    }

  def readOutput(reader: BufferedReader): IO[String] =
    IO.blocking {
      Iterator
        .continually(reader.readLine)
        .takeWhile(_ != null)
        .mkString("\n")
    }

  def httpGet(targetURL: String): IO[String] = {
    for {
      connection  <- createConnection(targetURL)
      inputStream <- IO(connection.getInputStream)
      reader      <- IO(new BufferedReader(new InputStreamReader(inputStream)))
      response    <- readOutput(reader)
    } yield response
  }

  override def run(args: List[String]): IO[ExitCode] = {
    httpGet("http://www.google.com")
      .flatTap(IO.println)
      .as(ExitCode.Success)
  }
}
