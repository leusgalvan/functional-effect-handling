package slides

import cats.effect.{ExitCode, IO, IOApp, Sync}

object TaglessFinal extends IOApp {
  case class Account()
  case class Transfer()
  type Error = String

  trait TransferService[F[_]] {
    def transfer(source: Account, dest: Account, amount: Double): F[Either[Error, Unit]]
    def getTransfers(account: Account): F[Either[Error, List[Transfer]]]
  }

  object TransferService {
    def impl[F[_]: Sync] = new TransferService[F] {
      override def transfer(source: Account, dest: Account, amount: Double): F[Either[Error, Unit]] = ???

      override def getTransfers(account: Account): F[Either[Error, List[Transfer]]] = ???
    }
  }

  override def run(args: List[String]): IO[ExitCode] = ???
}
