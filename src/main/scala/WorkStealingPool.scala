import java.util.concurrent.{Callable, Executors, TimeUnit}

object WorkStealingPool {
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

    case class ResultTask(id: Long) extends Callable[Long] {
      override def call(): Long = {
        println(s"Running result task $id on thread ${Thread.currentThread().getName}")
        id
      }
    }

    val workStealingPool = Executors.newWorkStealingPool()

    (1 to 1000).foreach { i =>
      workStealingPool.submit(BlockingTask(i))
    }

    workStealingPool.shutdown()
    workStealingPool.awaitTermination(15L, TimeUnit.SECONDS)
  }
}