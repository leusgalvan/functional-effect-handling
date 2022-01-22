import cats.effect._
import cats.effect.implicits._
import cats._
import cats.implicits._

Sync[IO].suspend(Sync.Type.Delay)(5) === Sync[IO].pure(5)
Async[IO]
Concurrent[IO]
MonadCancel[IO, Throwable]