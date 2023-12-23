package com.sorbonne.daar.utils.recherche;

import java.util.ArrayList;
import java.util.HashMap;

public class NFA {
	protected int start_state;
    protected int end_state;
    protected HashMap <Integer, HashMap<Integer,ArrayList<Integer>>> arcs; // ( stat1 ) -> ( caractére -> [stat 2] )
    
    public NFA(int start_state, int end_state, HashMap<Integer, HashMap<Integer,ArrayList<Integer>>> arcs) {
		this.start_state = start_state;
		this.end_state = end_state;
		this.arcs = arcs;
	}

	public void print() {
		System.out.println("--------- NFA -----------");
		System.out.println("start state = " + start_state);
		System.out.println("end state = " + end_state);
		for (Integer state : arcs.keySet()) {
			System.out.print(state + " : ");
			for (Integer i1 : arcs.get(state).keySet()) {
				System.out.print(ProjectEgrep.valueToString(i1) + " -> [");
				System.out.print(arcs.get(state).get(i1));
				System.out.print("], ");
			}
			System.out.println();
		}
	}
}
