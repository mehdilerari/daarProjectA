package com.sorbonne.daar.utils.keywords;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class MotCleMap implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1667849664615407919L;

	/** Map, keys are the stem of the keywords, values are the id of the books */
	private HashMap<String, List<Integer>> motCleMap;

	public MotCleMap() {
		this.motCleMap = new HashMap<>();
	}

	public HashMap<String, List<Integer>> getMotCleMap() {
		return motCleMap;
	}

	public void setMotCleMap(HashMap<String, List<Integer>> motCleMap) {
		this.motCleMap = motCleMap;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String stem : motCleMap.keySet()) {
			sb.append(stem + " -> " + motCleMap.get(stem).size());
			//sb.append(stem + " -> ");
			/*for (Integer id : motCleMap.get(stem)) {
				sb.append(" " + id);
			}*/
			sb.append("\n");
		}
		return sb.toString();
	}

}
