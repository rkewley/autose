    
 package models
    
 case class MdlCourseOfferings (
 	vidCourseOfferings : Long,
	vCourse : Long,
	vClassHour : String,
	vSection : Long,
	vLocation : String,
	vInstructorEmail : String
    )  {
    
      def this() = this(0, 0, "", 0, "", "")

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vidCourseOfferings.toString -> vidCourseOfferings.toString
    
      def compare(a: MdlCourseOfferings, b: MdlCourseOfferings) = a.vidCourseOfferings.compareTo(b.vidCourseOfferings)
}
    