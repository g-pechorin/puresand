package peterlavalle.puresand

import peterlavalle.puresand.NodeJS.{Command, Instance, open}

object DemoThing {

	def main(args: Array[String]): Unit = {

		println("heya!")

		val instance: Instance = open()

		val purs: Command = instance("purs", "purescript", "0.14.1")

		require {
			0 == instance("purs", "purescript", "0.14.1")("--version")
		}
		require {
			0 == instance("spago", "0.20.3")("--version")
		}

	}
}
