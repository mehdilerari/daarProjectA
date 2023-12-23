package com.sorbonne.daar.utils.recherche;

import java.util.ArrayList;

//UTILITARY CLASS
public class RegExTree {
	protected int root;
	protected ArrayList<RegExTree> subTrees;

	public RegExTree(int root, ArrayList<RegExTree> subTrees) {
		this.root = root;
		this.subTrees = subTrees;
	}

	// FROM TREE TO PARENTHESIS
	public String toString() {
		if (subTrees.isEmpty())
			return rootToString();
		String result = rootToString() + "(" + subTrees.get(0).toString();
		for (int i = 1; i < subTrees.size(); i++)
			result += "," + subTrees.get(i).toString();
		return result + ")";
	}

	private String rootToString() {
		if (root == ProjectEgrep.CONCAT)
			return ".";
		if (root == ProjectEgrep.ETOILE)
			return "*";
		if (root == ProjectEgrep.ALTERN)
			return "|";
		if (root == ProjectEgrep.DOT)
			return ".";
		if (root == ProjectEgrep.EPSILON)
			return "e";
		if (root == ProjectEgrep.PLUS)
			return "+";
		
		return Character.toString((char) root);
	}
}