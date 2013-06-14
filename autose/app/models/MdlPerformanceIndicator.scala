    
 package models
    
 case class MdlPerformanceIndicator (
 	vidTerminalLearningObjective : Option[Long],
	vTerminalLearningObjective : String,
	vTopic : Long,
	vProgram : Long
    ) extends Mdl[Long] {
    
      def this() = this(Option(0), "", 0, 0)

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vidTerminalLearningObjective.get.toString -> vidTerminalLearningObjective.get.toString
    
      def compare(a: MdlPerformanceIndicator, b: MdlPerformanceIndicator) = a.vidTerminalLearningObjective.get.compareTo(b.vidTerminalLearningObjective.get)
        
      def primaryKey = vidTerminalLearningObjective
}
    