    
 package models
    
 case class MdlCoursesSlick (
 	vidCourse : Option[Long],
	vCourseIDNumber : String,
	vAcademicYear : Long,
	vAcademicTerm : Long,
	vCourseName : String,
	vCourseDirector : Long,
	vProgramDirector : Long,
	vCourseDescriptionRedbook : String,
	vCreditHours : Double,
	vPrerequisites : String,
	vCorequisites : String,
	vDisqualifiers : String,
	vCourseStrategy : String,
	vCriteriaForPassing : String,
	vAdminInstructions : String,
	vDepartmentID : Long,
	vCourseWebsite : Boolean,
	vCourseDescriptionWebsite : String
    ) extends Mdl[Long] {
    
      def this() = this(Option(0), "", 0, 0, "", 0, 0, "", 0.0, "", "", "", "", "", "", 0, false, "")

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vidCourse.get.toString -> vidCourse.get.toString
    
      def compare(a: MdlCoursesSlick, b: MdlCoursesSlick) = a.vidCourse.get.compareTo(b.vidCourse.get)
        
      def primaryKey = vidCourse
}
    