    
 package models
    
 case class MdlKSA (
 	vTopicObjectiveNumber : Option[Long],
	vObjective : String,
	vTopic : Long,
	vKSAB : String
    ) extends Mdl[Long] {
	  
      self =>
    
      def this() = this(Option(0), "", 0, "")

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vTopicObjectiveNumber.get.toString -> vTopicObjectiveNumber.get.toString
    
      def compare(a: MdlKSA, b: MdlKSA) = a.vTopicObjectiveNumber.get.compareTo(b.vTopicObjectiveNumber.get)
        
      def primaryKey = vTopicObjectiveNumber
      
}
    