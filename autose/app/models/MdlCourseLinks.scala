    
 package models
    
 case class MdlCourseLinks (
 	vidCourseLinks : Long,
	vCourse : Long,
	vLink : String,
	vDisplayDescription : String,
	vIsFileLink : Boolean,
	vFaculty : Long
    )  {
    
      def this() = this(0, 0, "", "", false, -1)

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vidCourseLinks.toString -> vidCourseLinks.toString
    
      def compare(a: MdlCourseLinks, b: MdlCourseLinks) = a.vidCourseLinks.compareTo(b.vidCourseLinks)
}
    