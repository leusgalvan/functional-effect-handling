import cats.effect._
import cats.implicits._

import scala.concurrent.duration.DurationInt

object ConcurrencyAndParallelismAppExercise extends IOApp {
  case class Quote(author: String, text: String)

  def fetchHttp(n: Int): IO[List[Quote]] =
    IO.sleep(10.millis) *>
      (1 to n).toList.map(i => Quote(s"author $i", s"text $i")).pure[IO]

  def fetchDb(n: Int): IO[List[Quote]] =
    IO.sleep(100.millis) *>
      (1 to n).toList.map(i => Quote(s"author $i", s"text $i")).pure[IO]

  def fetchAuthorAge(author: String): IO[Int] =
    IO.sleep(150.millis) *> IO((math.random() * 100).toInt)


  override def run(args: List[String]): IO[ExitCode] = {
    val n = 3

    // fetch n quotes from the fastest source
    // calculate the average age of the authors
    IO.race(fetchHttp(n), fetchDb(n))
      .flatMap { _.fold(identity, identity).parTraverse(q => fetchAuthorAge(q.author)) }
      .flatTap(IO.println)
      .map( ages => ages.sum / n.toDouble)
      .flatTap(IO.println)
      .as(ExitCode.Success)
  }
}
