    
 package models
    
 case class MdlTerminalLearningObjective (
 	vidTerminalLearningObjective : Long,
	vTerminalLearningObjective : String,
	vTopic : Long,
	vProgram : Long
    )  {
    
      def this() = this(0, "", 0, 0)

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vidTerminalLearningObjective.toString -> vTerminalLearningObjective
    
      def compare(a: MdlTerminalLearningObjective, b: MdlTerminalLearningObjective) = a.vidTerminalLearningObjective.compareTo(b.vidTerminalLearningObjective)
}
    