package peterlavalle.puresand

import java.io._
import java.lang
import java.util.zip.{ZipEntry, ZipInputStream}

import scala.language.postfixOps
import scala.reflect.ClassTag
import scala.sys.process.{Process, ProcessLogger}


object NodeJS {

	import T._

	private val napTime: Long = 314

	def open(dump: File = "target" / "nodejs-dump", log: ProcessLogger = ProcessLogger(System.out.println(_: String))): Instance = {

		lazy val path: File = {
			NodeJS(dump, log.out(_: String))
		}

		def cmd(cmd: String): String = {
			val exe: File =
				os {
					case ("windows", _) =>
						path / s"$cmd.cmd"
				}

			require(exe.isFile)

			exe.AbsolutePath
		}


		(exe: String, name: String, version: String) => {

			lazy val PATH: (String, String) = "PATH" -> (path.getAbsolutePath + File.pathSeparator + System.getenv("PATH"))

			lazy val command: String = {
				require {
					0 == {
						Process(
							Seq(
								cmd("npm"),
								"install", "-g",
								if (null == version)
									name
								else
									name + "@" + version),
							path.ParentFile,
							PATH
						) ! log
					}
				}
				Thread.sleep(napTime)
				cmd(exe)
			}


			(args: List[String], exeLogger: ProcessLogger, cwd: File) =>
				(if (null == cwd)
					Process(command :: args, None, PATH)
				else
					Process(command :: args, cwd, PATH)) ! (if (null != exeLogger) exeLogger else log)
		}
	}

	def apply(dump: File = "target" / "nodejs-dump", note: String => Unit = println(_: String), force: Boolean = false, version: String = "v13.6.0"): File = {
		trait Entry {
			def name: String

			def data: Array[Byte]
		}

		type Access = InputStream => Stream[Entry]

		//
		val (suffix: String, access: Access) =
			os {
				case ("windows", "amd64") =>
					// 7z hits "Multi input/output stream coders are not yet supported" so we're not doing it
					"win-x64.zip" -> {
						inputStream: InputStream =>
							val zipInputStream = new ZipInputStream(inputStream)
							Stream.continually(zipInputStream.getNextEntry)
								.takeWhile(null != (_: ZipEntry))
								.filterNot((_: ZipEntry).isDirectory)
								.map {
									entry: ZipEntry =>
										new Entry {
											override val name: String = entry.getName.drop(s"node-$version-win-x64/")
											override val data: Array[Byte] = zipInputStream.readNBytes(entry.getSize.toInt)
										}
								}

					}
			}

		/**
		 * either download or reuse
		 */
		val archive: FileInputStream = {

			/**
			 * what's the archive URL?
			 */
			val url: String = s"https://nodejs.org/download/release/$version/node-$version-" + suffix

			// for when we're debugging
			val localArchive: File = System.getProperty("user.home") / "Downloads" / url.reverse.takeWhile('/' != (_: Char)).reverse
			if (localArchive isFile) {
				note("using local file " + localArchive.AbsolutePath)
				new FileInputStream(localArchive)
			} else {
				sys.error("download the file")
			}
		}

		/**
		 * unpack the thing
		 */
		if ((dump / "npm").isFile && !force)
			note("skipping re-extraction")
		else {
			access(archive)
				.foreach(
					(entry: Entry) => {
						val out = new FileOutputStream(dump / entry.name EnsureParent)
						new ByteArrayInputStream(entry.data).transferTo(out)
						out.close()
					}
				)
			Thread.sleep(napTime)
		}


		dump
	}

	trait Instance {
		final def apply(name: String): NodeJS.Command = apply(name, name, null)

		final def apply(name: String, version: String): NodeJS.Command = apply(name, name, version)

		def apply(exe: String, name: String, version: String): NodeJS.Command

	}

	trait Command {


		def !(cwd: File, args: AnyRef*): String = {

			val stringBuilder: lang.StringBuilder = new lang.StringBuilder()

			require {
				0 ==
					exe(
						args.toList
					)(
						ProcessLogger(
							(o: String) => stringBuilder.append(o + "\n"),
							(e: String) => sys.error(e)
						),
						cwd
					)
			}

			stringBuilder.toString.dropRight(1)

		}

		def apply(): Int = exe(Nil)(null)

		def apply(arg0: String, args: AnyRef*): Int = exe(arg0 :: args.toList)(null)

		def apply(arg0: File, args: AnyRef*): Int = exe(arg0 :: args.toList)(null)

		def apply(log: ProcessLogger = null, cwd: File = null)(args: AnyRef*): Int = exe(args.toList)(log, cwd)

		private final def exe(args: List[AnyRef])(log: ProcessLogger, cwd: File = null): Int =
			call(
				args.toList.map {
					case text: String => text
				},
				log,
				cwd
			)

		protected def call(args: List[String], log: ProcessLogger, cwd: File): Int

	}

	object T {


		def os[O: ClassTag](act: (String, String) => O): O = {
			act(
				System.getProperty("os.name").takeWhile(' ' != (_: Char)).toLowerCase(),
				System.getProperty("os.arch").takeWhile(' ' != (_: Char)).toLowerCase()
			)

		}

		implicit class extendString(text: String) {
			def /(path: String): File = new File(text) / path

			def drop(prefix: String): String = {
				require(text.startsWith(prefix))
				text.substring(prefix.length)
			}

		}

		implicit class extendFile(file: File) {
			def EnsureParent: File = {
				ParentFile.EnsureDirectory
				file.getAbsoluteFile
			}

			def ParentFile: File = file.getAbsoluteFile.getParentFile.getAbsoluteFile

			def EnsureDirectory: File = {
				require(file.getAbsoluteFile.isDirectory || file.getAbsoluteFile.mkdirs())
				file.getAbsoluteFile
			}

			def AbsolutePath: String = file.getAbsolutePath.replace('\\', '/')

			def /(path: String): File =
				if (path.startsWith("../"))
					new extendFile(file.getAbsoluteFile.getParentFile) / path.drop(3)
				else
					new File(file.getAbsoluteFile, path).getAbsoluteFile

			def Unlink(): Unit =
				Option(file.listFiles()).toList.flatten.map((_: File).getAbsoluteFile)
					.foreach {
						file =>
							if (file.isDirectory)
								file.Unlink()
							require(file.delete())
					}
		}

	}

}
