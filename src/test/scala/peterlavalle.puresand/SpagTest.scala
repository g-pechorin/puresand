package peterlavalle.puresand

import java.io.File

import org.scalatest.funsuite.AnyFunSuite

import scala.sys.process.ProcessLogger

class SpagTest extends AnyFunSuite {

	test("install the thing") {
		TempDir() {
			temp: File =>

				val instance = NodeJS.open(temp)

				require {
					"0.14.1" == instance("purs", "purescript", "0.14.1").!(null, "--version")
				}
				require {
					"0.20.3" == instance("spago", "0.20.3").!(null, "--version")
				}
		}
	}

	test("init and build") {
		TempDir() {
			node: File =>

				val instance = NodeJS.open(node)

				// install both things
				val spago: NodeJS.Command = {

					// install purescript first
					require {
						// need to "kick it" with a check
						"0.14.1" == instance("purs", "purescript", "0.14.1").!(null, "--version")
					}

					// setup spago
					val spago: NodeJS.Command = instance("spago", "0.20.3")

					// again - kick it
					require {
						0 == spago("--version")
					}
					spago
				}

				TempDir() {
					project: File =>

						require {
							0 == spago(null: ProcessLogger, project)("init")
						}

						require {
							0 == spago(null: ProcessLogger, project)("build")
						}
				}
		}
	}

}
