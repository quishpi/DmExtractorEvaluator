package edu.upc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FilenameUtils;
import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;

import edu.upc.evaluator.DmnEvaluator;
import edu.upc.freelingutils.dm.DmnFoldersUrl;
import edu.upc.freelingutils.dm.DmnFreelingUtils;

public class App {
	static String lang = "en";
	static DmParser parser;
	private static File dmnInputFolder = new File("/home/luis/doc2dmnutils/" + lang + "/input/dmn");

	public static void main(String[] args)
			throws IOException, SAXException, ParserConfigurationException, ParseException {
		if (args.length != 0) {
			FileWriter csvWriterRequirement = new FileWriter(DmnFoldersUrl.OUTPUT_FOLDER + "evaluationRequirement.csv");
			FileWriter csvWriterLogic = new FileWriter(DmnFoldersUrl.OUTPUT_FOLDER + "evaluationLogic.csv");
			String title = "File\tG-Decision\tG-InputData\tG-Requirement\tG-Total\t"
					+ "E-Decision\tE-InputData\tE-Requirement+\tE-Total\t"
					+ "C-Decision\tC-InputData\tC-Requirement+\tC-Total\t" + "Precision\tRecall\tFscore\n";
			String titleLogic = "File\tG-InputEntry\tG-OutputEntry\tG-Rule\tG-Total"
					+ "\tE-InputEntry\tE-OutputEntry\tE-Rule\tE-Total"
					+ "\tC-InputEntry\tC-OutputEntry\tC-Rule\tC-Total\t" + "Precision\tRecall\tFscore\n";
			csvWriterRequirement.append(title);
			csvWriterLogic.append(titleLogic);
			File[] files = dmnInputFolder.listFiles();
			Arrays.sort(files);
			for (File path : files) {
				String nlpTextFilePath = path.toString();
				String justNameOfFile = DmnFreelingUtils.getFileNameWithoutExtension(new File(nlpTextFilePath));
				String extensionOfFile = FilenameUtils.getExtension(nlpTextFilePath);
				if (extensionOfFile.equals("dmn")) {
					System.out.println("-----------------------------------------------");
					System.out.println("---------------- " + justNameOfFile + "------------");
					System.out.println("-----------------------------------------------");
					nlpTextFilePath = DmnFoldersUrl.INPUT_FOLDER + "/texts/" + justNameOfFile + ".txt";
					DmnEvaluator evaluator = new DmnEvaluator(nlpTextFilePath, lang);
					evaluator.RequirementScore();
					evaluator.LogicScore();
					csvWriterRequirement.append(evaluator.getRequirimentCsvRow());
					csvWriterLogic.append(evaluator.getLogicCsvRow());
				}
			}
			csvWriterRequirement.flush();
			csvWriterRequirement.close();
			csvWriterLogic.flush();
			csvWriterLogic.close();
		} else {
			String nlpTextFilePath = DmnFoldersUrl.INPUT_FOLDER + "/texts/";
			nlpTextFilePath += "test";
			nlpTextFilePath += ".txt";
			String justNameOfFile = DmnFreelingUtils.getFileNameWithoutExtension(new File(nlpTextFilePath));
			System.out.println("-----------------------------------------------");
			System.out.println("---------------- " + justNameOfFile + "------------");
			System.out.println("-----------------------------------------------");
			DmnEvaluator evaluator = new DmnEvaluator(nlpTextFilePath, lang);
			evaluator.RequirementScore();
			evaluator.LogicScore();
			FileWriter csvWriter = new FileWriter(DmnFoldersUrl.OUTPUT_FOLDER + "evaluationIndividual.csv");
			String title = "File\tG-Decision\tG-InputData\tG-Requirement\tG-Total\t"
					+ "E-Decision\tE-InputData\tE-Requirement+\tE-Total\t"
					+ "C-Decision\tC-InputData\tC-Requirement+\tC-Total\t" + "Precision\tRecall\tFscore\n";
			csvWriter.append(title);
			csvWriter.append(evaluator.getRequirimentCsvRow());
			title = "File\tG-InputEntry\tG-OutputEntry\tG-Rule\tG-Total"
					+ "\tE-InputEntry\tE-OutputEntry\tE-Rule+\tE-Total"
					+ "\tC-InputEntry\tC-OutputEntry\tC-Rule+\tC-Total\t" + "Precision\tRecall\tFscore\n";
			csvWriter.append(title);
			csvWriter.append(evaluator.getLogicCsvRow());

			csvWriter.flush();
			csvWriter.close();

		}

		System.out.println("Evaluation DONE!");
	}
}
