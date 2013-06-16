    
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
    
      def selectIdentifier: (String, String) = vidPrograms.get.toString -> vProgram.toString
    
      def compare(a: MdlPrograms, b: MdlPrograms): Int = {
        return a.vProgram.compareTo(b.vProgram)
      }
      
      override def toString = "MdlPrograms: \n" +
        "idPrograms = " + vidPrograms.get + "\n" +
        "Program = " + vProgram + "\n" +
        "Name = " + vName + "\n" +
        "Slogan = " + vSlogan + "\n" +
        "Information = " + vInformation + "\n" +
        "ProgramDirector = " + vProgramDirector + "\n" +
        "Department = " + vDepartment + "\n"

}
    