import cats.effect._
import cats.implicits._

import scala.concurrent.duration.DurationInt

object MemoizationExercise extends IOApp {
  trait Currency
  case object Dollar extends Currency
  case object Euro extends Currency

  case class Balance(amount: Double, currency: Currency)

  // pretend this is calling an API and takes some time
  def fetchDollarExchangeRate(currency: Currency): IO[Double] = {
    IO.sleep(2.seconds) *>
    IO.println("fetching exchange rate") *>
    IO.pure(
      currency match {
        case Dollar => 1.0
        case Euro => 1.12
      }
    )
  }

  //lazy val euroExchangeRate: IO[Double] = fetchDollarExchangeRate(Euro)

  def getBalancesInDollars(balances: List[Balance]): IO[List[Double]] = {
    fetchDollarExchangeRate(Euro).memoize.flatMap { euroExchangeRateIO =>
      balances.parTraverse(balance => balance.currency match {
        case Dollar => IO(balance.amount)
        case Euro => euroExchangeRateIO.map(euroExchangeRate => balance.amount * euroExchangeRate)
      })
    }
  }

  override def run(args: List[String]): IO[ExitCode] = {
    // Modify both functions so they return an IO
    // Achieve the same behaviour:
    // - If all balances are dollars, you never fetch the exchange rate
    // - If more than one balance is euros, you only fetch the exchange rate once
    getBalancesInDollars(List(Balance(10, Euro), Balance(20, Euro)))
      .flatTap(IO.println)
      .as(ExitCode.Success)
  }
}