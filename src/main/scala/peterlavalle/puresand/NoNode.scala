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


				lazy val spago: File = Extract(dump)

				override def apply(cmd: String, args: String*): Command = {
					(cwd: File) =>
						val sysPath: String = System.getenv("PATH")
						val newPath: String = dump.getAbsolutePath + File.pathSeparator + sysPath

						println(s"sysPath = $sysPath")
						println(s"newPath = $newPath")

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
								"PATH" -> newPath
							)

						(log: ProcessLogger) =>
							process ! log
				}
			}

}
