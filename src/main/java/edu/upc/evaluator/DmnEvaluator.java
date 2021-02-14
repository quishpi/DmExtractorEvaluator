package edu.upc.evaluator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.upc.DmParser;
import edu.upc.entities.dm.DecisionTable_Dmn;
import edu.upc.entities.dm.Decision_Dmn;
import edu.upc.entities.dm.InputData_Dmn;
import edu.upc.entities.dm.InputEntry_Rule;
import edu.upc.entities.dm.OutputEntry_Rule;
import edu.upc.entities.dm.Requirement;
import edu.upc.entities.dm.Rule_Table;
import edu.upc.freelingutils.ActionType;
import edu.upc.freelingutils.dm.DmnFoldersUrl;
import edu.upc.freelingutils.dm.DmnFreelingUtils;
import edu.upc.freelingutils.dm.TypeRef_Table;

public class DmnEvaluator {

	private DocumentBuilderFactory factory;
	private DocumentBuilder builder;
	private File goldXmlFile;
	// Decision | InputData | Requirement | Total
	private Integer[] goldRequirementResult = { 0, 0, 0, 0 };
	private Integer[] extractedRequirementResul = { 0, 0, 0, 0 };
	private Integer[] coincidenceRequirementResult = { 0, 0, 0, 0 };
	private Double[] scoreRequirementResult = { 0.0, 0.0, 0.0 };

	// InputEntry | OutPutEntry | Total
	private Integer[] goldLogicResult = { 0, 0, 0, 0 };
	private Integer[] extractedLogicResul = { 0, 0, 0, 0 };
	private Integer[] coincidenceLogicResult = { 0, 0, 0, 0 };
	private Double[] scoreLogicResult = { 0.0, 0.0, 0.0 };

	private DmParser parser;

	private LinkedHashMap<String, Requirement> requirementList;
	private LinkedHashMap<String, Decision_Dmn> decisionList;
	private LinkedHashMap<String, InputData_Dmn> inputDataList;
	private LinkedHashMap<String, DecisionTable_Dmn> decisionTableList;

	private LinkedHashMap<String, GoldDecision> goldDecisionList = new LinkedHashMap<String, GoldDecision>();
	private LinkedHashMap<String, GoldInputData> goldInputDataList = new LinkedHashMap<String, GoldInputData>();
	private String justNameOfFile;

	public DmnEvaluator(String nlpTextFilePath, String lang)
			throws ParserConfigurationException, ParseException, IOException {
		factory = DocumentBuilderFactory.newInstance();
		builder = factory.newDocumentBuilder();
		parser = new DmParser(DmnFreelingUtils.readFile(nlpTextFilePath), "all", lang);
		this.decisionList = parser.getDecisionList();
		this.requirementList = parser.getRequirementList();
		this.inputDataList = parser.getInputDataList();
		this.decisionTableList = parser.getDecisionTableList();

		justNameOfFile = DmnFreelingUtils.getFileNameWithoutExtension(new File(nlpTextFilePath));
		goldXmlFile = new File(DmnFoldersUrl.INPUT_FOLDER + "/dmn/" + justNameOfFile + ".dmn");

	}

	public String getRequirimentCsvRow() throws IOException {
		String csvRow = "";
		csvRow += justNameOfFile + "\t";
		csvRow += goldRequirementResult[0] + "\t" + goldRequirementResult[1] + "\t" + goldRequirementResult[2] + "\t"
				+ goldRequirementResult[3] + "\t";
		csvRow += extractedRequirementResul[0] + "\t" + extractedRequirementResul[1] + "\t"
				+ extractedRequirementResul[2] + "\t" + extractedRequirementResul[3] + "\t";
		csvRow += coincidenceRequirementResult[0] + "\t" + coincidenceRequirementResult[1] + "\t"
				+ coincidenceRequirementResult[2] + "\t" + coincidenceRequirementResult[3] + "\t";
		csvRow += scoreRequirementResult[0] + "\t" + scoreRequirementResult[1] + "\t" + scoreRequirementResult[2]
				+ "\n";
		return csvRow;
	}

	public String getLogicCsvRow() throws IOException {
		String csvRow = "";
		csvRow += justNameOfFile + "\t";
		csvRow += goldLogicResult[0] + "\t" + goldLogicResult[1] + "\t" + goldLogicResult[2] + "\t" + goldLogicResult[3]
				+ "\t";
		csvRow += extractedLogicResul[0] + "\t" + extractedLogicResul[1] + "\t" + extractedLogicResul[2] + "\t"
				+ extractedLogicResul[3] + "\t";
		csvRow += coincidenceLogicResult[0] + "\t" + coincidenceLogicResult[1] + "\t" + coincidenceLogicResult[2] + "\t"
				+ coincidenceLogicResult[3] + "\t";
		csvRow += scoreLogicResult[0] + "\t" + scoreLogicResult[1] + "\t" + scoreLogicResult[2] + "\n";
		return csvRow;
	}

	public void LogicScore() throws SAXException, IOException {
		goldDecisionList.clear();
		System.out.println("---- GOLD INPUTENTRY & OUTPUTENTRY & RULE--------");
		goldDecisionList = getDecisionListFromDefinition();
		ArrayList<GoldInputEntry> goldInputEntryListTmp = new ArrayList<GoldInputEntry>();
		ArrayList<GoldOutputEntry> goldOutputEntryListTmp = new ArrayList<GoldOutputEntry>();
		for (Entry<String, GoldDecision> goldDecision : goldDecisionList.entrySet()) {
			if (goldDecision.getValue().getDecisionTable() != null) {
				ArrayList<GoldRule> goldRuleList = goldDecision.getValue().getDecisionTable().getRuleList();
				goldLogicResult[2] += goldRuleList.size();
				for (GoldRule goldRule : goldRuleList) {
					ArrayList<GoldInputEntry> goldInputEntryList = goldRule.getInputEntryList();
					for (GoldInputEntry goldInputEntry : goldInputEntryList) {
						String goldInputEntryName = goldInputEntry.getName();
						if (goldInputEntryName != null && !goldInputEntryName.isEmpty()) {
							goldInputEntryListTmp
									.add(new GoldInputEntry(goldInputEntry.getId(), goldInputEntry.getName()));
						}
					}
					String goldOutputName = goldRule.getOutputEntry().getName();
					if (goldOutputName != null && !goldOutputName.isEmpty()) {
						goldOutputEntryListTmp.add(new GoldOutputEntry(goldRule.getOutputEntry().getId(),
								goldRule.getOutputEntry().getName()));
						goldLogicResult[1]++;
					}
				}
			}
		}
		goldLogicResult[0] = goldInputEntryListTmp.size();

		System.out.println("---- EXTRACTED INPUTENTRY & OUTPUTENTRY & RULE--------");
		ArrayList<InputEntry_Rule> inputEntryListTmp = new ArrayList<InputEntry_Rule>();
		ArrayList<OutputEntry_Rule> outputEntryListTmp = new ArrayList<OutputEntry_Rule>();
		for (Entry<String, DecisionTable_Dmn> decisionTable : decisionTableList.entrySet()) {
			if (decisionTable.getValue().getRules() != null) {
				extractedLogicResul[2] += decisionTable.getValue().getRules().size();
				for (Rule_Table rule : decisionTable.getValue().getRules()) {
					for (Entry<String, InputEntry_Rule> inputEntry : rule.getInputEntries().entrySet()) {
						String inputEntryName = inputEntry.getValue().getText();
						if (inputEntryName != null && !inputEntryName.isEmpty()) {
							if (inputEntry.getValue().getNegation() && inputEntry.getValue().getInput() != null) {
								if (inputEntry.getValue().getInput().getTypeRef().equals(TypeRef_Table.BOOLEAN)) {
									if (inputEntryName.equals("true"))
										inputEntryName = "false";
									else
										inputEntryName = "true";
								}
							}
							inputEntry.getValue().setText(inputEntryName);
							inputEntryListTmp
									.add(new InputEntry_Rule(inputEntry.getKey(), inputEntry.getValue().getText(),
											inputEntry.getValue().getInput(), inputEntry.getValue().getNegation()));
						}
					}
					String outputEntryName = rule.getOutputEntry().getText();
					if (outputEntryName != null && !outputEntryName.isEmpty()) {
						if (rule.getOutputEntry().getNegation()) {
							if (decisionTable.getValue().getOutput().getTypeRef().equals(TypeRef_Table.BOOLEAN)) {
								if (outputEntryName.equals("true"))
									outputEntryName = "false";
								else
									outputEntryName = "true";
							}
						}
						rule.getOutputEntry().setText(outputEntryName);
						outputEntryListTmp.add(new OutputEntry_Rule(rule.getOutputEntry().getId(),
								rule.getOutputEntry().getText(), rule.getOutputEntry().getNegation()));
						extractedLogicResul[1]++;
					}
				}
			}
		}
		extractedLogicResul[0] = inputEntryListTmp.size();

		System.out.println("---- COINCIDENCE INPUTENTRY --------");
		for (int i = 0; i < goldInputEntryListTmp.size(); i++) {
			String goldInputEntryName = goldInputEntryListTmp.get(i).getName().toLowerCase().trim();
			for (int j = 0; j < inputEntryListTmp.size(); j++) {
				String inputEntryName = inputEntryListTmp.get(j).getText().toLowerCase().trim();
				if (inputEntryName != null && !inputEntryName.isEmpty()) {
					if (goldInputEntryName.contains(inputEntryName) || inputEntryName.contains(goldInputEntryName)) {
						coincidenceLogicResult[0]++;
						inputEntryListTmp.get(j).setText("");
						System.out.println("> " + goldInputEntryName + " >> " + inputEntryName);
						break;
					}
				}
			}
		}

		System.out.println("---- COINCIDENCE OUTPUTENTRY --------");
		for (int i = 0; i < goldOutputEntryListTmp.size(); i++) {
			String goldOutputEntryName = goldOutputEntryListTmp.get(i).getName().toLowerCase().trim();
			for (int j = 0; j < outputEntryListTmp.size(); j++) {
				String outputEntryName = outputEntryListTmp.get(j).getText().toLowerCase().trim();
				if (outputEntryName != null && !outputEntryName.isEmpty()) {
					if (goldOutputEntryName.contains(outputEntryName)
							|| outputEntryName.contains(goldOutputEntryName)) {
						coincidenceLogicResult[1]++;
						outputEntryListTmp.get(j).setText("");
						System.out.println("> " + goldOutputEntryName + " >> " + outputEntryName);
						break;
					}
				}
			}
		}

		System.out.println("---- COINCIDENCE RULE --------");
		for (Entry<String, GoldDecision> goldDecision : goldDecisionList.entrySet()) {
			if (goldDecision.getValue().getDecisionTable() != null) {
				String goldDecisionName = goldDecision.getValue().getName().toLowerCase().trim();
				ArrayList<GoldRule> goldRuleList = goldDecision.getValue().getDecisionTable().getRuleList();
				for (GoldRule goldRule : goldRuleList) {
					ArrayList<GoldInputEntry> goldInputEntryList = goldRule.getInputEntryList();
					String goldOutputEntryText = goldRule.getOutputEntry().getName().toLowerCase().trim();
					Boolean found = false;
					for (Entry<String, DecisionTable_Dmn> decisionTable : decisionTableList.entrySet()) {
						String decisionName = decisionTable.getValue().getName().toLowerCase().trim();
						if (goldDecisionName.contains(decisionName) || decisionName.contains(goldDecisionName)) {
							if (decisionTable.getValue().getRules() != null) {
								for (Rule_Table rule : decisionTable.getValue().getRules()) {
									LinkedHashMap<String, InputEntry_Rule> innputEntryList = rule.getInputEntries();
									String outputEntryText = rule.getOutputEntry().getText().toLowerCase().trim();
									Integer count = 0;
									if (goldOutputEntryText.contains(outputEntryText)
											|| outputEntryText.contains(goldOutputEntryText)) {
										for (GoldInputEntry goldInputEntry : goldInputEntryList) {
											String goldInputEntryText = goldInputEntry.getName().toLowerCase().trim();
											for (Entry<String, InputEntry_Rule> inputEntry : innputEntryList
													.entrySet()) {
												String inputEntryText = inputEntry.getValue().getText().toLowerCase()
														.trim();
												if (goldInputEntryText.contains(inputEntryText)
														|| inputEntryText.contains(goldInputEntryText)) {
													count++;
													break;
												}
											}
										}
										//
										if (count == goldInputEntryList.size()) {
											found = true;
											coincidenceLogicResult[2]++;
											break;
										}
									}
								}
							}
							if (found)
								break;
						}
					}
				}
			}
		}

		// TOTAL
		goldLogicResult[3] = goldLogicResult[0] + goldLogicResult[1] + goldLogicResult[2];
		extractedLogicResul[3] = extractedLogicResul[0] + extractedLogicResul[1] + extractedLogicResul[2];
		coincidenceLogicResult[3] = coincidenceLogicResult[0] + coincidenceLogicResult[1] + coincidenceLogicResult[2];
		scoreLogicResult[0] = (coincidenceLogicResult[3] * 1.0 / extractedLogicResul[3]);
		scoreLogicResult[1] = (coincidenceLogicResult[3] * 1.0 / goldLogicResult[3]);
		scoreLogicResult[2] = 2.0
				* ((scoreLogicResult[0] * scoreLogicResult[1]) / (scoreLogicResult[0] + scoreLogicResult[1]));

		System.out.println("LOGIC:");
		System.out.println(
				"G-InputEntry\tG-OutputEntry\tG-Rule\tG-Total" + "\tE-InputEntry\tE-OutputEntry\tE-Rule+\tE-Total"
						+ "\tC-InputEntry\tC-OutputEntry\tC-Rule+\tC-Total");

		System.out.print(goldLogicResult[0] + "\t" + goldLogicResult[1] + "\t" + goldLogicResult[2] + "\t"
				+ goldLogicResult[3] + "\t");

		System.out.print(extractedLogicResul[0] + "\t" + extractedLogicResul[1] + "\t" + extractedLogicResul[2] + "\t"
				+ extractedLogicResul[3] + "\t");

		System.out.print(coincidenceLogicResult[0] + "\t" + coincidenceLogicResult[1] + "\t" + coincidenceLogicResult[2]
				+ "\t" + coincidenceLogicResult[3] + "\t");

		System.out.print(scoreLogicResult[0] + "\t" + scoreLogicResult[1] + "\t" + scoreLogicResult[2] + "\n");
	}

	public void RequirementScore() throws SAXException, IOException {

		// Element rootElement = document.getDocumentElement();
		goldDecisionList.clear();
		System.out.println("---- GOLD DECISIONS --------");
		goldDecisionList = getDecisionListFromDefinition();
		goldRequirementResult[0] = goldDecisionList.size();

		System.out.println("---- GOLD INPUTDATA --------");
		goldInputDataList = getInputDataListFromDefinition();
		goldRequirementResult[1] = goldInputDataList.size();

		System.out.println("---- GOLD REQUIREMENT --------");
		for (Entry<String, GoldDecision> goldDecision : goldDecisionList.entrySet()) {
			goldRequirementResult[2] = goldRequirementResult[2] + goldDecision.getValue().getRequirementList().size();
		}
		System.out.println("> " + goldRequirementResult[2]);

		System.out.println("---- EXTRACTED DECISIONS --------");
		extractedRequirementResul[0] = decisionList.size();
		for (Entry<String, Decision_Dmn> decision : decisionList.entrySet()) {
			System.out.println("> " + decision.getValue().getDrgElement().getName());
		}

		System.out.println("---- EXTRACTED INPUTDATA --------");
		ArrayList<InputData_Dmn> inputDataListTmp = new ArrayList<InputData_Dmn>();
		for (Entry<String, InputData_Dmn> inputData_Dmn : inputDataList.entrySet()) {
			if (inputData_Dmn.getValue().getDrgElement().getType().equals(ActionType.INPUTDATA)) {
				System.out.println("> " + inputData_Dmn.getValue().getDrgElement().getName());
				extractedRequirementResul[1]++;
				inputDataListTmp.add(inputData_Dmn.getValue());
			}
		}
		System.out.println("---- EXTRACTED REQUIREMENT --------");
		LinkedHashMap<String, ArrayList<String>> requirementListTmp = new LinkedHashMap<String, ArrayList<String>>();
		extractedRequirementResul[2] = requirementList.size();
		System.out.println("> " + extractedRequirementResul[2]);
		for (Entry<String, Decision_Dmn> decision : decisionList.entrySet()) {
			String decisionName = decision.getValue().getDrgElement().getName().toLowerCase().trim();
			ArrayList<String> inputDataStringList = new ArrayList<String>();
			for (Entry<String, Requirement> requirement : requirementList.entrySet()) {
				String decisionName2 = requirement.getValue().getDecision().getDrgElement().getName().toLowerCase()
						.trim();
				String inputDataName = requirement.getValue().getInputData().getDrgElement().getName().toLowerCase()
						.trim();
				if (decisionName.equals(decisionName2)) {
					inputDataStringList.add(inputDataName);
				}
			}
			requirementListTmp.put(decisionName, inputDataStringList);
		}

		System.out.println("---- CONCIDENCE DECISIONS --------");
		for (Entry<String, GoldDecision> goldDecision : goldDecisionList.entrySet()) {
			String goldDecisionName = goldDecision.getValue().getName().toLowerCase().trim();
			for (Entry<String, Decision_Dmn> decision : decisionList.entrySet()) {
				String extractedDecisionName = decision.getValue().getDrgElement().getName().toLowerCase().trim();
				if (goldDecisionName.contains(extractedDecisionName)
						|| extractedDecisionName.contains(goldDecisionName)) {
					coincidenceRequirementResult[0]++;
					System.out.println("> " + goldDecisionName + " = " + extractedDecisionName);
				}
			}
		}

		System.out.println("---- COINCIDENCE INPUTDATA --------");
		for (Entry<String, GoldInputData> goldInputData : goldInputDataList.entrySet()) {
			String goldInputDataName = goldInputData.getValue().getName().toLowerCase().trim();
			for (InputData_Dmn inputData_Dmn : inputDataListTmp) {
				String extractedInputDataName = inputData_Dmn.getDrgElement().getName().toLowerCase().trim();
				if (!inputData_Dmn.getDrgElement().getName().isEmpty()) {
					if (goldInputDataName.contains(extractedInputDataName)
							|| extractedInputDataName.contains(goldInputDataName)) {
						coincidenceRequirementResult[1]++;
						System.out.println("> " + goldInputDataName + " = " + extractedInputDataName);
						inputData_Dmn.getDrgElement().setName("");
						break;
					}
				}
			}
		}

		System.out.println("---- COINCIDENCE REQUIREMENT --------");
		for (Entry<String, GoldDecision> goldDecision : goldDecisionList.entrySet()) {
			String goldDecisionName = goldDecision.getValue().getName().toLowerCase().trim();
			for (int i = 0; i < goldDecision.getValue().getRequirementList().size(); i++) {
				String href = goldDecision.getValue().getRequirementList().get(i).getHref().replace("#", "");
				String goldInputDataName = null;
				GoldInputData goldInputData = goldInputDataList.get(href);
				if (goldInputData != null) {
					goldInputDataName = goldInputData.getName().toLowerCase().trim();
				} else {
					GoldDecision goldDecsionTmp = goldDecisionList.get(href);
					goldInputDataName = goldDecsionTmp.getName().toLowerCase().trim();
				}
				for (Entry<String, ArrayList<String>> requirement : requirementListTmp.entrySet()) {
					String decisionName = requirement.getKey();
					Boolean found = false;
					if (goldDecisionName.contains(decisionName) || decisionName.contains(goldDecisionName)) {
						for (int j = 0; j < requirement.getValue().size(); j++) {
							String inputDataName = requirement.getValue().get(j);
							if (!inputDataName.isEmpty()) {
								if ((goldInputDataName.contains(inputDataName)
										|| inputDataName.contains(goldInputDataName))) {
									System.out.println("> " + inputDataName + " >> " + decisionName);
									coincidenceRequirementResult[2]++;
									found = true;
									requirementListTmp.get(decisionName).set(j, "");// .replaceAll(requirement.getValue().get(j),
																					// "");
									break;
								}
							}
						}
						if (found)
							break;
					}
				}
			}
		}

		// TOTAL
		goldRequirementResult[3] = goldRequirementResult[0] + goldRequirementResult[1] + goldRequirementResult[2];
		extractedRequirementResul[3] = extractedRequirementResul[0] + extractedRequirementResul[1]
				+ extractedRequirementResul[2];
		coincidenceRequirementResult[3] = coincidenceRequirementResult[0] + coincidenceRequirementResult[1]
				+ coincidenceRequirementResult[2];
		scoreRequirementResult[0] = (coincidenceRequirementResult[3] * 1.0 / extractedRequirementResul[3]);
		scoreRequirementResult[1] = (coincidenceRequirementResult[3] * 1.0 / goldRequirementResult[3]);
		scoreRequirementResult[2] = 2.0 * ((scoreRequirementResult[0] * scoreRequirementResult[1])
				/ (scoreRequirementResult[0] + scoreRequirementResult[1]));

		System.out.println("REQUIREMENTS:");
		System.out.println(
				"G-Decision\tG-InputData\tG-Requirement\tG-Total" + "\tE-Decision\tE-InputData\tE-Requirement+\tE-Total"
						+ "\tC-Decision\tC-InputData\tC-Requirement+\tC-Total");

		System.out.print(goldRequirementResult[0] + "\t" + goldRequirementResult[1] + "\t" + goldRequirementResult[2]
				+ "\t" + goldRequirementResult[3] + "\t");

		System.out.print(extractedRequirementResul[0] + "\t" + extractedRequirementResul[1] + "\t"
				+ extractedRequirementResul[2] + "\t" + extractedRequirementResul[3] + "\t");

		System.out.print(coincidenceRequirementResult[0] + "\t" + coincidenceRequirementResult[1] + "\t"
				+ coincidenceRequirementResult[2] + "\t" + coincidenceRequirementResult[3] + "\t");

		System.out.print(
				scoreRequirementResult[0] + "\t" + scoreRequirementResult[1] + "\t" + scoreRequirementResult[2] + "\n");

	}

	private LinkedHashMap<String, GoldInputData> getInputDataListFromDefinition() throws SAXException, IOException {
		LinkedHashMap<String, GoldInputData> goldInputDataList = new LinkedHashMap<String, GoldInputData>();
		NodeList goldInputDataNodeList = getElementsFromDefinition(goldXmlFile, "inputData");
		System.out.println("> InputData:");
		for (int i = 0; i < goldInputDataNodeList.getLength(); i++) {
			Element goldInputDataElement = (Element) goldInputDataNodeList.item(i);
			String goldInputDataId = goldInputDataElement.getAttributes().getNamedItem("id").getNodeValue();
			String goldeInputDataName = goldInputDataElement.getAttributes().getNamedItem("name").getNodeValue();
			System.out.println("> " + goldeInputDataName);
			GoldInputData goldInputData = new GoldInputData(goldInputDataId, goldeInputDataName);
			goldInputDataList.put(goldInputData.getId(), goldInputData);

		}
		return goldInputDataList;
	}

	private LinkedHashMap<String, GoldDecision> getDecisionListFromDefinition() throws SAXException, IOException {
		LinkedHashMap<String, GoldDecision> goldDecisionList = new LinkedHashMap<String, GoldDecision>();
		NodeList goldDecisionNodeList = getElementsFromDefinition(goldXmlFile, "decision");
		for (int i = 0; i < goldDecisionNodeList.getLength(); i++) {
			Element goldDecisionElement = (Element) goldDecisionNodeList.item(i);
			String goldDecisionId = goldDecisionElement.getAttributes().getNamedItem("id").getNodeValue();
			String goldeDecisionName = goldDecisionElement.getAttributes().getNamedItem("name").getNodeValue();

			System.out.println("> Decision:");
			System.out.println("> " + goldeDecisionName);

			System.out.println(">>>> Requirement:");
			NodeList goldRequirementNodeList = goldDecisionElement.getElementsByTagName("informationRequirement");
			ArrayList<GoldRequirement> goldRequirementList = new ArrayList<GoldRequirement>();
			for (int j = 0; j < goldRequirementNodeList.getLength(); j++) {
				Element goldRequirementElement = (Element) goldRequirementNodeList.item(j);
				String goldRequirementId = goldRequirementElement.getAttribute("id");

				Element goldRequiredInputElement = (Element) goldRequirementElement
						.getElementsByTagName("requiredInput").item(0);
				String href = "";
				if (goldRequiredInputElement == null)
					goldRequiredInputElement = (Element) goldRequirementElement.getElementsByTagName("requiredDecision")
							.item(0);

				href = goldRequiredInputElement.getAttribute("href");

				GoldRequirement goldRequirement = new GoldRequirement(goldRequirementId, href);
				goldRequirementList.add(goldRequirement);

				System.out.println(">>>> href: " + href);
			}

			System.out.println(">>>>>>>> DecisionTable:");
			Element goldDecisionTableElement = (Element) goldDecisionElement.getElementsByTagName("decisionTable")
					.item(0);
			GoldDecisionTable goldDecisionTable = null;
			if (goldDecisionTableElement != null) {
				String decisionTableId = goldDecisionTableElement.getAttribute("id");
				System.out.println(">>>>>>>> " + decisionTableId);

				System.out.println(">>>>>>>>>>>> Rule:");
				NodeList goldRuleNodeList = goldDecisionTableElement.getElementsByTagName("rule");
				ArrayList<GoldRule> goldRuleList = new ArrayList<GoldRule>();
				for (int j = 0; j < goldRuleNodeList.getLength(); j++) {
					Element goldRuleElement = (Element) goldRuleNodeList.item(j);
					String goldRuleId = goldRuleElement.getAttribute("id");
					System.out.println(">>>>>>>>>>>> " + goldRuleId);

					System.out.println(">>>>>>>>>>>>>>>> InputEntry:");
					NodeList goldInputEntryNodeList = goldRuleElement.getElementsByTagName("inputEntry");
					ArrayList<GoldInputEntry> goldInputEntryList = new ArrayList<GoldInputEntry>();
					System.out.print(">>>>>>>>>>>>>>>> ");
					for (int k = 0; k < goldInputEntryNodeList.getLength(); k++) {
						Element goldInputEntryElement = (Element) goldInputEntryNodeList.item(k);
						String goldInputEntryId = goldInputEntryElement.getAttribute("id");

						String goldInputEntryName = goldInputEntryElement.getElementsByTagName("text").item(0)
								.getTextContent();
						goldInputEntryName = goldInputEntryName.replace("\"", "");
						if (!goldInputEntryName.isEmpty()) {
							GoldInputEntry goldInputEntry = new GoldInputEntry(goldInputEntryId, goldInputEntryName);
							goldInputEntryList.add(goldInputEntry);
						}

						System.out.print("[" + goldInputEntryName + "] ");
					}
					System.out.println();
					System.out.println(">>>>>>>>>>>>>>>> OutputEntry:");
					Element goldOutputEntryElement = (Element) goldRuleElement.getElementsByTagName("outputEntry")
							.item(0);
					String goldOutputEntryId = goldOutputEntryElement.getAttribute("id");
					String goldOutputEntryText = goldOutputEntryElement.getElementsByTagName("text").item(0)
							.getTextContent();
					goldOutputEntryText = goldOutputEntryText.replace("\"", "");
					GoldOutputEntry goldOutputEntry = new GoldOutputEntry(goldOutputEntryId, goldOutputEntryText);
					GoldRule goldRule = new GoldRule(goldRuleId, goldInputEntryList, goldOutputEntry);
					goldRuleList.add(goldRule);

					System.out.println(">>>>>>>>>>>>>>>> " + goldOutputEntryText);
				}
				goldDecisionTable = new GoldDecisionTable(decisionTableId, goldRuleList);
			}
			GoldDecision goldDecision = new GoldDecision(goldDecisionId, goldeDecisionName, goldRequirementList,
					goldDecisionTable);
			goldDecisionList.put(goldDecision.getId(), goldDecision);
		}

		return goldDecisionList;
	}

	private NodeList getElementsFromDefinition(File goldFile, String elementName) throws SAXException, IOException {
		Document document = builder.parse(goldFile);
		Element definition = (Element) document.getElementsByTagName("definitions").item(0);
		NodeList nodeList = definition.getElementsByTagName(elementName);
		// for (int i = 0; i < nodeList.getLength(); i++) {
		// Element elemnt = (Element) nodeList.item(i);
		// System.out.println("> " +
		// elemnt.getAttributes().getNamedItem("name").getNodeValue());
		// }
		return nodeList;
	}

	public Integer[] getGoldRequirementResult() {
		return goldRequirementResult;
	}

	public void setGoldRequirementResult(Integer[] goldRequirementResult) {
		this.goldRequirementResult = goldRequirementResult;
	}

	public Integer[] getExtractedRequirementResul() {
		return extractedRequirementResul;
	}

	public void setExtractedRequirementResul(Integer[] extractedRequirementResul) {
		this.extractedRequirementResul = extractedRequirementResul;
	}

	public Integer[] getCoincidenceRequirementResult() {
		return coincidenceRequirementResult;
	}

	public void setCoincidenceRequirementResult(Integer[] coincidenceRequirementResult) {
		this.coincidenceRequirementResult = coincidenceRequirementResult;
	}

}
