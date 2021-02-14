package edu.upc.evaluator;

import java.util.ArrayList;

public class GoldRule {

	private String id;
	private ArrayList<GoldInputEntry> inputEntryList;
	private GoldOutputEntry outputEntry;

	public GoldRule(String id, ArrayList<GoldInputEntry> inputEntryList, GoldOutputEntry outputEntry) {
		super();
		this.id = id;
		this.inputEntryList = inputEntryList;
		this.outputEntry = outputEntry;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ArrayList<GoldInputEntry> getInputEntryList() {
		return inputEntryList;
	}

	public void setInputEntryList(ArrayList<GoldInputEntry> inputEntryList) {
		this.inputEntryList = inputEntryList;
	}

	public GoldOutputEntry getOutputEntry() {
		return outputEntry;
	}

	public void setOutputEntry(GoldOutputEntry outputEntry) {
		this.outputEntry = outputEntry;
	}

	@Override
	public String toString() {
		return "GoldRule [id=" + id + ", inputEntryList=" + inputEntryList + ", outputEntry=" + outputEntry + "]";
	}

}
