    
 package models
    
 case class MdlCourses (
 	vCourseIDNumber : String,
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
	vDepartmentID : Int,
	vCourseWebsite : Boolean,
	vCourseDescriptionWebsite : String
    )  {
    
      def this() = this("", "", "", "", "", 0.0, "", "", "", "", "", "", 0, false, "")
  
  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
}
    