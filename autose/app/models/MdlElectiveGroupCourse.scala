    
 package models
    
 case class MdlElectiveGroupCourse (
 	vidElectiveGroupCourse : Option[Long],
	vElectiveGroup : Long,
	vCourse : String,
	vRequired : Boolean
    ) extends Mdl[Long] {
    
      def this() = this(Option(0), 0, "", false)

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vidElectiveGroupCourse.get.toString -> vidElectiveGroupCourse.get.toString
    
      def compare(a: MdlElectiveGroupCourse, b: MdlElectiveGroupCourse) = a.vidElectiveGroupCourse.get.compareTo(b.vidElectiveGroupCourse.get)
        
      def primaryKey = vidElectiveGroupCourse
}
    