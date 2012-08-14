package search.managers

import search.indexing.InvertedIndex
import search.indexing.SearchRanker
import java.io.File
import search.documents.Document
import search.parsing.Parser

object SearchManager {
  def apply(folder: File):SearchManager = {
    val x = new SearchManager()
    x.addFolderToIndex(folder)
    x
  }
}

case class SearchManager {

  private val _index: InvertedIndex = new InvertedIndex()
  private val ranker: SearchRanker = new SearchRanker(index)
  private val documentManager = new DocumentManager()
  private val parser = new Parser()

  def SearchManager(folder: File) = {
   this.addFolderToIndex(folder)
  }
  
  def addFileToIndex(filename: String): Document = {
    addFileToIndex(new File(filename))
  }

  def addFileToIndex(file: File): Document = {
    val document = documentManager.parseFile(file)
    doesDocumentAlreadyExist(document) match {
      case Some(docFound) => docFound
      case None => {
        _index.addDocumentToIndex(document)
        document
      }
    }
  }

  def addFolderToIndex(folder: File): List[Document] = {
    val files = folder.listFiles()
    files.map(addFileToIndex(_)).toList
  }

  def doesDocumentAlreadyExist(document: Document): Option[Document] = {
    index.getAllDocuments.find(d => d.name == document.name)
  }

  def query(input: String):List[(Document, Double)] = {
    val queryable = parser.parse(input)
    ranker.calcQueryScoreCombined(queryable).filter(d => d._2 > 0.0)
  }

  def index = _index

}