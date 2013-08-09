    
 package models
 
 object MdlTopics {
   def compare(a: MdlTopics, b: MdlTopics) = a.vTopic < (b.vTopic)
 }
    
 case class MdlTopics (
 	vTopicsIndex : Long,
	vTopic : String,
	vTopicShortDescription : String,
	vTopicDetailedDescription : String
    )  {
    
      def this() = this(0, "", "", "")

  	  def validate: Boolean = true
    
	  def validationErrors: String = ""
    
      def selectIdentifier: (String, String) = vTopicsIndex.toString -> vTopic
    
      def compare(a: MdlTopics, b: MdlTopics) = a.vTopic.compareTo(b.vTopic)
}
    