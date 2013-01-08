    
 package models
 
 import persistence.SqlTopicObjectives
    
 case class MdlTopicObjectives (
 	vTopicObjectiveNumber : Long,
	vObjective : String,
	vTopic : Long,
	vKSAB : String
    )  {
    
      def this() = this(0, "", 0, "")

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = {
        vTopicObjectiveNumber.toString -> SqlTopicObjectives.fullTopicName(this)
      }
    
      def compare(a: MdlTopicObjectives, b: MdlTopicObjectives) = a.vTopicObjectiveNumber.compareTo(b.vTopicObjectiveNumber)
}
    