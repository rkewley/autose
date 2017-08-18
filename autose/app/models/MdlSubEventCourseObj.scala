    
 package models
    
 case class MdlSubEventCourseObj (
 	vidSubEventCourseObj : Option[Long],
	vSubEventAMS : Long,
	vCourseObj : Long
    ) extends Mdl[Long] {
    
      def this() = this(Option(0), 0, 0)

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vidSubEventCourseObj.get.toString -> vidSubEventCourseObj.get.toString
    
      def compare(a: MdlSubEventCourseObj, b: MdlSubEventCourseObj) = a.vidSubEventCourseObj.get.compareTo(b.vidSubEventCourseObj.get)
        
      def primaryKey = vidSubEventCourseObj
}
    