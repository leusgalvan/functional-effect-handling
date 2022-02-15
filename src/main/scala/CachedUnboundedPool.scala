import java.util.concurrent.{Callable, ExecutorService, Executors, TimeUnit}

object CachedUnboundedPool {
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

    val cachedUnboundedPool = Executors.newCachedThreadPool()

    (1 to 1000).foreach { i =>
      cachedUnboundedPool.submit(BlockingTask(i))
    }

    cachedUnboundedPool.shutdown()
    cachedUnboundedPool.awaitTermination(5L, TimeUnit.SECONDS)
  }
}

