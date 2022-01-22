package slides

import cats._
import cats.effect._
import cats.implicits._
import cats.effect.implicits._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

object AsyncApp2 extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    case class Person(id: String, name: String)

    def findPersonById(id: String)(implicit ec: ExecutionContext): Future[Person] = Future {
      println("Finding person")
      Person(id, "blabla")
    }

    def findPersonByIdIO(id: String): IO[Person] = {
      implicit val ec = ExecutionContext.global
      IO(Await.result(findPersonById(id), Duration.Inf))
    }

    def findPersonByIdIO2(id: String): IO[Person] = {
      IO.executionContext.flatMap { implicit ec =>
        IO.async_(cb => findPersonById(id).onComplete {
          case Success(value) => cb(Right(value))
          case Failure(exception) => cb(Left(exception))
        })
      }
    }

    findPersonByIdIO2("abc123").flatTap(IO.println).as(ExitCode.Success)
  }
}
