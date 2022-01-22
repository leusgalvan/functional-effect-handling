import cats.effect._
import cats.implicits._
import cats._
import cats.effect.implicits._
import java.io._

object ResourceSafetyIIApp extends IOApp {

  def write(bytes: Array[Byte], fos: FileOutputStream): IO[Unit] =
    IO.println("writing") *> IO.blocking(fos.write(bytes))

  def read(fis: FileInputStream): IO[Array[Byte]] =
    IO.println("reading") *>
      IO.blocking {
      Iterator
        .continually(fis.read)
        .takeWhile(_ != -1)
        .map(_.toByte)
        .toArray
    }

  def encrypt(bytes: Array[Byte]): IO[Array[Byte]] = {
    IO.println("encrypting") *> bytes.map(b => (b + 1).toByte).pure[IO]
  }

  def closeWriter(ac: AutoCloseable): IO[Unit] =
    IO.println("closing writer") *> IO.blocking(ac.close())

  def closeReader(ac: AutoCloseable): IO[Unit] =
    IO.println("closing reader") *> IO.blocking(ac.close())

  def encryptFile(sourceFile: File, destFile: File): IO[Unit] = {
    val acquireReader = IO.println("acquiring reader") *> IO.blocking(new FileInputStream(sourceFile))
    val acquireWriter = IO.println("acquiring writer") *> IO.blocking(new FileOutputStream(destFile))

    acquireReader.bracket { reader =>
      acquireWriter.bracket { writer =>
        IO.println("using") *>
          read(reader).flatMap(encrypt).flatMap(write(_, writer))
      }(closeWriter)
    }(closeReader)
  }

  def encryptFile2(sourceFile: File, destFile: File): IO[Unit] = {
    val acquireReader = IO.println("acquiring reader") *> IO.blocking(new FileInputStream(sourceFile))
    val acquireWriter = IO.println("acquiring writer") *> IO.blocking(new FileOutputStream(destFile))

    val readerRes = Resource.make[IO, FileInputStream](acquireReader)(closeReader)
    val writerRes = Resource.make[IO, FileOutputStream](acquireWriter)(closeWriter)
    val readerAndWriterRes = readerRes.flatMap(r => writerRes.map(w => (r, w)))

    readerAndWriterRes.use { case (reader, writer) =>
      IO.raiseError(new Exception("boom"))
      //read(reader).flatMap(encrypt).flatMap(write(_, writer))
    }
  }

  def encryptFile3(sourceFile: File, destFile: File): IO[Unit] = {
    val acquireReader = IO.println("acquiring reader") *> IO.blocking(new FileInputStream(sourceFile))
    val acquireWriter = IO.println("acquiring writer") *> IO.blocking(new FileOutputStream(destFile))

    val readerRes = Resource.fromAutoCloseable(acquireReader)
    val writerRes = Resource.fromAutoCloseable(acquireWriter)
    val readerAndWriterRes = readerRes.flatMap(r => writerRes.map(w => (r, w)))

    readerAndWriterRes.use { case (reader, writer) =>
      read(reader).flatMap(encrypt).flatMap(write(_, writer))
    }
  }

  override def run(args: List[String]): IO[ExitCode] = {
    val source = new File("source")
    val dest = new File("dest")
    encryptFile3(source, dest).as(ExitCode.Success)
  }
}
