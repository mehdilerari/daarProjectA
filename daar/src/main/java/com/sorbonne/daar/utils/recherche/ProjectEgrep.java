package com.sorbonne.daar.utils.recherche;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sorbonne.daar.DaarApplication;
import com.sorbonne.daar.utils.keywords.MotsClesExtractor;

public class ProjectEgrep {
	// MACROS
	static final int CONCAT = 0xC04CA7;
	static final int ETOILE = 0xE7011E;
	static final int PLUS = 0x625353;
	static final int ALTERN = 0xA17E54;
	static final int PROTECTION = 0xBADDAD;

	static final int PARENTHESEOUVRANT = 0x16641664;
	static final int PARENTHESEFERMANT = 0x51515151;
	static final int DOT = 0xD07;
	static final int EPSILON = 0x8765434;

	static int compteur = 0;
	static int compteurDFA = 0;
	static ArrayList<Integer> alphabet = new ArrayList<Integer>();
	static ArrayList<DFAState> allStates = new ArrayList<DFAState>();
	
	private static long beginTime;
	private static long endTime;

	// REGEX
	private static String regEx;
	
	// CONSTRUCTOR
	public ProjectEgrep() {
	}
	
	/**
	 * Research books using regex and aho-ullman algorithm
	 */
	public static List<Integer> advancedResearch(String regex) throws Exception {
		regEx = regex;
		List<Integer> ids = new ArrayList<>();
		
		if(isConcatenated(regEx)) {
			// KMP
			regEx = MotsClesExtractor.stem(regEx);
			ids = kmp(regEx, DaarApplication.keywords);
			return ids;
		}
		//regExTree
		RegExTree ret = parse();
		
		//nfa
		NFA nfa = toAutomaton(parse());
		
		//dfa
		DFA dfa = toDFA(nfa);
		dfa.buildNumbers(allStates);
				
		//Minimization
		ArrayList<ArrayList<DFAState>> partions = new ArrayList<ArrayList<DFAState>>(); 
		ArrayList<DFAState> non_final = new ArrayList<DFAState>(allStates);
		non_final.removeAll(dfa.end_state);
		partions.add(dfa.end_state);
		partions.add(non_final);
		DFA minimizedDfa = DfaMinimization(dfa, partions);
		compteurDFA=0;
		minimizedDfa.buildNumbers(allStates);
		
		// Minimized DFA, Research in the text
		ids = new ArrayList<>();
		ids = searchText(minimizedDfa, DaarApplication.keywords);
		return ids;
	}
	
	

	// FROM REGEX TO SYNTAX TREE
	private static RegExTree parse() throws Exception {
		// BEGIN DEBUG: set conditionnal to true for debug example
		if (false)
			throw new Exception();
		RegExTree example = exampleAhoUllman();
		if (false)
			return example;
		// END DEBUG

		ArrayList<RegExTree> result = new ArrayList<RegExTree>();
		for (int i = 0; i < regEx.length(); i++)
			result.add(new RegExTree(charToRoot(regEx.charAt(i)), new ArrayList<RegExTree>()));

		return parse(result);
	}

	private static int charToRoot(char c) {
		if (c == '.')
			return ProjectEgrep.DOT;
		if (c == '*')
			return ProjectEgrep.ETOILE;
		if (c == '|')
			return ProjectEgrep.ALTERN;
		if (c == '+')
			return ProjectEgrep.PLUS;
		if (c == '(')
			return ProjectEgrep.PARENTHESEOUVRANT;
		if (c == ')')
			return ProjectEgrep.PARENTHESEFERMANT;
		return (int) c;
	}

	private static RegExTree parse(ArrayList<RegExTree> result) throws Exception {
		while (containParenthese(result))
			result = processParenthese(result);
		while (containEtoile(result))
			result = processEtoile(result);
		while (containPlus(result))
			result = processPlus(result);
		while (containConcat(result))
			result = processConcat(result);
		while (containAltern(result))
			result = processAltern(result);

		if (result.size() > 1)
			throw new Exception();

		return removeProtection(result.get(0));
	}

	private static boolean containParenthese(ArrayList<RegExTree> trees) {
		for (RegExTree t : trees)
			if (t.root == PARENTHESEFERMANT || t.root == PARENTHESEOUVRANT)
				return true;
		return false;
	}

	private static ArrayList<RegExTree> processParenthese(ArrayList<RegExTree> trees) throws Exception {
		ArrayList<RegExTree> result = new ArrayList<RegExTree>();
		boolean found = false;
		for (RegExTree t : trees) {
			if (!found && t.root == PARENTHESEFERMANT) {
				boolean done = false;
				ArrayList<RegExTree> content = new ArrayList<RegExTree>();
				while (!done && !result.isEmpty())
					if (result.get(result.size() - 1).root == PARENTHESEOUVRANT) {
						done = true;
						result.remove(result.size() - 1);
					} else
						content.add(0, result.remove(result.size() - 1));
				if (!done)
					throw new Exception();
				found = true;
				ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
				subTrees.add(parse(content));
				result.add(new RegExTree(PROTECTION, subTrees));
			} else {
				result.add(t);
			}
		}
		if (!found)
			throw new Exception();
		return result;
	}

	private static boolean containEtoile(ArrayList<RegExTree> trees) {
		for (RegExTree t : trees)
			if (t.root == ETOILE && t.subTrees.isEmpty())
				return true;
		return false;
	}

	private static ArrayList<RegExTree> processEtoile(ArrayList<RegExTree> trees) throws Exception {
		ArrayList<RegExTree> result = new ArrayList<RegExTree>();
		boolean found = false;
		for (RegExTree t : trees) {
			if (!found && t.root == ETOILE && t.subTrees.isEmpty()) {
				if (result.isEmpty())
					throw new Exception();
				found = true;
				RegExTree last = result.remove(result.size() - 1);
				ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
				subTrees.add(last);
				result.add(new RegExTree(ETOILE, subTrees));
			} else {
				result.add(t);
			}
		}
		return result;
	}
	
	private static boolean containPlus(ArrayList<RegExTree> trees) {
		for (RegExTree t : trees)
			if (t.root == PLUS && t.subTrees.isEmpty())
				return true;
		return false;
	}

	private static ArrayList<RegExTree> processPlus(ArrayList<RegExTree> trees) throws Exception {
		ArrayList<RegExTree> result = new ArrayList<RegExTree>();
		boolean found = false;
		for (RegExTree t : trees) {
			if (!found && t.root == PLUS && t.subTrees.isEmpty()) {
				if (result.isEmpty())
					throw new Exception();
				found = true;
				RegExTree last = result.remove(result.size() - 1);
				ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
				subTrees.add(last);
				result.add(new RegExTree(PLUS, subTrees));
			} else {
				result.add(t);
			}
		}
		return result;
	}

	private static boolean containConcat(ArrayList<RegExTree> trees) {
		boolean firstFound = false;
		for (RegExTree t : trees) {
			if (!firstFound && t.root != ALTERN) {
				firstFound = true;
				continue;
			}
			if (firstFound)
				if (t.root != ALTERN)
					return true;
				else
					firstFound = false;
		}
		return false;
	}

	private static ArrayList<RegExTree> processConcat(ArrayList<RegExTree> trees) throws Exception {
		ArrayList<RegExTree> result = new ArrayList<RegExTree>();
		boolean found = false;
		boolean firstFound = false;
		for (RegExTree t : trees) {
			if (!found && !firstFound && t.root != ALTERN) {
				firstFound = true;
				result.add(t);
				continue;
			}
			if (!found && firstFound && t.root == ALTERN) {
				firstFound = false;
				result.add(t);
				continue;
			}
			if (!found && firstFound && t.root != ALTERN) {
				found = true;
				RegExTree last = result.remove(result.size() - 1);
				ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
				subTrees.add(last);
				subTrees.add(t);
				result.add(new RegExTree(CONCAT, subTrees));
			} else {
				result.add(t);
			}
		}
		return result;
	}

	private static boolean containAltern(ArrayList<RegExTree> trees) {
		for (RegExTree t : trees)
			if (t.root == ALTERN && t.subTrees.isEmpty())
				return true;
		return false;
	}

	private static ArrayList<RegExTree> processAltern(ArrayList<RegExTree> trees) throws Exception {
		ArrayList<RegExTree> result = new ArrayList<RegExTree>();
		boolean found = false;
		RegExTree gauche = null;
		boolean done = false;
		for (RegExTree t : trees) {
			if (!found && t.root == ALTERN && t.subTrees.isEmpty()) {
				if (result.isEmpty())
					throw new Exception();
				found = true;
				gauche = result.remove(result.size() - 1);
				continue;
			}
			if (found && !done) {
				if (gauche == null)
					throw new Exception();
				done = true;
				ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
				subTrees.add(gauche);
				subTrees.add(t);
				result.add(new RegExTree(ALTERN, subTrees));
			} else {
				result.add(t);
			}
		}
		return result;
	}

	private static RegExTree removeProtection(RegExTree tree) throws Exception {
		if (tree.root == PROTECTION && tree.subTrees.size() != 1)
			throw new Exception();
		if (tree.subTrees.isEmpty())
			return tree;
		if (tree.root == PROTECTION)
			return removeProtection(tree.subTrees.get(0));

		ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
		for (RegExTree t : tree.subTrees)
			subTrees.add(removeProtection(t));
		return new RegExTree(tree.root, subTrees);
	}

	// EXAMPLE
	// --> RegEx from Aho-Ullman book Chap.10 Example 10.25
	private static RegExTree exampleAhoUllman() {
		RegExTree a = new RegExTree((int) 'a', new ArrayList<RegExTree>());
		RegExTree b = new RegExTree((int) 'b', new ArrayList<RegExTree>());
		RegExTree c = new RegExTree((int) 'c', new ArrayList<RegExTree>());
		ArrayList<RegExTree> subTrees = new ArrayList<RegExTree>();
		subTrees.add(c);
		RegExTree cEtoile = new RegExTree(ETOILE, subTrees);
		subTrees = new ArrayList<RegExTree>();
		subTrees.add(b);
		subTrees.add(cEtoile);
		RegExTree dotBCEtoile = new RegExTree(CONCAT, subTrees);
		subTrees = new ArrayList<RegExTree>();
		subTrees.add(a);
		subTrees.add(dotBCEtoile);
		return new RegExTree(ALTERN, subTrees);
	}
	
	protected static String valueToString(int value) {
		if (value == ProjectEgrep.CONCAT)
			return ".";
		if (value == ProjectEgrep.ETOILE)
			return "*";
		if (value == ProjectEgrep.ALTERN)
			return "|";
		if (value == ProjectEgrep.DOT)
			return ".";
		if (value == 949)
			return "(e)";
		if (value == ProjectEgrep.PLUS)
			return "+";
		return Character.toString((char) value);
	}
	
	private static NFA toAutomaton(RegExTree reg) {
		
		if(reg.root == ALTERN) {
			return union(toAutomaton(reg.subTrees.get(0)),toAutomaton(reg.subTrees.get(1)));
		}else if (reg.root == CONCAT){
			return concat(toAutomaton(reg.subTrees.get(0)),toAutomaton(reg.subTrees.get(1)));
		}else if (reg.root == ETOILE){
			return etoile(toAutomaton(reg.subTrees.get(0)));
		}else if (reg.root == PLUS){
			return plus(toAutomaton(reg.subTrees.get(0)));
		}
		
		if(!alphabet.contains(reg.root)){
			alphabet.add(reg.root);
		}
		
		int start_state = compteur;
		compteur++;
		int end_state = compteur;
		compteur++;
		HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> arcs = new HashMap<Integer,HashMap<Integer,ArrayList<Integer>>>();
		ArrayList<Integer> a = new ArrayList<Integer>();
		a.add(end_state);
		HashMap<Integer,ArrayList<Integer>> b = new HashMap<Integer,ArrayList<Integer>>();
		b.put(reg.root, a);
		arcs.put(start_state, b);
		return new NFA(start_state, end_state, arcs);
	}
	
	private static NFA union(NFA A1, NFA A2){
		A1.arcs.putAll(A2.arcs);
		int start_state = compteur;
		compteur++;
		int end_state = compteur;
		compteur++;
		
		ArrayList<Integer> a = new ArrayList<Integer>();
		a.add(A1.start_state);
		a.add(A2.start_state);
		HashMap<Integer,ArrayList<Integer>> b = new HashMap<Integer,ArrayList<Integer>>();
		b.put(949, a);
		A1.arcs.put(start_state, b);
		
		a = new ArrayList<Integer>();
		a.add(end_state);
		b = new HashMap<Integer,ArrayList<Integer>>();
		b.put(949, a);
		A1.arcs.put(A1.end_state,b);
		
		a = new ArrayList<Integer>();
		a.add(end_state);
		b = new HashMap<Integer,ArrayList<Integer>>();
		b.put(949, a);
		A1.arcs.put(A2.end_state,b);
		return new NFA(start_state, end_state, A1.arcs);
	}
	
	private static NFA concat(NFA A1, NFA A2){
		A1.arcs.putAll(A2.arcs);
		ArrayList<Integer> a = new ArrayList<Integer>();
		a.add(A2.start_state);
		HashMap<Integer,ArrayList<Integer>> b = new HashMap<Integer,ArrayList<Integer>>();
		b.put(949, a);
		A1.arcs.put(A1.end_state, b);
		return new NFA(A1.start_state, A2.end_state, A1.arcs);
	}
	
	private static NFA etoile(NFA A){
		int start_state = compteur;
		compteur++;
		int end_state = compteur;
		compteur++;
		
		ArrayList<Integer> a = new ArrayList<Integer>();
		a.add(A.start_state);
		a.add(end_state);
		HashMap<Integer,ArrayList<Integer>> b = new HashMap<Integer,ArrayList<Integer>>();
		b.put(949, a);
		A.arcs.put(start_state, b);
		
		a = new ArrayList<Integer>();
		a.add(A.start_state);
		a.add(end_state);
		b = new HashMap<Integer,ArrayList<Integer>>();
		b.put(949, a);
		A.arcs.put(A.end_state,b);
		return new NFA(start_state, end_state, A.arcs);
	}
	
	private static NFA plus(NFA A){
		int start_state = compteur;
		compteur++;
		int end_state = compteur;
		compteur++;
		
		ArrayList<Integer> a = new ArrayList<Integer>();
		a.add(A.start_state);
		//a.add(end_state);
		HashMap<Integer,ArrayList<Integer>> b = new HashMap<Integer,ArrayList<Integer>>();
		b.put(949, a);
		A.arcs.put(start_state, b);
		
		a = new ArrayList<Integer>();
		a.add(A.start_state);
		a.add(end_state);
		b = new HashMap<Integer,ArrayList<Integer>>();
		b.put(949, a);
		A.arcs.put(A.end_state,b);
		return new NFA(start_state, end_state, A.arcs);
	}
	
	/**
	 * Increase the size of the current state with the epsilon moves
	 * @param state, the state we are increasing
	 * @param A, the NFA
	 */
	private static DFAState increaseCurrentState (DFAState state, NFA A){
		DFAState result = new DFAState();
		
		for(Integer y  : state.getValues() ){
		
			result.getValues().add(y);
			if(A.arcs.get(y) != null){
				ArrayList<Integer> s = A.arcs.get(y).get(949);
				if(s != null) {
					result.getValues().addAll(increaseCurrentState(new DFAState(s), A).getValues());
				}
			}
		}
		
		return result;
	}
	
	private static DFA toDFA(NFA A) {
		
		//initialisation des variables
		DFAState start_state = new DFAState();
		ArrayList<DFAState> end_state = new ArrayList<DFAState>();
		HashMap <DFAState, HashMap<Integer,DFAState>> arcs = new HashMap<DFAState,HashMap<Integer,DFAState>>();
		
		//calcul de l'etat initiale
		DFAState list = new DFAState();
		list.getValues().add(A.start_state);
		start_state.getValues().addAll(increaseCurrentState(list, A).getValues());
		
		allStates.add(start_state);
		ArrayList<DFAState> s = new ArrayList<DFAState>();
		s.add(start_state);
		
		//parcourir chaque etat en commencant par l'état initiale pour trouver de nouveau états
		while(!s.isEmpty()){
			for(Integer character : alphabet){
				DFAState newState = new DFAState();
				for( Integer x : s.get(0).getValues()){
					if(A.arcs.get(x) != null){
						if(A.arcs.get(x).containsKey(character)){
							newState.getValues().addAll(increaseCurrentState(new DFAState(A.arcs.get(x).get(character)), A).getValues());
						}
					}
				}
				if (!newState.getValues().isEmpty()){
					//si l'état obtenu est nouveau l'ajouté dans l'ensemble states  et dans s pour le parcourir
					if(!allStates.contains(newState)){
						s.add(newState);
						allStates.add(newState);
					}
					//ajouter l'arc ( current -> (caractére -> newState) ) au dfa
					if(!arcs.containsKey(s.get(0))){
						arcs.put(s.get(0), new HashMap<Integer,DFAState>());
					}				
					arcs.get(s.get(0)).put(character, newState);
					//marquer comme end state du dfa si il contient le end state du nfa
					if(newState.getValues().contains(A.end_state) && !end_state.contains(newState)){
						end_state.add(newState);
					}
				}
			}
			//remove current state that had been iterated through
			s.remove(0);
			//System.out.println(states.size());
		}
			
		return new DFA(start_state,end_state, arcs);
	}
	
	private static boolean areStatesDistinguishable( DFA A, ArrayList<ArrayList<DFAState>> partions, DFAState state1, DFAState state2) {
    	if(A.arcs.get(state1) == null || A.arcs.get(state2) == null){
    		return !(A.arcs.get(state1) == A.arcs.get(state2));
    	}
    	
    	Set<Integer> set = new HashSet<Integer>();
		set.addAll(A.arcs.get(state1).keySet());
		set.addAll(A.arcs.get(state2).keySet());
	     
        for (Integer e : set) {
            if (!containedBySamePartion(partions, A.arcs.get(state1).get(e) , A.arcs.get(state2).get(e))) {
                return true;
            }
        }
        
        return false;
    }
    
    private static boolean containedBySamePartion(ArrayList<ArrayList<DFAState>> partions, DFAState s1, DFAState s2) {
    	if(s1 == null || s2 == null){
    		return s1 == s2;
    	}
        for (final ArrayList<DFAState> partion : partions) {
            if (partion.contains(s1) && partion.contains(s2)) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean containsState( ArrayList<ArrayList<DFAState>> partions, DFAState state) {
    	
        for (ArrayList<DFAState> partion : partions) {
            if (partion.contains((DFAState)state)) {
                return true;
            }
        }
        return false;
    }
    
    /**
	 * Replace references of a state with the reference of a new state 
	 * @param arcs, the transitions 
	 * @param state, state that need to be replaced
	 * @param new_state, the resulted state from merging the states of a partion
	 * @return arcs with updated references to the new state
	 */
    private static HashMap <DFAState, HashMap<Integer, DFAState>> replace_occurancies(HashMap<DFAState, HashMap<Integer, DFAState>> arcs, DFAState state,
			DFAState new_state) {
    	
    	arcs.remove(state);
		allStates.remove(state);

    	for(Map.Entry<DFAState,HashMap<Integer,DFAState>> e1 : arcs.entrySet()){
    		HashMap<Integer, DFAState>  x = e1.getValue();		
    		for(Map.Entry<Integer, DFAState> e2 : x.entrySet()){
    			e2.getValue(); e2.getKey();
    			if(e2.getValue().equals(state)){
    				x.put(e2.getKey(), new_state);
    				arcs.put(e1.getKey(), x);
    			}
    		}
    	}
    	
    	return arcs;
    }
    
    /**
	 * Merging the DFA states contained in the same partion.
	 * @param A, the DFA 
	 * @param partions, each partion contain a group of states. 
	 * @return DFA with merged states
	 */
    private static DFA merge_states(DFA A, ArrayList<ArrayList<DFAState>> partions) {
        
    	DFAState start_state = A.start_state;
        ArrayList<DFAState> end_state = new ArrayList<DFAState>();
    	HashMap <DFAState, HashMap<Integer, DFAState>> arcs = new HashMap <DFAState, HashMap<Integer, DFAState>>(A.arcs);
    	
    	for(ArrayList<DFAState> partion : partions){
    		if (partion.size()>1){
    			DFAState new_state = new DFAState();
    			HashMap<Integer, DFAState> a = new HashMap<Integer, DFAState>();
    			arcs.put(new_state, a);
    			allStates.add(new_state);
    			for(DFAState state : partion){
    				for(Integer c : alphabet){
    					if(arcs.get(state)!=null){
	        				if(arcs.get(state).get(c) != null){
	        					if(a.get(c) == null){
		        					a.put(c, new DFAState());
	        					}
	        					DFAState b = a.get(c);
	        					
	        					//addAll without duplicates
		    					Set<Integer> joinedSet = new HashSet<Integer>();
		    					joinedSet.addAll(arcs.get(state).get(c).getValues());
		    					joinedSet.addAll(b.getValues());
		    					b.getValues().clear();
		    					b.getValues().addAll(joinedSet);
		    					
	        					}
    						}
    					}
    				
    					//addAll without duplicates
    					Set<Integer> joinedSet = new HashSet<Integer>();
    					joinedSet.addAll(state.getValues());
    					joinedSet.addAll(new_state.getValues());
    					new_state.getValues().clear();
    					new_state.getValues().addAll(joinedSet);
    					
						if(A.end_state.contains(state) && !end_state.contains(new_state)){
							end_state.add(new_state);
						}

				    arcs = replace_occurancies(arcs, state, new_state);					
				}
    		}
    		else{
    			if(A.end_state.contains(partion.get(0))){ 
					end_state.add(partion.get(0));
				}
    		}
    	}
    	
    	return new DFA(start_state, end_state, arcs);
    }
    
	/**
	 * Minimization of a given DFA
	 * @param A, the DFA 
	 * @param partions, each partion contain a group of states. 
	 * @return the minimized DFA
	 */
	private static DFA DfaMinimization(DFA A, ArrayList<ArrayList<DFAState>> partions){
		
		//p(k+1)
		ArrayList<ArrayList<DFAState>> result = new ArrayList<ArrayList<DFAState>>();
				
		for(ArrayList<DFAState> partion : partions){
			if(partion.size()>1){
				for(int i = 0; i < partion.size() ; i++){
					if (!containsState(result, partion.get(i))) {
						ArrayList<DFAState> p1 = new ArrayList<DFAState>();
						p1.add(partion.get(i));
						for(int j = i+1; j < partion.size() ; j++){
							if(!areStatesDistinguishable(A, partions, partion.get(i), partion.get(j))){
								p1.add(partion.get(j));
							}
						}
						result.add(p1);
					}
				}
			}else{
				result.add(partion);
			}
		}
		
		//stop if no new partion found
		if(partions.size() == result.size() ){
			//System.out.println("nombres de partions : " + partions.size());
			return merge_states(A, result);
		}
		
		return DfaMinimization(A,result);
	}
	
	/**
	 * Searches lines in a text using a DFA
	 * @param A, the DFA we're using
	 * @param fileName, the name of the file we're searching (should be in current directory)
	 * @return the lines matching the DFA
	 */
	private static List<Integer> searchText(DFA A, HashMap<String, List<Integer>> keywords) {
		List<Integer> ids = new ArrayList<>();

		for (String keyword : keywords.keySet()) {
			DFAState current = null;
			for (int i = 0; i < keyword.length(); i++) {
				current = A.start_state;
				boolean foundWord = false;
				for (int j = i; j < keyword.length(); j++) {
					// if the current state is null, that means that the
					// current word doesn't match the DFA so we quit the loop
					if (A.arcs.get(current) != null) {
						current = A.arcs.get(current).get((int) keyword.charAt(j));
						current = getTrueState(A, current);
						// if the current state is in the end_state array, that means that the
						// current word does match the DFA, so we add the line to the list
						if (current != null && A.end_state.contains(current)) {
							ids.addAll(keywords.get(keyword));
							foundWord = true;
							break;
						}
					} else {
						break;
					}
				}
				if (foundWord)
					break;
			}
		}

		return ids;
	}
	
	/**
	 * Gets a state directly from the arc list
	 * (Sometimes the contains() method ignores a state even though
	 * they are identical, this fixes it)
	 * @param A, the DFA containing the arcs
	 * @param state, the state we're looking for
	 * @return, the real DFAState
	 */
	private static DFAState getTrueState(DFA A, DFAState state) {
		if (state != null) 
		{
			for (DFAState s : A.arcs.keySet()) {
				if (state.equals(s)) 
					state = s;
			}
		}
		return state;
	}
	
	/**
	 * KMP algorithm, searches books with keywords following a pattern
	 */
	private static ArrayList<Integer> kmp(String pattern, HashMap<String, List<Integer>> keywords) {
		ArrayList<Integer> ids = new ArrayList<>();

		int carryOver[] = new int[pattern.length()];

		buildCarryOver(pattern, carryOver);

		for (String keyword : keywords.keySet()) {
			int i = 0;
			int compteur = 0;
			while (i < keyword.length()) {
				if (pattern.charAt(compteur) == keyword.charAt(i)) {
					i++;
					compteur++;
				}
				if (compteur == pattern.length()) {
					// found matching word
					ids.addAll(keywords.get(keyword));
					break;
					// compteur = carryOver[compteur - 1];
				} else if (i < keyword.length() && pattern.charAt(compteur) != keyword.charAt(i)) {
					if (compteur != 0) {
						compteur = carryOver[compteur - 1];
					} else {
						i++;
					}
				}
			}
		}

		return ids;

	}
	
	private static void buildCarryOver(String pattern, int[] carryOver) {
		
		int longestPrefixSuffixLength = 0;
		carryOver[0] = 0;
		int i = 1;
		
		while (i < pattern.length()) 
		{
			if(pattern.charAt(i) == pattern.charAt(longestPrefixSuffixLength))
			{
				longestPrefixSuffixLength++;
				carryOver[i] = longestPrefixSuffixLength;
				i++;
			}
			else {
				if(longestPrefixSuffixLength != 0) {
					longestPrefixSuffixLength = carryOver[longestPrefixSuffixLength - 1];
				} else {
					carryOver[i] = longestPrefixSuffixLength;
					i++;
				}
			}
		}
	}
	
	private static boolean isConcatenated(String str) {
		boolean isConcatenated = true;
		for (int i = 0; i < str.length(); i++) {
			if(str.charAt(i) == '*' || str.charAt(i) == '+'
					|| str.charAt(i) == '|') {
				isConcatenated = false;
				break;
			}
		}
		return isConcatenated;
	}
}

