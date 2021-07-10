package peterlavalle.puresand

import org.scalatest.funsuite.AnyFunSuite

class NoNodeTest extends AnyFunSuite {
	test("run and check version") {
		TempDir.cache() {
			temp =>
				assert(
					(0, List(";0.20.3")) == {
						NoNode(temp)("version") ! ()
					}
				)
		}
	}
}
