package com.sorbonne.daar.utils.graph;

import java.io.Serializable;
import java.util.Arrays;

public class JaccardMatrice implements Serializable{
	
	private final static int NUMBER_OF_BOOKS = 1664;

	/**
	 */
	private static final long serialVersionUID = 204749000403344832L;
	
	public JaccardMatrice() {
		this.jaccardMatrice = new Float[NUMBER_OF_BOOKS][NUMBER_OF_BOOKS];
	}
	
	private Float[][] jaccardMatrice;

	public Float[][] getJaccardMatrice() {
		return jaccardMatrice;
	}

	public void setJaccardMatrice(Float[][] jaccardMatrice) {
		this.jaccardMatrice = jaccardMatrice;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < jaccardMatrice.length; i++) {
			sb.append(i + " ");
			for (int j = 0; j < jaccardMatrice.length; j++) {
				if(jaccardMatrice[i][j] != null) {
					sb.append(jaccardMatrice[i][j].toString() + " ");
				} else { 
					sb.append("?? "); 
				}
			}
			sb.append("\n");
		}
		return sb.toString();
	}
}
