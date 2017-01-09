package tse.pagerank.main;

import java.io.File;

import tse.pagerank.io.InputOutput;

public class Main {
 public static void main(String[] args) 
 {
	 File file = new File("simplewiki-20161020-category.sql.gz");
	 
	 InputOutput.readSGZ(file);
	System.out.println("hello world!");
 }
}
