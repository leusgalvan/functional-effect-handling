import java.util.concurrent.{Executors, TimeUnit}
import scala.concurrent.ExecutionContext

object ExecutionContextApp {
  def main(args: Array[String]): Unit = {
    case class Task(id: Long) extends Runnable {
      override def run(): Unit = {
        println(s"Running task $id on thread ${Thread.currentThread().getName}")
      }
    }

    case class BlockingTask(id: Long) extends Runnable {
      override def run(): Unit = {
        println(s"Running blocking task $id on thread ${Thread.currentThread().getName}")
        Thread.sleep(2000)
        println(s"Waking up blocking task $id on thread ${Thread.currentThread().getName}")
      }
    }

    val workStealingPool = Executors.newWorkStealingPool()
    val executionContext = ExecutionContext.fromExecutorService(workStealingPool)

    (1 to 1000).foreach { i =>
      executionContext.execute(BlockingTask(i))
    }

    executionContext.shutdown()
    executionContext.awaitTermination(5L, TimeUnit.SECONDS)
  }
}
