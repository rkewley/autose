    
 package models
    
 case class MdlEvalCourseObjSubEvent (
 	vidEvalCourseObjSubEvent : Option[Long],
	vEvaluation : Long,
	vCourseObjSubEvent : Long
    ) extends Mdl[Long] {
    
      def this() = this(Option(0), 0, 0)

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vidEvalCourseObjSubEvent.get.toString -> vidEvalCourseObjSubEvent.get.toString
    
      def compare(a: MdlEvalCourseObjSubEvent, b: MdlEvalCourseObjSubEvent) = a.vidEvalCourseObjSubEvent.get.compareTo(b.vidEvalCourseObjSubEvent.get)
        
      def primaryKey = vidEvalCourseObjSubEvent
}
    