package org.pisfly

import org.scalatest.FunSuite

/**
 * Created by fatihdonmez on 29/11/15
 */
class ButterflyTest extends FunSuite {

  test("Butterfly print for console") {

    val b4 = new ButterflyShape(4)
    println(b4.printButterfly)
  }

  test("Butterfly should work as expected for known sizes") {

    val expected = "+       + \n+ + - + + \n+       + \n"
    val b2 = new ButterflyShape(2)

    val result = b2.printButterfly

    assertResult(expected)(b2.printButterfly)
  }
}
