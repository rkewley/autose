    
 package models
    
 case class MdlCourses (
 	vidCourse : Long,
	vCourseIDNumber : String,
	vAcademicYear : Long,
	vAcademicTerm : Long,
	vCourseName : String,
	vCourseDirectorEmail : String,
	vProgramDirectorEmail : String,
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
    )  {
    
      def this() = this(0, "", 0, 0, "", "", "", "", 0.0, "", "", "", "", "", "", 0, false, "")

  	  def validate: Boolean = vAcademicYear >= 2012 && vAcademicYear <= 2020 && vAcademicTerm >= 1 && vAcademicTerm <= 2
    
	  def validationErrors: String = {
  	    val ayError = (vAcademicYear >= 2012 && vAcademicYear <= 2020) match {
  	      case true => ""
  	      case false=> "Academic year should be between 2012 and 2020"
  	    } 
  	    val atError = (vAcademicTerm >= 1 && vAcademicTerm <= 2) match {
  	      case true => ""
  	      case false=> "Academic term should be 1 (for Fall) or 2 (for Spring)"  	      
  	    }
  	    ayError + atError
  	  }
    
      def selectIdentifier: (String, String) = vidCourse.toString -> vCourseIDNumber
    
      def compare(a: MdlCourses, b: MdlCourses) = a.vidCourse.compareTo(b.vidCourse)
}
    