package com.ericsson.xml.converter;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.utils.BusinessUtils;
import com.ericsson.utils.PropertyReader;

import input.generated.Entity;
import input.generated.IDNumberTypes;
import input.generated.Name;
import input.generated.PFA;
import input.generated.Person;
import input.generated.Records;
import output.org.tempuri.sdnlist.SdnList;
import output.org.tempuri.sdnlist.SdnList.SdnEntry;

public final class ConverterMain {
	
	private static final Logger log = LoggerFactory.getLogger(ConverterMain.class);
	
	private ConverterMain() {
		
	}
	
	private static final String TARGET_FOLDER_PATH = "targetFolderPath";
	private static final String FILE_NAME = PropertyReader.getPropertyValue("targetFilePrefix");
	private static final String FILE_EXTENSION = ".xml";

	public static void main(String[] args) {

		PFA pfa = jaxbXMLToObject();
		
		SdnList sdnList = new SdnList();
		SdnList.PublshInformation publshInformation = new SdnList.PublshInformation();
		
		sdnList.setPublshInformation(publshInformation);
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        
        String strDate = dateFormat.format(new Date());
		publshInformation.setPublishDate(strDate);
		
		Records records = pfa.getRecords();
		List<Person> persons = records.getPerson();
		
		//load countries.properties file
		BusinessUtils.INSTANCE.loadCountriesfile();
	    
		for (Person person : persons) {
			
	    	SdnEntry sdnEntry = BusinessUtils.INSTANCE.updatePerson(person);
	    	sdnList.getSdnEntry().add(sdnEntry);
	    	
		}
		
		List<Entity> entities = records.getEntity();
		int vesselCount = 0;
		
		for (Entity entity : entities) {
			if(entity.getVesselDetails().isEmpty()) {	//If Vessel details are populated then need to ignore.
				
				SdnEntry sdnEntry = new SdnEntry();
				List<Name> nameList = entity.getNameDetails().getName();
				sdnEntry.setUid(Integer.valueOf(entity.getId()));
				sdnEntry.setSdnType("Entity");

				BusinessUtils.INSTANCE.updatePersonName(sdnEntry, nameList);
				
				IDNumberTypes idNumberTypes = entity.getIDNumberTypes();
				BusinessUtils.INSTANCE.updateIdTypes(sdnEntry, idNumberTypes);
				BusinessUtils.INSTANCE.loadCountriesfile();
				sdnList.getSdnEntry().add(sdnEntry);
				
			}
			else {
				vesselCount++;
			}
		}

		publshInformation.setRecordCount(sdnList.getSdnEntry().size());
		jaxbObjectToXML(sdnList);
		
		log.info("Number of vessels are "+ vesselCount);
	}

	private static PFA jaxbXMLToObject() {
        try {
            JAXBContext context = JAXBContext.newInstance(PFA.class);
            Unmarshaller un = context.createUnmarshaller();
			//File file = new File(FILE_PATH, FileUtility.getPropertyValue("sourceFileName")); 
            return (PFA) un.unmarshal(BusinessUtils.INSTANCE.readFile());
            
        } catch (JAXBException e) {
        	log.error("Exception occured while converting XML to object");
        }
        return null;
    }

		
	private static void jaxbObjectToXML(SdnList sdnList)  {

		try {
			JAXBContext context = JAXBContext.newInstance(SdnList.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			// Write to System.out for debugging
			m.marshal(sdnList, System.out);
			
			//creating Time stamp
			DateFormat df = new SimpleDateFormat("yyyyMMddhhmmss", Locale.US); // add S if you need milliseconds
			String filename = FILE_NAME + df.format(new Date()) + FILE_EXTENSION;
			
			//Create folder
			File file = new File(PropertyReader.getPropertyValue(TARGET_FOLDER_PATH)+ "/" + filename);
			file.getParentFile().mkdir();
			
			// Write to File
			m.marshal(sdnList, file);
		} 
		catch (JAXBException e) {
			log.error("Error while marshalling");
		}
	}
	
}
