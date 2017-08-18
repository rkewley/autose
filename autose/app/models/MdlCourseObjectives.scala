    
 package models
    
 case class MdlCourseObjectives (
 	vObjectiveID : Option[Long],
	vCourseIDNumber : Long,
	vObjective : String
    ) extends Mdl[Long] {
    
      def this() = this(Option(0), 0, "")

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vObjectiveID.get.toString -> vObjective.toString
    
      def compare(a: MdlCourseObjectives, b: MdlCourseObjectives) = a.vObjectiveID.get.compareTo(b.vObjectiveID.get)
        
      def primaryKey = vObjectiveID

}
    