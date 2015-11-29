package org.pisfly

/**
 * Created by fatihdonmez on 26/11/15
 */
class DiamondShape(private val n: Int) {

  /**
   * Create a diamond string for size n
   */
  def printDiamond: String = {

    var resultStr = ""

    //Upper half of diamond
    for(i <- 1 to n by 2) {
      val space = (n - i) / 2
      val plus = i

      resultStr += ("  " * space)
      resultStr += ("+ " * plus)
      resultStr += "\n"
    }

    //Last half of the diamond
    for(i <- (n - 2) to 1 by -2) {
      val space = (n - i) / 2
      val plus = i

      resultStr += ("  " * space)
      resultStr += ("+ " * plus)
      resultStr += "\n"
    }

    resultStr
  }
}

class ButterflyShape(private val n: Int) {

  /**
   * Create a butterfly string for size n
   */
  def printButterfly: String = {

    var resultStr = ""

    //Upper half of the butterfly
    for(i <- 1 to n) {
      val space = n*2 - i*2 + 1
      val plus = i

      resultStr += ("+ " * plus)

      //Check it's middle or not
      space match {
        case 1 =>  resultStr +="- "
        case _ => resultStr += "  " * space

      }

      resultStr += ("+ " * plus)
      resultStr += "\n"
    }

    //Other half of the butterfly
    for(i <- (n - 1) to 1 by -1) {
      val space = n*2 - i*2 + 1
      val plus = i

      resultStr += ("+ " * plus + "  " * space + "+ " * plus + "\n")

    }

    resultStr
  }
}
