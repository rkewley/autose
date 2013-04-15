    
 package models
 import java.util.Date
    
 case class MdlLessonDate (
 	vidLessonDate : Long,
	vLesson : Long,
	vAcademicYear : Long,
	vAcademicTerm : Long,
	vDay1 : Date,
	vDay2 : Date
    )  {
    
      def this() = this(0, 0, 0, 0, new Date(), new Date())

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vidLessonDate.toString -> vidLessonDate.toString
    
      def compare(a: MdlLessonDate, b: MdlLessonDate) = a.vidLessonDate.compareTo(b.vidLessonDate)
}
    