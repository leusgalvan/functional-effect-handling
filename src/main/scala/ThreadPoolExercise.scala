import cats._
import cats.implicits._
import cats.effect._
import cats.effect.implicits._

// Hints:
// - LinkedBlockingQueue for tasks
// - Workers run forever
object ThreadPoolExercise {
  class FixedThreadPool(noThreads: Int) {
    def execute(runnable: Runnable): Unit = ???
  }

  def main(args: Array[String]): Unit = {

  }
}
