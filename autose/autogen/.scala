    
 package models
 case class  (
    	CourseIDNumber : String,
	CourseName : String,
	CourseDirectorEmail : String,
	ProgramDirectorEmail : String,
	CourseDescriptionRedbook : String,
	CreditHours : java.lang.Double,
	Prerequisites : String,
	Corequisites : String,
	Disqualifiers : String,
	CourseStrategy : String,
	CriteriaForPassing : String,
	AdminInstructions : String,
	DepartmentID : Int,
	CourseWebsite : Boolean,
	CourseDescriptionWebsite : String
    )  {
  
  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
}
    