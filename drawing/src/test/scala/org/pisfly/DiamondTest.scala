package org.pisfly

import org.scalatest.FunSuite

/**
 * Created by fatihdonmez on 29/11/15
 */
class DiamondTest extends FunSuite {

  test("Diamond print for console") {

    val d5 = new DiamondShape(5)
    println(d5.printDiamond)
  }

  test("Diamond should work as expected for known sizes") {

    val expected = "  + \n+ + + \n  + \n"
    val d1 = new DiamondShape(3)

    assertResult(expected)(d1.printDiamond)
  }

}
