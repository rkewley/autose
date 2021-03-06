    
 package models
 
 object MdlGradedRequirements { 
	 def compare(a: MdlGradedRequirements, b: MdlGradedRequirements): Boolean = a.vLessoncompleted < b.vLessoncompleted
 }
    
 case class MdlGradedRequirements (
 	vGradedEventIndex : Option[Long],
	vCourse : Long,
	vGradedEventName : String,
	vEventDescription : String,
	vTypeOfEvent : Long,
	vPoints : Double,
	vLessonassigned : Long,
	vLessoncompleted : Long
    ) extends Mdl[Long] {
    
      def this() = this(Option(0), 0, "", "", 0, 0.0, 0, 0)

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vGradedEventIndex.get.toString -> vGradedEventName
    
        
      def primaryKey = vGradedEventIndex
}
    