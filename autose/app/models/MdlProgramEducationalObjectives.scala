    
 package models
    
 case class MdlProgramEducationalObjectives (
 	vProgObjNumber : Option[Long],
	vProgram : Long,
	vProgramEducationalObjective : String
    ) extends Mdl[Long] {
    
      def this() = this(Option(0), 0, "")

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vProgObjNumber.get.toString -> vProgramEducationalObjective
    
      def compare(a: MdlProgramEducationalObjectives, b: MdlProgramEducationalObjectives) = a.vProgObjNumber.get.compareTo(b.vProgObjNumber.get)
        
      def primaryKey = vProgObjNumber
}
    