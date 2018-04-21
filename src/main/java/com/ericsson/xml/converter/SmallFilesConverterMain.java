package com.ericsson.xml.converter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.utils.BusinessUtils;
import com.ericsson.utils.PropertyReader;
import com.ericsson.utils.FileUtils;

import input.generated.Entity;
import input.generated.Person;
import input.generated.Records;
import output.org.tempuri.sdnlist.SdnList;
import output.org.tempuri.sdnlist.SdnList.SdnEntry;

public final class SmallFilesConverterMain  extends FileUtils{
	
	private static final String SRC_FOLDER_PATH = PropertyReader.getPropertyValue("sourceFolderPath");
	private static final String INVAILD_COUNTRY_CODE = "Invaild_Country";
	private static final String INVAILD_DATE_OF_BIRTH = "Invaild_DOB";
	private static final String FILE_NAME = "Output_";

	private static List<Entity> invalidEntityCodeList = new ArrayList<>();
	private static List<Person> invalidPersonDobList = new ArrayList<>();
	private static List<Person> invalidPersonCountryList = new ArrayList<>();

	private static final Logger log = LoggerFactory.getLogger(SmallFilesConverterMain.class);

	private SmallFilesConverterMain() {
		//Epmty Constructor
	}

	public static void main(String[] args) throws IOException {

		long startTime = System.currentTimeMillis();
		log.info("Starting execution -> Records extraction from Small file/s");
		
		File folder = new File(SRC_FOLDER_PATH);
		File[] listOfFiles = folder.listFiles();
		List<String> totalFiles = new ArrayList<>();

		totalFiles = BusinessUtils.INSTANCE.extractFiles(listOfFiles, totalFiles);
		BusinessUtils.INSTANCE.loadCountriesfile();
		
		if(totalFiles.isEmpty()) {
			log.info("No xml files found");
		}
		
		SdnList personList = new SdnList();
		SdnList entityList = new SdnList();
		
		for(String file : totalFiles) {
			try {
				personList = extractPersonRecords(file, personList);
				entityList = extractEntityRecords(file, entityList);
			} catch (XMLStreamException | JAXBException e) {
				log.error("Error occured while transforming to xml ", e);
			}
		}
		
		jaxbObjectToXML(personList, FILE_NAME);
		jaxbObjectToXML(entityList, FILE_NAME);
		jaxbPersonObjectToXML(invalidPersonDobList, INVAILD_DATE_OF_BIRTH);
		jaxbPersonObjectToXML(invalidPersonCountryList, INVAILD_COUNTRY_CODE);
		jaxbEntityObjectToXML(invalidEntityCodeList, INVAILD_COUNTRY_CODE);
		
		log.info("Execution ended successfully -> Records extraction from Small file/s");
		log.info("Execution time {} seconds", (System.currentTimeMillis() - startTime)/1000);
		
	}

	private static SdnList extractPersonRecords(String inputFileName, SdnList sdnList) throws FileNotFoundException, XMLStreamException, JAXBException {
        
		// Parse the data, filtering out the start elements
        XMLInputFactory xmlif = XMLInputFactory.newInstance();
        FileReader fr = new FileReader(inputFileName);

        XMLEventReader xmler = xmlif.createXMLEventReader(fr);
        
		EventFilter filter = new EventFilter() {
            public boolean accept(XMLEvent event) {
            	 if (!event.isStartElement()) {
                     return false;
                 }
                 QName elementQName = event.asStartElement().getName();
                 if ("Person".equals(elementQName.getLocalPart())) {
                     return true;
                 }
                 return false;
             }
            
        };

        XMLEventReader xmlfer = xmlif.createFilteredReader(xmler, filter);
        
        // Jump to the first element in the document, the enclosing element
	    if(xmlfer.hasNext()){
	        StartElement startElement = (StartElement) xmlfer.peek();
	        
	        JAXBContext context = JAXBContext.newInstance(new Class[] { Records.class, Person.class });
	        String elementName = context.createJAXBIntrospector().getElementName(new Person()).getLocalPart();
	        
			if (elementName.equals(startElement.getName().getLocalPart())) {
	            sdnList = prepareRecordsList(xmler, xmlfer, context, sdnList,inputFileName);
	        }
	    }
        
        return sdnList;
	}

	private static SdnList extractEntityRecords(String inputFileName, SdnList sdnList) throws FileNotFoundException, XMLStreamException, JAXBException {
        
		// Parse the data, filtering out the start elements
        XMLInputFactory xmlif = XMLInputFactory.newInstance();
        FileReader fr = new FileReader(inputFileName);

        XMLEventReader xmler = xmlif.createXMLEventReader(fr);
		
        EventFilter filter = new EventFilter() {
            public boolean accept(XMLEvent event) {
            	 if (!event.isStartElement()) {
                     return false;
                 }
                 QName elementQName = event.asStartElement().getName();
                 if ("Entity".equals(elementQName.getLocalPart())) {
                     return true;
                 }
                 return false;
             }
            
        };
        
        XMLEventReader xmlfer = xmlif.createFilteredReader(xmler, filter);
        
        // Jump to the first element in the document, the enclosing element
        if(xmlfer.hasNext()){
	        StartElement startElement = (StartElement) xmlfer.peek();
	        
	        JAXBContext context = JAXBContext.newInstance(new Class[] { Entity.class });
	        String elementName = context.createJAXBIntrospector().getElementName(new Entity()).getLocalPart();
	        
			
	        if (elementName.equals(startElement.getName().getLocalPart())) {
	            sdnList = prepareRecordsList(xmler, xmlfer, context, sdnList,inputFileName);
	        }
	    }
        
        return sdnList;
	}

	private static SdnList prepareRecordsList(XMLEventReader xmler, XMLEventReader xmlfer, JAXBContext context,
			SdnList sdnList, String inputFileName) throws JAXBException, XMLStreamException {
		Unmarshaller unmarshaller = context.createUnmarshaller();
   
		while (xmlfer.peek() != null) {
		    Object object = unmarshaller.unmarshal(xmler);
		    
		    if (object instanceof Person) {
		    	Person person = (Person)object;
		    	
		    	boolean toBeIncluded = BusinessUtils.INSTANCE.filterRecords(person.getDescriptions());
		    	if(toBeIncluded) {
		    		if(BusinessUtils.INSTANCE.countryValidation(person)){
		    			invalidPersonCountryList.add(person);
		    			continue;
		    		}
		    		SdnEntry sdnEntry = BusinessUtils.INSTANCE.updatePerson(person);
		    		if(sdnEntry != null) {
		    			sdnList.getSdnEntry().add(sdnEntry);
		    		}
		    		else {
		    			invalidPersonDobList.add(person);
		    		}
		    	}
		    }
		    
		    else if (object instanceof Entity) {
		    	Entity entity = (Entity)object;
		    	boolean toBeIncluded = BusinessUtils.INSTANCE.filterRecords(entity.getDescriptions());
		    	if(toBeIncluded) {
		    	   	SdnEntry sdnEntry = BusinessUtils.INSTANCE.updateEntity(entity, inputFileName);
		    	   	if(sdnEntry != null) {
		    			sdnList.getSdnEntry().add(sdnEntry);
		    		}
		    		else {
		    			invalidEntityCodeList.add(entity);
		    		}
		    	}
		    }

		}
		
		sdnList = BusinessUtils.INSTANCE.setMiscFields(sdnList);

		return sdnList;
		
	}
}