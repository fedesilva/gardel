package gardel.core.util

import java.util.Enumeration
/**
 *  Holds implicit conversions to make easier iterating over java enumerations. <br/>
 *  Allows you to do: 
 *  <code>
 *  import java.util.zip.{ZipFile, ZipEntry}
 *  val l = List.fromIterator(new ZipFile(null:java.io.File).entries)
 *  </code>
 *  Credit: Peter Kofler, James Iry (http://stackoverflow.com/questions/1054571/converting-enumeration-to-iterator)
 */
object EnumerationWrapper {

  implicit def enum2Iterator[A](e : Enumeration[A]) = new Iterator[A] {
    def next = e.nextElement
    def hasNext = e.hasMoreElements
  }

}

