    
 package models
    
 case class MdlLessonObjective (
 	vidLessonObjective : Option[Long],
	vLesson : Long,
	vSupportedCourseObjective : Long,
	vLessonObjective : String
    ) extends Mdl[Long] {
    
      def this() = this(Option(0), 0, 0, "")

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vidLessonObjective.get.toString -> vidLessonObjective.get.toString
    
      def compare(a: MdlLessonObjective, b: MdlLessonObjective) = a.vidLessonObjective.get.compareTo(b.vidLessonObjective.get)
        
      def primaryKey = vidLessonObjective
}
    