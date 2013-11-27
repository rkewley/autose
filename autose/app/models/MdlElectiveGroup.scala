    
 package models
    
 case class MdlElectiveGroup (
 	vidElectiveGroup : Option[Long],
	vElectiveGroupName : String,
	vNumberToChoose : Long,
	vProgram : Long,
	vSubDiscipline : Boolean
    ) extends Mdl[Long] {
    
      def this() = this(Option(0), "", 0, 0, false)

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vidElectiveGroup.get.toString -> vidElectiveGroup.get.toString
    
      def compare(a: MdlElectiveGroup, b: MdlElectiveGroup) = a.vidElectiveGroup.get.compareTo(b.vidElectiveGroup.get)
        
      def primaryKey = vidElectiveGroup
}
    