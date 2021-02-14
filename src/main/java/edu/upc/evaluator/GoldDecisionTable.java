package edu.upc.evaluator;

import java.util.ArrayList;

public class GoldDecisionTable {

	private String id;
	private ArrayList<GoldRule> ruleList;

	public GoldDecisionTable(String id, ArrayList<GoldRule> ruleList) {
		super();
		this.id = id;
		this.ruleList = ruleList;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ArrayList<GoldRule> getRuleList() {
		return ruleList;
	}

	public void setRuleList(ArrayList<GoldRule> ruleList) {
		this.ruleList = ruleList;
	}

	@Override
	public String toString() {
		return "GoldDecisionTable [id=" + id + ", ruleList=" + ruleList + "]";
	}

}
