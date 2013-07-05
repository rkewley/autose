    
 package models
    
 case class MdlLessonsSlick (
 	vLessonIndex : Option[Long],
	vLessonNumber : Long,
	vLessonName : String,
	vAssignment : String,
	vLocation : String,
	vidCourse : Long,
	vDuration : Long,
	vLab : Boolean,
	vLessonSummary : String
    ) extends Mdl[Long] {
    
      def this() = this(Option(0), 0, "", "", "", 0, 0, false, "")

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vLessonIndex.get.toString -> vLessonIndex.get.toString
    
      def compare(a: MdlLessonsSlick, b: MdlLessonsSlick) = a.vLessonIndex.get.compareTo(b.vLessonIndex.get)
        
      def primaryKey = vLessonIndex
}
    