package utilities

import persistence.SqlFaculty
import models.MdlCourseLinks
import models.MdlLessonLinks

class Link (val href: String, val description: String) {
  override def toString = "<a href=\"" + href + "\">" + description + "</a>"
}
class FileGroup(val owner: String, val links: List[Link]) {
  override def toString = "File Group: " + owner + links.map(link => link.toString).mkString("\n")
}
object FileGroup {

  def addToList(fileList: List[FileGroup], owner: String, link: Link)= {
    val fileGroup = fileList.find(a => a.owner == owner)
	fileGroup match {
	  case Some(fg) =>
		fileList.updated(fileList.indexOf(fg), new FileGroup(owner, fg.links :+ link))
	  case None =>
		fileList :+ new FileGroup(owner, List(link))
	}  	  
  }
  
  def fileGroupFromLinks(courseLinks: List[(Long, Link)]) = {
    def addGroup(links: List[(Long, Link)], fileGroups: List[FileGroup]):List[FileGroup] = {
      if(links.isEmpty) fileGroups
      else {
        val head = links.head
        val owner = head._1 match {
          case -1 => "Course level file"
          case _ => SqlFaculty.select(head._1).selectIdentifier._2
        }
        val newFileGroups: List[FileGroup] = addToList(fileGroups, owner, head._2)
        addGroup(links.tail, newFileGroups)
      }
    }
    addGroup(courseLinks, List[FileGroup]()) 
  }
  
  def convertCourseLinks(courseLinks: List[MdlCourseLinks]): List[(Long, Link)] = {
    courseLinks.map(link => Tuple2(link.vFaculty, new Link(link.vLink, link.vDisplayDescription)))
  }

  def convertLessonLinks(lessonLinks: List[MdlLessonLinks]): List[(Long, Link)] = {
    lessonLinks.map(link => Tuple2(link.vFaculty, new Link(link.vLink, link.vDescription)))
  }

  def getFileGroupsLessonLinks(lessonLinks: List[MdlLessonLinks]) = fileGroupFromLinks(convertLessonLinks(lessonLinks))

  def getFileGroupsCourseLinks(courseLinks: List[MdlCourseLinks]) = fileGroupFromLinks(convertCourseLinks(courseLinks))
}
