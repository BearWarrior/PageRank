package tse.pagerank.nayuki;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/* 
 * Computing Wikipedia's internal PageRanks
 * 
 * Copyright (c) 2016 Project Nayuki
 * All rights reserved. Contact Nayuki for licensing.
 * https://www.nayuki.io/page/computing-wikipedias-internal-pageranks
 */

import java.util.Arrays;
import java.util.Map;

import tse.pagerank.model.Page;
import tse.pagerank.ordonnanceur.GrandManitou;


/* 
 * Calculates PageRank, by encapsulating a list of links and a list of current PageRank values.
 */
public final class Pagerank {
	
	/*---- Fields ----*/
	
	// The vector of current PageRank values, changing after each iteration. Length equals idLimit.
	// Other classes can read this data, but should not modify it.
	public double[] pageranks;
	
	
	// List of page-to-page links in a packed run-length format:
	// (target page ID, number of incoming links, source page IDs...), ... .
	private int[] links;
	
	// Maximum page ID value plus 1. This sets the length of various arrays.
	private int idLimit;
	
	// Number of page IDs with incoming links or outgoing links (ignores disconnected nodes).
	private int numActive;
	
	// Indicates whether each page ID is active or not. Length equals idLimit.
	private boolean[] isActive;
	
	// The number of outgoing links each page ID has. Length equals idLimit.
	private int[] numOutgoingLinks;
	
	// Temporary array, which is filled and discarded per iteration. Length equals idLimit.
	private double[] newPageranks;
	
	//File which can contains the raw pagerank (for faster process)
	private static final File PAGERANKS_RAW_FILE = new File("wikipedia-pageranks.raw");  // Output file
	
	/*---- Constructor ----*/
	
	// Constructs a PageRank calculator based on the given array of links
	// in the compressed format returned by class PageLinksList.
	public Pagerank(int[] links) {
		this.links = links;
		
		// Find highest page ID among all links
		int maxId = 0;
		for (int i = 0; i < links.length; ) {
			int dest = links[i];
			maxId = Math.max(dest, maxId);
			int numIncoming = links[i + 1];
			for (int j = 0; j < numIncoming; j++) {
				int src = links[i + 2 + j];
				maxId = Math.max(src, maxId);
			}
			i += numIncoming + 2;
		}
		idLimit = maxId + 1;
		
		// Compute metadata fields
		boolean[] hasIncomingLinks = new boolean[idLimit];
		numOutgoingLinks = new int[idLimit];
		for (int i = 0; i < links.length; ) {
			int dest = links[i];
			hasIncomingLinks[dest] = true;
			int numIncoming = links[i + 1];
			for (int j = 0; j < numIncoming; j++) {
				int src = links[i + 2 + j];
				numOutgoingLinks[src]++;
			}
			i += numIncoming + 2;
		}
		isActive = new boolean[idLimit];
		numActive = 0;
		for (int i = 0; i < idLimit; i++) {
			if (numOutgoingLinks[i] > 0 || hasIncomingLinks[i]) {
				isActive[i] = true;
				numActive++;
			}
		}
		
		// Initialize PageRanks uniformly for active pages
		pageranks = new double[idLimit];
		double initWeight = 1.0 / numActive;
		for (int i = 0; i < idLimit; i++) {
			if (isActive[i])
				pageranks[i] = initWeight;
		}
		newPageranks = new double[idLimit];
	}
	
	
	/*---- Methods ----*/
	
	// Performs one iteration of the PageRank algorithm and updates the values in the array 'pageranks'.
	public void iterateOnce(double damping) {
		// Pre-divide by number of outgoing links
		for (int i = 0; i < idLimit; i++) {
			if (numOutgoingLinks[i] > 0)
				pageranks[i] /= numOutgoingLinks[i];
		}
		
		// Distribute PageRanks over links (main calculation)
		Arrays.fill(newPageranks, 0);
		for (int i = 0; i < links.length; ) {
			int numIncoming = links[i + 1];
			double sum = 0;
			for (int j = 0; j < numIncoming; j++) {
				int src = links[i + 2 + j];
				sum += pageranks[src];
			}
			int dest = links[i];
			newPageranks[dest] = sum;
			i += numIncoming + 2;
		}
		
		// Calculate global bias due to pages without outgoing links
		double bias = 0;
		for (int i = 0; i < idLimit; i++) {
			if (isActive[i] && numOutgoingLinks[i] == 0)
				bias += pageranks[i];
		}
		bias /= numActive;
		
		// Apply bias and damping to all active pages
		double temp = bias * damping + (1 - damping) / numActive;  // Factor out some arithmetic
		for (int i = 0; i < idLimit; i++) {
			if (isActive[i])
				pageranks[i] = newPageranks[i] * damping + temp;
		}
	}
	
	public void setPageRanksToPages()
	{
		for (int i = 0; i < pageranks.length; i++) 
		{
			if(pageranks[i] != 0) 
			{
				if(GrandManitou.hashPage.get(i) != null)
				{
					GrandManitou.hashPage.get(i).setPagerank(pageranks[i]);
				}
			}
		}
	}
	
	public void createRawFile()
	{
		DataOutputStream out;
		try {
			out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(PAGERANKS_RAW_FILE)));
			
			try {
				for (double x : this.pageranks)
					out.writeDouble(x);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static double[] printPagerankChangeRatios(double[] prevPr, double[] pr) 
	{
		double[] out = new double[2];
		double min = Double.POSITIVE_INFINITY;
		double max = 0;
		for (int i = 0; i < pr.length; i++) 
		{
			if (pr[i] != 0 && prevPr[i] != 0) 
			{
				double ratio = pr[i] / prevPr[i];
				min = Math.min(ratio, min);
				max = Math.max(ratio, max);
			}
		}
		out[0] = min;
		out[1] = max;
		//System.out.println("Range of ratio of changes: " + min + " to " + max);
		return out;
	}
	
	private static void printTopPages(double[] pageranks, Map<Integer, Page> titleById) 
	{
		final int NUM_PAGES = 30;
		double[] sorted = pageranks.clone();
		Arrays.sort(sorted);
		for (int i = 0; i < NUM_PAGES; i++) 
		{
			for (int j = 0; j < sorted.length; j++) 
			{
				if (pageranks[j] == sorted[sorted.length - 1 - i]) 
				{
					System.out.printf("  %.3f  %s%n", Math.log10(pageranks[j]), titleById.get(j));
					break;
				}
			}
		}
	}
}