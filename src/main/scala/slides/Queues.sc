import cats.effect._
import cats.effect.implicits._
import cats._
import cats.effect.std._
import cats.implicits._
import cats.effect.unsafe.implicits.global

import java.time.ZonedDateTime
import scala.concurrent.duration.DurationInt

trait Event
case class UserAdded(userId: Long) extends Event
case class UserDeleted(userId: Long) extends Event

def producer(eventsQueue: Queue[IO, Event]): IO[Unit] = {
  val generateEvent: IO[Event] = IO {
    val id = (math.random() * 1000).toInt
    if (math.random() < 0.5) UserAdded(id)
    else                     UserDeleted(id)
  }
  val addEvent: IO[Unit] =
    IO.sleep(100.millis) *> generateEvent.flatMap(eventsQueue.offer)
  addEvent.foreverM
}

def consumer(eventsQueue: Queue[IO, Event]): IO[Unit] = {
  eventsQueue.take.flatMap { IO.println }.foreverM
}

val program = Queue.unbounded[IO, Event].flatMap { eventsQueue =>
  producer(eventsQueue).both(consumer(eventsQueue)).void
}

program.timeoutTo(2.seconds, IO.unit).unsafeRunSync()