package slick
import models.Mdl

trait Crud[A <: Mdl[K], K] {
  def all: List[A]
  def select(id: K): Option[A]
  def delete(id: K)
  def insert(item: A)
  def update(item: A)
  //def selectWhere(sql: String)
}