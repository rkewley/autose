    
 package models
    
 case class MdlLessonLinks (
 	vLessonLinkNumber : Option[Long],
	vLink : String,
	vDescription : String,
	vIsFileLiink : Boolean,
	vLesson : Long,
	vFaculty : Long
    ) extends Mdl[Long] {
    
      def this() = this(Option(0), "", "", false, 0, 0)

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vLessonLinkNumber.get.toString -> vLessonLinkNumber.get.toString
    
      def compare(a: MdlLessonLinks, b: MdlLessonLinks) = a.vLessonLinkNumber.get.compareTo(b.vLessonLinkNumber.get)
        
      def primaryKey = vLessonLinkNumber
}
    