package pt.uninova.ditag.tool;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import pt.uninova.ditag.tool.report.Report;
import pt.uninova.ditag.tool.report.Report.ExitCode;
import pt.uninova.ditag.tool.schema.SchemaDocument;
import pt.uninova.ditag.tool.schema.SchemaTree;
import pt.uninova.ditag.tool.schema.SchemaTree.MatchingTimeoutException;
import pt.uninova.ditag.tool.semantics.SemanticComparator;
import pt.uninova.ditag.tool.translator.TranslatorCreator;

public class SemanticEngine {

	// Documents (Raw Data)
	private SchemaDocument consumerDocument;
	private SchemaDocument providerDocument;
	public SchemaDocument getConsumerDocument() {
		return consumerDocument;
	}
	public void setConsumerDocument(SchemaDocument consumerDocument) {
		this.consumerDocument = consumerDocument;
	}
	public SchemaDocument getProviderDocument() {
		return providerDocument;
	}
	public void setProviderDocument(SchemaDocument providerDocument) {
		this.providerDocument = providerDocument;
	}
	
	// Provider Java Classes
	private String consumerClass;
	private String providerClass;
	public String getConsumerClass() {
		return consumerClass;
	}
	public void setConsumerClass(String consumerClass) {
		this.consumerClass = consumerClass;
	}
	public String getProviderClass() {
		return providerClass;
	}
	public void setProviderClass(String providerClass) {
		this.providerClass = providerClass;
	}
	
	// Schema Trees
	private SchemaTree consumerTree;
	private SchemaTree providerTree;
	public SchemaTree getConsumerTree() {
		return consumerTree;
	}
	public void setConsumerTree(SchemaTree consumerTree) {
		this.consumerTree = consumerTree;
	}
	public SchemaTree getProviderTree() {
		return providerTree;
	}
	public void setProviderTree(SchemaTree providerTree) {
		this.providerTree = providerTree;
	}

	public Boolean translationStatus = true;
	public Boolean getTranslationStatus() {
		return translationStatus;
	}
	public void setTranslationStatus(Boolean translationStatus) {
		this.translationStatus = translationStatus;
	}
	
	// Semantic Comparator
	private SemanticComparator semanticComparator;
	public SemanticComparator getSemanticComparator() {
		return semanticComparator;
	}
	public void setSemanticComparator(SemanticComparator semanticComparator) {
		this.semanticComparator = semanticComparator;
	}

	public TranslatorCreator translatorCreator;
	public TranslatorCreator getTranslatorCreator() {
		return translatorCreator;
	}
	public void setTranslatorCreator(TranslatorCreator translatorCreator) {
		this.translatorCreator = translatorCreator;
	}
	
	public Report report;
	public Report getReport() {
		return report;
	}
	public void setReport(Report report) {
		this.report = report;
	}
	
	// Time limit (in seconds) for the obligatory matching; 0 or less means no limit
	public int timeoutSeconds = 0;
	public int getTimeoutSeconds() {
		return timeoutSeconds;
	}
	public void setTimeoutSeconds(int timeoutSeconds) {
		this.timeoutSeconds = timeoutSeconds;
	}
	
	public boolean firstMatch = true;
	public boolean isFirstMatch() {
		return firstMatch;
	}
	public void setFirstMatch(boolean firstMatch) {
		this.firstMatch = firstMatch;
	}

	// *** *** //
	
	public SemanticEngine() {
		super();
		
		this.report = new Report();
	}
	
	public void runEngine() {
	
		this.consumerTree = new SchemaTree(this.consumerDocument.getData(), this.semanticComparator);
		this.providerTree = new SchemaTree(this.providerDocument.getData(), this.semanticComparator);
		
		DTLogger.logger.fine(this.consumerTree.toString());
		DTLogger.logger.fine(this.providerTree.toString());

		this.report.setConsumerTree(this.consumerTree);
		this.report.setProviderTree(this.providerTree);
		
		this.consumerTree.setTimeoutSeconds(this.timeoutSeconds);
		
		try {
			Pair<List<Map<String, Object>>, List<Triple<String, String, Boolean>>> engineReturn = this.consumerTree.match(this.providerTree, true, this.firstMatch);
			
			this.report.setCombinations(engineReturn.getLeft());
			this.report.setTryLog(engineReturn.getRight());
		} catch (MatchingTimeoutException e) {
			DTLogger.logger.info(e.getMessage());
			this.report.setExitCode(ExitCode.TIMEOUT);
			this.translationStatus = false;
		} catch (Exception e) {
			e.printStackTrace();
			this.translationStatus = false;
		}
		
		this.translatorCreator = new TranslatorCreator(this.consumerClass, this.providerClass, this.consumerTree, this.providerTree);
	}
}	
