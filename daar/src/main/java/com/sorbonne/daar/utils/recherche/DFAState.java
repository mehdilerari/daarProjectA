package com.sorbonne.daar.utils.recherche;

import java.util.ArrayList;
import java.util.Collections;

public class DFAState {
	private ArrayList<Integer> values;
	private Integer number;
	
	public DFAState() { values = new ArrayList<>(); }
	
	public DFAState(ArrayList<Integer> val) { 
		values = val; 
	}

	public ArrayList<Integer> getValues() {
		return values;
	}

	public void sort() {
		Collections.sort(this.getValues());
	}
	
	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("[DFAState : ");
		for (Integer integer : values) {
			sb.append(integer + " ");
		}
		sb.append("]");
		return sb.toString();
	}
	
	@Override
	public boolean equals(Object s) {
		DFAState state = new DFAState();
		if (s instanceof DFAState) {
			state = (DFAState)s; 
		} else { return false; }
		
		this.sort();
		state.sort();
		
		if (this.getValues().size() != state.getValues().size()) return false;
		for (int i = 0; i < this.getValues().size(); i++) {
			if (!this.getValues().get(i).equals(state.getValues().get(i))) return false;
		}
		return true;
	}
	
	public DFAState copy() {
		DFAState res = new DFAState();
		
		for (Integer integer : this.getValues()) {
			int i = integer.intValue();
			res.getValues().add(new Integer(i));
		}
		return res;
	}
}