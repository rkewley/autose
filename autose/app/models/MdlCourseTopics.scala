    
 package models
    
 case class MdlCourseTopics (
 	vCourseIDNumber : String,
	vidCourse : Long,
	vCourseName : String,
	vLessonNumber : Long,
	vLessonIndex : Long,
	vLessonName : String,
	vTopic : String,
	vTopicsIndex : Long,
	vObjective : String
    )  {
    
      def this() = this("", 0, "", 0, 0, "", "", 0, "")

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
}
    