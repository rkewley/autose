package models

trait Mdl[K] {
  
  def primaryKey: Option[K]
 
  def validate: Boolean
    
  def validationErrors: String 
    
  def selectIdentifier: (String, String)
    

}