package slides

import java.util.concurrent.Executors

object ThreadPools {
  def simpleThreads = {
    val task = new Runnable {
      override def run(): Unit = {
        while(true) {
          println("Still working...")
          Thread.sleep(100)
        }
      }
    }

    val t = new Thread(task, "My thread")
    t.start()

    while(true) {
      println("Main thread working")
      Thread.sleep(100)
    }
  }

  def fixedThreadPool(): Unit = {
    def task(i: Int): Runnable = new Runnable {
      override def run(): Unit = {
        if(i % 20 == 0) {
          println(s"${Thread.currentThread().getName}: throwing from task $i...")
          throw new RuntimeException("boom!")
        }
        println(s"${Thread.currentThread().getName}: finishing task $i...")
      }

    }
    val threadPoolExecutor = Executors.newFixedThreadPool(3)
    (1 to 100).foreach(i => threadPoolExecutor.execute(task(i)))
    threadPoolExecutor.shutdown()
  }

  def cachedThreadPool(): Unit = {
    def task(i: Int): Runnable = new Runnable {
      override def run(): Unit = {
        println(s"${Thread.currentThread().getName}: $i...")
      }

    }
    val threadPoolExecutor = Executors.newCachedThreadPool()
    (1 to 1000).foreach(i => threadPoolExecutor.execute(task(i)))
    threadPoolExecutor.shutdown()
  }

  def main(args: Array[String]): Unit = {
    fixedThreadPool()
  }
}
