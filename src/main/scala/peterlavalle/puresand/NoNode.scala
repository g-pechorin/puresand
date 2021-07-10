package peterlavalle.puresand

import java.io.{FileInputStream, FileOutputStream, _}
import java.util
import java.util.zip.GZIPInputStream

import org.apache.commons.compress.archivers.tar.{TarArchiveEntry, TarArchiveInputStream}

import scala.language.postfixOps
import scala.sys.process.{ProcessLogger, _}

trait NoNode {

	trait Command extends (File => ProcessLogger => Int) {
		def !(cwd: File = null): (Int, List[String]) = {
			val lines = new util.LinkedList[String]()
			apply(
				cwd
			)(
				ProcessLogger(
					(o: String) => lines.add(';' + o),
					(e: String) => lines.add('!' + e)
				)
			) -> {
				import scala.collection.JavaConverters._
				lines.asScala.toList
			}
		}

		def ! : (Int, List[String]) = this ! null
	}

	def apply(cmd: String, args: String*): Command
}

object NoNode {
	def apply(dump: File): NoNode =
		if (dump != dump.getAbsoluteFile)
			apply(dump.getAbsoluteFile)
		else
			new NoNode {

				// setup the install
				extract(dump)

				lazy val spago: File =
					os {
						case ("windows", "amd64") =>
							dump / "spago.exe"

						case ("linux", "i386" | "amd64") |
								 ("mac", "x86_64") =>
							dump / "spago"
					}

				override def apply(cmd: String, args: String*): Command = {
					(cwd: File) =>
						val process: ProcessBuilder =
							Process(
								// build the spago command
								command = spago.getAbsolutePath :: cmd :: args.toList,

								// workout where to run
								cwd =
									if (null == cwd)
										dump
									else
										cwd.getAbsoluteFile,

								// prepend the extacted things to the PATH
								"PATH" -> (dump.getAbsolutePath + File.pathSeparator + System.getenv("PATH"))
							)

						(log: ProcessLogger) =>
							process ! log
				}
			}

	def extract(dump: File): Unit = {

		val tars = {
			os {
				case ("windows", "amd64") =>
					("win64.tar.gz", "Windows.tar.gz")

				case ("mac", "x86_64") =>
					("macos.tar.gz", "macOS.tar.gz")

				case ("linux", "amd64") =>
					("linux64.tar.gz", "Linux.tar.gz")

				case ("linux", "i386") =>
					System.err.println("I should warn you that I'm using the amd64 build on an i386 platform - this probably won't work and will give a no-such-file error")
					("linux64.tar.gz", "Linux.tar.gz")


				// ("linux64.tar.gz", "Linux.tar.gz")
				// ("macos.tar.gz", "macOS.tar.gz")
			} match {
				case (pure, spago) =>

					List(
						("https://github.com/purescript/purescript/releases/download/v0.14.2/" + pure, "purescript/"),
						("https://github.com/purescript/spago/releases/download/0.20.3/" + spago, ""),
					)

			}
		}

		tars.foreach {
			case (tar, fix) =>
				val file: File = dump / tar.hashString

				// download
				import java.net.URL

				import sys.process._
				if (!file.isFile)
					require {
						"" == (new URL(tar) #> file.EnsureParent !!)
					}

				// unpack
				val tarStream =
					new TarArchiveInputStream(new GZIPInputStream(new FileInputStream(file)))

				Stream.continually(tarStream.getNextTarEntry)
					.takeWhile(null !=)
					.filterNot((_: TarArchiveEntry).isDirectory)
					.foreach {
						entry: TarArchiveEntry =>
							val file: File =
								dump / entry.getName.drop(fix)

							val out =
								new FileOutputStream(
									file.EnsureParent
								)
							out.write(
								tarStream.fill(entry.getSize.toInt)
							)
							out.close()

							Mode(
								entry.getMode
							)(file)

					}


		}


	}

}
