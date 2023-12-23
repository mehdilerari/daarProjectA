package com.sorbonne.daar.utils.recherche;

import java.util.ArrayList;
import java.util.HashMap;

public class DFA {
	protected DFAState start_state;
	protected ArrayList<DFAState> end_state;
	protected HashMap <DFAState, HashMap<Integer,DFAState>> arcs; // ( stat1 ) -> ( value -> stat 2 )
    
    public DFA(DFAState start_state, ArrayList<DFAState> end_state, HashMap<DFAState, HashMap<Integer,DFAState>> arcs) {
		this.start_state = start_state;
		this.end_state = end_state;
		this.arcs = arcs;
	}
    
	public void buildNumbers(ArrayList<DFAState> allStates) {
		
    	//we give each state a number
    	for (DFAState dfa : allStates) {
    		dfa.setNumber(ProjectEgrep.compteurDFA);
			ProjectEgrep.compteurDFA++;
		}
    	
    	// we look for all the states to give theme their corresponding number
    	for (DFAState dfa : arcs.keySet()) {
    		if (allStates.contains(dfa)) {
    			int i = allStates.indexOf(dfa);
    			dfa.setNumber(allStates.get(i).getNumber().intValue());
    		}
    		for (DFAState dfaState : arcs.get(dfa).values()) {
    			if (allStates.contains(dfaState)) {
        			int i = allStates.indexOf(dfaState);
        			dfaState.setNumber(allStates.get(i).getNumber().intValue());
        		}
    		}
		}
    }

	public void print() {
		System.out.println("--------- DFA -----------");
		System.out.println("start state = " + start_state.getNumber());
		System.out.print("end state = ");
		for(DFAState state : end_state){
			System.out.print(state.getNumber()+" ");
		}
		System.out.println("");
		System.out.println("tout les arcs : ");
		for (DFAState dfa : arcs.keySet()) {
			System.out.print(dfa.getNumber() + " : ");
			for (Integer i1 : arcs.get(dfa).keySet()) {
				System.out.print(ProjectEgrep.valueToString(i1) + " -> [");
				System.out.print(arcs.get(dfa).get(i1).getNumber());
				System.out.print("], ");
			}
			System.out.println();
		}
	}
}
