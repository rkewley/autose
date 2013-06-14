    
 package models
    
 case class MdlProgramOutcomes (
 	vProgramOutcomeNumber : Option[Long],
	vProgram : Long,
	vProgramOutcome : String
    ) extends Mdl[Long] {
    
      def this() = this(Option(0), 0, "")

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vProgramOutcomeNumber.get.toString -> vProgramOutcome
    
      def compare(a: MdlProgramOutcomes, b: MdlProgramOutcomes) = a.vProgramOutcomeNumber.get.compareTo(b.vProgramOutcomeNumber.get)
        
      def primaryKey = vProgramOutcomeNumber
}
    