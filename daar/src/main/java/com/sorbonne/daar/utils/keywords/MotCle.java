package com.sorbonne.daar.utils.keywords;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class MotCle implements Comparable<MotCle>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2124878834992453643L;

	/** Racine du mot */
	private final String racine;
	
	/** Tous les mots ayant cette racine */
	private final Set<String> mots = new HashSet<String>();
	
	/** Nombre de fois où le mot apparait */
	private int frequence = 0;

	public MotCle(String racine) {
		this.racine = racine;
	}

	/**
	 * Ajoute un mot à la liste
	 * @param term
	 */
	public void add(String term) {
		mots.add(term);
		frequence++;
	}

	/**
	 * On classe les mots du set selon leur fréquence dans le texte
	 */
	@Override
	public int compareTo(MotCle o) {
		// descending order
		return Integer.valueOf(o.frequence).compareTo(frequence);
	}

	/**
	 * 2 mots sont considérés égaux s'ils ont la même racine
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (!(obj instanceof MotCle)) {
			return false;
		} else {
			return racine.equals(((MotCle) obj).racine);
		}
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(new Object[] { racine });
	}

	public String getRacine() {
		return racine;
	}

	public Set<String> getMots() {
		return mots;
	}

	public int getFrequence() {
		return frequence;
	}

	@Override
	public String toString() {
		return "MotCle [racine = " + racine + ", frequence = " + frequence + "]";
	}
}
