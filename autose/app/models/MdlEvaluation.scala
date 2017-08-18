    
 package models
    
 case class MdlEvaluation (
 	vidEvaluation : Option[Long],
	vName : String,
	vProgram : Long,
	vYear : Long
    ) extends Mdl[Long] {
    
      def this() = this(Option(0), "", 0, 0)

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vidEvaluation.get.toString -> (vName + " " + vYear)
    
      def compare(a: MdlEvaluation, b: MdlEvaluation) = a.vidEvaluation.get.compareTo(b.vidEvaluation.get)
        
      def primaryKey = vidEvaluation
}
    