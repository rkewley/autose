    
 package models
    
 case class MdlStudents (
 	vidStudents : Option[Long],
	vStudentId : String,
	vLastname : String,
	vFirstname : String,
	vMajorAMS : String,
	vGradYear : Long,
	vProgram : Long
    ) extends Mdl[Long] {
    
      def this() = this(Option(0), "", "", "", "", 0, 0)

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vidStudents.get.toString -> vidStudents.get.toString
    
      def compare(a: MdlStudents, b: MdlStudents) = a.vidStudents.get.compareTo(b.vidStudents.get)
        
      def primaryKey = vidStudents
}
    