package models

object ModelUtils {
  
  def compareListings(a: (String, String), b: (String, String)): Boolean = a._2 < b._2

}