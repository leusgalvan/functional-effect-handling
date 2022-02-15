import cats.effect._
import cats.effect.implicits._
import cats._
import cats.effect.std._
import cats.implicits._

import scala.concurrent.duration.DurationInt

object QueueApp extends IOApp {
  sealed trait Event
  case class UserAdded(id: Long) extends Event
  case class UserDeleted(id: Long) extends Event

  def producer(queue: Queue[IO, Event]): IO[Nothing] = {
    val generateEvent: IO[Event] = IO {
      val id = (math.random() * 1000).toLong
      if(math.random() < 0.5) UserAdded(id)
      else                    UserDeleted(id)
    }
    (IO.sleep(100.millis) *> generateEvent.flatMap(queue.offer)).foreverM
  }

  def consumer(queue: Queue[IO, Event]): IO[Nothing] = {
    (IO.sleep(1.second) *> queue.take.flatMap(IO.println)).foreverM
  }

  override def run(args: List[String]): IO[ExitCode] = {
    Queue.bounded[IO, Event](0).flatMap { queue =>
      producer(queue).both(consumer(queue))
    }.timeoutTo(3.seconds, IO.unit).as(ExitCode.Success)
  }
}
