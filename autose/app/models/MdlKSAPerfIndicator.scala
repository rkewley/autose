    
 package models
    
 case class MdlKSAPerfIndicator (
 	vidKSAPerfIndicator : Option[Long],
	vKSA : Long,
	vPerformanceIndicator : Long
    ) extends Mdl[Long] {
    
      def this() = this(Option(0), 0, 0)

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vidKSAPerfIndicator.get.toString -> vidKSAPerfIndicator.get.toString
    
      def compare(a: MdlKSAPerfIndicator, b: MdlKSAPerfIndicator) = a.vidKSAPerfIndicator.get.compareTo(b.vidKSAPerfIndicator.get)
        
      def primaryKey = vidKSAPerfIndicator
}
    