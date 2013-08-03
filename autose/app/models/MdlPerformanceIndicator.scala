    
 package models
    
 case class MdlPerformanceIndicator (
 	vidPerformanceIndicator : Option[Long],
	vPerformanceIndicator : String,
	vProgramOutcome : Long
    ) extends Mdl[Long] {
    
      def this() = this(Option(0), "", 0)

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vidPerformanceIndicator.get.toString -> vPerformanceIndicator
    
      def compare(a: MdlPerformanceIndicator, b: MdlPerformanceIndicator) = a.vidPerformanceIndicator.get.compareTo(b.vidPerformanceIndicator.get)
        
      def primaryKey = vidPerformanceIndicator
}
    