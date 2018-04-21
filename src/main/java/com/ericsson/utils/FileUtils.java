package com.ericsson.utils;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import input.generated.Entity;
import input.generated.PFA;
import input.generated.Person;
import input.generated.Records;
import output.org.tempuri.sdnlist.SdnList;
import output.org.tempuri.sdnlist.SdnList.SdnEntry;

public class FileUtils {
	
	protected FileUtils() {
		//Epmty Constructor
	}

	private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

	private static final String TARGET_FOLDER_PATH = "targetFolderPath";
	private static final String FILE_EXTENSION = ".xml";
	protected static final int TARGET_FILE_ENTRY_SIZE = Integer.parseInt(PropertyReader.getPropertyValue("targetFileEntrySize"));
	private static final String FILTER_CATEGORY = PropertyReader.getPropertyValue("filterCategory");

	private static void writeToFile(String fileName, PFA pfa) {
		
		DateFormat df = new SimpleDateFormat("yyyyMMddhhmmssS", Locale.US); 
		String filename = fileName + "_" + df.format(new Date()) + "_" + FILTER_CATEGORY + FILE_EXTENSION;
		
		File file = new File(PropertyReader.getPropertyValue(TARGET_FOLDER_PATH)+ "/" + filename);
		file.getParentFile().mkdir();

		try {
			JAXBContext context = JAXBContext.newInstance(PFA.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			m.marshal(pfa, file);
			
		} 
		catch (JAXBException e) {
			log.error("Error while marshalling");
		}

	}
	
	protected static void jaxbPersonObjectToXML(List<Person> list, String fileName) {

		if(list.isEmpty()) {
			log.info("No person records found for {}", fileName);
			return;
		}
		
		log.info("Number of invalid records for {} are {}", fileName, list.size());
		
		PFA pfa = new PFA();
		Records records = new Records();
		pfa.setRecords(records);
		int entrySize = list.size();
		
		if(entrySize > TARGET_FILE_ENTRY_SIZE) {
			for(int i = 0 ; i < entrySize; i = i + TARGET_FILE_ENTRY_SIZE) {
				List<Person> newList = new ArrayList<>();
				if(entrySize - i > TARGET_FILE_ENTRY_SIZE) {
					newList = list.subList(i, i+TARGET_FILE_ENTRY_SIZE);
				}
				else {
					newList = list.subList(i, entrySize);
				}
				pfa.getRecords().getPerson().clear();
				pfa.getRecords().getPerson().addAll(newList);				
				writeToFile(fileName, pfa);
			}
		}
		else {
			pfa.getRecords().getPerson().addAll(list);
			writeToFile(fileName, pfa);
		}
		
	}

	protected static void jaxbEntityObjectToXML(List<Entity> list, String fileName) {
		
		if(list.isEmpty()) {
			log.info("No entity records found with invalid country code");
			return;
		}
			
		log.info("Number of invalid records for {} are {}", fileName, list.size());
		PFA pfa = new PFA();
		Records records = new Records();
		pfa.setRecords(records);
		
		int entrySize = list.size();
        if(entrySize > TARGET_FILE_ENTRY_SIZE) {
			
			for(int i = 0 ; i < entrySize; i = i + TARGET_FILE_ENTRY_SIZE) {
				List<Entity> newList = new ArrayList<>();
				if(entrySize - i > TARGET_FILE_ENTRY_SIZE) {
					newList = list.subList(i, i+TARGET_FILE_ENTRY_SIZE);
				}
				else {
					newList = list.subList(i, entrySize);
				}
				pfa.getRecords().getEntity().clear();
				pfa.getRecords().getEntity().addAll(newList);				
				writeToFile(fileName, pfa);
			}
		}
		else {
			pfa.getRecords().getEntity().addAll(list);
			writeToFile(fileName, pfa);
		}
		
	}

	protected static void jaxbObjectToXML(SdnList sdnList, String fileName)  {
		
		if(sdnList.getSdnEntry().isEmpty()) {
			log.info("No successful records found");
			return;
		}
		
		log.info("Number of processed records for {} are {}", fileName, sdnList.getSdnEntry().size());
		try {
			JAXBContext context = JAXBContext.newInstance(SdnList.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			List<SdnEntry> sdnEntries = sdnList.getSdnEntry();	
			int entrySize = sdnEntries.size();
			if(entrySize > TARGET_FILE_ENTRY_SIZE) {
				
				for(int i = 0 ; i < entrySize; i = i + TARGET_FILE_ENTRY_SIZE) {
					
					List<SdnEntry> newSdnEntries = new ArrayList<>();
					if(entrySize - i > TARGET_FILE_ENTRY_SIZE) {
						newSdnEntries = sdnEntries.subList(i, i+TARGET_FILE_ENTRY_SIZE);
					}
					else {
						newSdnEntries = sdnEntries.subList(i, entrySize);
					}
					
					SdnList newSdnList = new SdnList();
					newSdnList.getSdnEntry().addAll(newSdnEntries);
					newSdnList = BusinessUtils.INSTANCE.setMiscFields(newSdnList);

					File file = writeToFile(fileName);
					m.marshal(newSdnList, file);
				}
			}
			else {
				File file = writeToFile(fileName);
				sdnList = BusinessUtils.INSTANCE.setMiscFields(sdnList);
				m.marshal(sdnList, file);
			}
		} 
		catch (JAXBException e) {
			log.error("Error while marshalling");
		}
		
	}
	
	private static File writeToFile(String fileName) {
		DateFormat df = new SimpleDateFormat("yyyyMMddhhmmssS", Locale.US); 
		String filename = fileName + "_" + df.format(new Date()) + "_" + FILTER_CATEGORY +  FILE_EXTENSION;
		
		File file = new File(PropertyReader.getPropertyValue(TARGET_FOLDER_PATH)+ "/" + filename);
		file.getParentFile().mkdir();
		return file;
	}
	
}
