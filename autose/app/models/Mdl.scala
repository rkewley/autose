package models

abstract class Mdl[K] {
  
  def primaryKey: K
 
  def validate: Boolean
    
  def validationErrors: String 
    
  def selectIdentifier: (String, String)
    
  def compare(a: Mdl[K], b: Mdl[K]): Int
  
  def test = "Test"

}