    
 package models
    
 case class MdlMappingSubEvent (
 	vidMappingSubEvent : Option[Long],
	vSubEvent : Long,
	vSubEventAMS : Long
    ) extends Mdl[Long] {
    
      def this() = this(Option(0), 0, 0)

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vidMappingSubEvent.get.toString -> vidMappingSubEvent.get.toString
    
      def compare(a: MdlMappingSubEvent, b: MdlMappingSubEvent) = a.vidMappingSubEvent.get.compareTo(b.vidMappingSubEvent.get)
        
      def primaryKey = vidMappingSubEvent
}
    