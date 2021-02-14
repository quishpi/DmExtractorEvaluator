package edu.upc.evaluator;

import java.util.ArrayList;

public class GoldDecision extends GoldElement {

	private ArrayList<GoldRequirement> requirementList;
	private GoldDecisionTable decisionTable;

	public GoldDecision(String id, String name, ArrayList<GoldRequirement> requirementList,
			GoldDecisionTable decisionTable) {
		super(id, name);
		this.requirementList = requirementList;
		this.decisionTable = decisionTable;
	}

	public ArrayList<GoldRequirement> getRequirementList() {
		return requirementList;
	}

	public void setRequirementList(ArrayList<GoldRequirement> requirementList) {
		this.requirementList = requirementList;
	}

	public GoldDecisionTable getDecisionTable() {
		return decisionTable;
	}

	public void setDecisionTable(GoldDecisionTable decisionTable) {
		this.decisionTable = decisionTable;
	}

}
