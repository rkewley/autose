    
 package models

 object Courses {
   def compare(a: MdlCourses, b: MdlCourses): Boolean = {
     (a.vAcademicYear*10 + a.vAcademicTerm) - (b.vAcademicYear*10 + b.vAcademicTerm) match {
       case x if x < 0 => true
       case x if x > 0 => false
       case 0 => {
         a.vCourseIDNumber < b.vCourseIDNumber
       }
     }
   }
 }
 
 
 case class MdlCourses (
 	vidCourse : Option[Long],
	vCourseIDNumber : String,
	tempAcademicYear : Long,
	vAcademicTerm : Long,
	vCourseName : String,
	vCourseDirector : Long,
	vProgramDirector : Long,
	vCourseDescriptionRedbook : String,
	vCreditHours : Double,
	vPrerequisites : String,
	vCorequisites : String,
	vETCredits : Double,
	vCourseStrategy : String,
	vCriteriaForPassing : String,
	vAdminInstructions : String,
	vDepartmentID : Long,
	vCourseWebsite : Boolean,
	vCourseDescriptionWebsite : String
    ) extends Mdl[Long] {

      val vAcademicYear: Long = tempAcademicYear match {
        case t if t >= 12 && t <= 20 => tempAcademicYear + 2000
        case _ => tempAcademicYear
      }

   
      def this() = this(Option(0), "", 0, 0, "", 0, 0, "", 0.0, "", "", 0.0, "", "", "", 0, false, "")

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
    
      def selectIdentifier: (String, String) = vidCourse.get.toString -> (vCourseIDNumber + " AT" + vAcademicYear + "-" + vAcademicTerm )
    
      def compare(a: MdlCourses, b: MdlCourses) = a.vidCourse.get.compareTo(b.vidCourse.get)
        
      def primaryKey = vidCourse
      
  def newCourse(newYear: Long, newTerm: Long) = MdlCourses(
    Option(0),
    vCourseIDNumber,
    newYear,
    newTerm,
    vCourseName,
    vCourseDirector,
    vProgramDirector,
    vCourseDescriptionRedbook,
    vCreditHours,
    vPrerequisites,
    vCorequisites,
    vETCredits,
    vCourseStrategy,
    vCriteriaForPassing,
    vAdminInstructions,
    vDepartmentID,
    vCourseWebsite,
    vCourseDescriptionWebsite)

}
    