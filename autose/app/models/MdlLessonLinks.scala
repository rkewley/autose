    
 package models
    
 case class MdlLessonLinks (
 	vLessonLinkNumber : Long,
	vLink : String,
	vDescription : String,
	vIsFileLiink : Boolean,
	vLesson : Long,
	vFacultyEmail: String
    )  {
    
      def this() = this(0, "", "", false, 0, "all")

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vLessonLinkNumber.toString -> vLessonLinkNumber.toString
    
      def compare(a: MdlLessonLinks, b: MdlLessonLinks) = a.vLessonLinkNumber.compareTo(b.vLessonLinkNumber)
}
    