import cats.effect._
import cats.effect.implicits._
import cats.effect.unsafe.implicits.global

val a: IO[Int] =
  for {
    _ <- IO.println("hello")
    _ <- IO.println("world")
  } yield 5

a.start.unsafeRunSync()