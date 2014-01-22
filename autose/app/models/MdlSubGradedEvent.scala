    
 package models
 
 import slick.AppDB
    
 case class MdlSubGradedEvent (
 	vidSubGradedEvent : Option[Long],
	vGradedEvent : Long,
	vDescription : String,
	vPoints : Double
    ) extends Mdl[Long] {
    
      def this() = this(Option(0), 0, "", 0.0)

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vidSubGradedEvent.get.toString -> {
        val gradedEventName = AppDB.dal.GradedRequirements.select(vGradedEvent).get.vGradedEventName
        gradedEventName + ", " + vDescription
      }
    
      def compare(a: MdlSubGradedEvent, b: MdlSubGradedEvent) = a.vidSubGradedEvent.get.compareTo(b.vidSubGradedEvent.get)
        
      def primaryKey = vidSubGradedEvent
}
    