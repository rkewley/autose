    
 package models
    
 case class MdlRequiredCourses (
 	vidRequiredCourses : Option[Long],
	vProgram : Long,
	vCourse : String
    ) extends Mdl[Long] {
    
      def this() = this(Option(0), 0, "")

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vidRequiredCourses.get.toString -> vidRequiredCourses.get.toString
    
      def compare(a: MdlRequiredCourses, b: MdlRequiredCourses) = a.vidRequiredCourses.get.compareTo(b.vidRequiredCourses.get)
        
      def primaryKey = vidRequiredCourses
}
    