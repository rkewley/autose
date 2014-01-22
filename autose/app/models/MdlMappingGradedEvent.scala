    
 package models
    
 case class MdlMappingGradedEvent (
 	vidMappingGradedEvent : Option[Long],
	vGradedEvent : Long,
	vGradedEventAMS : Long
    ) extends Mdl[Long] {
    
      def this() = this(Option(0), 0, 0)

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vidMappingGradedEvent.get.toString -> vidMappingGradedEvent.get.toString
    
      def compare(a: MdlMappingGradedEvent, b: MdlMappingGradedEvent) = a.vidMappingGradedEvent.get.compareTo(b.vidMappingGradedEvent.get)
        
      def primaryKey = vidMappingGradedEvent
}
    