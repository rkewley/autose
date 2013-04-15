
package persistence

import models.MdlLessonDate
import anorm._
import anorm.SqlParser._
import play.api.db._
import java.util.Date
import play.api.Play.current
import play.Logger

object SqlLessonDate {

  val vLessonDate = {
    get[Long]("idLessonDate") ~
	get[Long]("Lesson") ~
	get[Long]("AcademicYear") ~
	get[Long]("AcademicTerm") ~
	get[Date]("Day1") ~
	get[Date]("Day2") map { case
    vidLessonDate ~
		vLesson ~
		vAcademicYear ~
		vAcademicTerm ~
		vDay1 ~
		vDay2 =>
    MdlLessonDate(vidLessonDate,
		vLesson,
		vAcademicYear,
		vAcademicTerm,
		vDay1,
		vDay2)
    }
  }

  	def all: List[MdlLessonDate] = DB.withConnection { implicit c =>
  		SQL("select * from `LessonDate`").as(vLessonDate *)
	}

	def select(vidLessonDate: Long): MdlLessonDate = DB.withConnection { implicit c =>
  		SQL("select * from `LessonDate` WHERE `idLessonDate` = {sqlidLessonDate}").on(
  			'sqlidLessonDate -> vidLessonDate).as(vLessonDate *).head
	}

  	def selectWhere(where: String): List[MdlLessonDate] = DB.withConnection { implicit c =>
  		SQL("select * from `LessonDate` WHERE " + where).as(vLessonDate *)
	}

	def delete(vidLessonDate: Long) = DB.withConnection { implicit c =>
  		SQL("DELETE FROM `LessonDate` WHERE `idLessonDate` = {sqlidLessonDate}").on(
      'sqlidLessonDate -> vidLessonDate
  		).executeUpdate()
    }

	def insert(vLessonDate: MdlLessonDate) = DB.withConnection { implicit c =>
  		SQL("INSERT INTO `LessonDate` (`idLessonDate`, `Lesson`, `AcademicYear`, `AcademicTerm`, `Day1`, `Day2`) VALUES ({sqlidLessonDate}, {sqlLesson}, {sqlAcademicYear}, {sqlAcademicTerm}, {sqlDay1}, {sqlDay2})").on('sqlidLessonDate -> vLessonDate.vidLessonDate, 'sqlLesson -> vLessonDate.vLesson, 'sqlAcademicYear -> vLessonDate.vAcademicYear, 'sqlAcademicTerm -> vLessonDate.vAcademicTerm, 'sqlDay1 -> vLessonDate.vDay1, 'sqlDay2 -> vLessonDate.vDay2).executeInsert()
	}

	def update(vLessonDate: MdlLessonDate) = DB.withConnection { implicit c =>
  		SQL("UPDATE `LessonDate` SET `Lesson` = {sqlLesson}, `AcademicYear` = {sqlAcademicYear}, `AcademicTerm` = {sqlAcademicTerm}, `Day1` = {sqlDay1}, `Day2` = {sqlDay2} WHERE `idLessonDate` = {sqlidLessonDate}").on('sqlidLessonDate -> vLessonDate.vidLessonDate, 'sqlLesson -> vLessonDate.vLesson, 'sqlAcademicYear -> vLessonDate.vAcademicYear, 'sqlAcademicTerm -> vLessonDate.vAcademicTerm, 'sqlDay1 -> vLessonDate.vDay1, 'sqlDay2 -> vLessonDate.vDay2).executeUpdate()

  }

}