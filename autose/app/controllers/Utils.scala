package controllers

object Utils {
  
  def formatDouble2Places(d: Double) = {
    d.isNaN() match {
      case true => "-"
      case false => "%.2f" format d
    }
  }

}