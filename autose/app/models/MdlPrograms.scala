    
 package models
    
 case class MdlPrograms (
    vidPrograms : Option[Long],
 	vProgram : String,
	vName : String,
	vSlogan : String,
	vInformation : String,
	vProgramDirector : Long,
	vDepartment : Long
    ) extends Mdl[Long] {
    
      def this() = this(Some(0), "", "", "", "", 0, 0)
      
      def primaryKey: Option[Long] = vidPrograms

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vidPrograms.toString -> vProgram.toString
    
      def compare(a: MdlPrograms, b: MdlPrograms) = a.vidPrograms.get.compareTo(b.vidPrograms.get)
}
    