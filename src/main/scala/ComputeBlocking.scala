import cats.effect._
import cats.effect.implicits._
import cats._
import cats.implicits._

import scala.concurrent.duration.DurationInt

object ComputeBlocking extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    def task(id: Long): IO[Unit] =
      IO.blocking(println(s"Running task $id on thread ${Thread.currentThread().getName}"))


    def blockingTask(id: Long): IO[Unit] = IO.blocking {
      println(s"Running blocking task $id on thread ${Thread.currentThread().getName}")
      Thread.sleep(2000)
      println(s"Waking up blocking task $id on thread ${Thread.currentThread().getName}")
    }

    (1 to 1000).toList.parTraverse { i =>
      task(i)
    }.timeoutTo(5.seconds, IO.unit).as(ExitCode.Success)
  }
}
