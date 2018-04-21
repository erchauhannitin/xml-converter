package com.ericsson.xml.converter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

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
import com.ericsson.utils.FileUtils;
import com.ericsson.utils.PropertyReader;

import input.generated.Person;
import input.generated.Records;
import output.org.tempuri.sdnlist.SdnList;
import output.org.tempuri.sdnlist.SdnList.SdnEntry;

public final class ExtractPersonById extends FileUtils{
	
	private static final String FILE_NAME = "Person_Id_";

	private static final String FILTER_CATEGORY = PropertyReader.getPropertyValue("filterCategory");
	private static final String PERSON_ID = PropertyReader.getPropertyValue("personId");

    private static final Logger log = LoggerFactory.getLogger(ExtractPersonById.class);

	private ExtractPersonById() {
		//Epmty Constructor
	}

	public static void main(String[] args) throws IOException {

		long startTime = System.currentTimeMillis();
		log.info("Starting execution -> Person extraction based on Id from Huge file");
		validateCategory();
		
		try {
			extractPersonRecords(BusinessUtils.INSTANCE.readFile());
		} catch (XMLStreamException | JAXBException e) {
			log.error("Error occured while transforming to xml ", e);
		}
			
		log.info("Execution ended successfully -> Person extraction based on Id from Huge file");
		log.info("Execution time {} seconds "+ (System.currentTimeMillis() - startTime)/1000);
		
	}

	private static void validateCategory() {

		if(!FILTER_CATEGORY.isEmpty() &&
				FILTER_CATEGORY.contains("D4"))
		{
			Scanner scan = new Scanner(System.in);
			log.info("Filter category used would probably not result in any output data. \n"
					+ "Please change the category used or use Entity program. \n"
					+ "Press any key if you still want to continue or N to stop execution ?");
			

			if("n".equalsIgnoreCase(scan.nextLine())) {
				System.exit(0);
			}
			
			scan.close();
		}
	}
	
	private static SdnList extractPersonRecords(File file) throws FileNotFoundException, XMLStreamException, JAXBException {
        
        XMLInputFactory xmlif = XMLInputFactory.newInstance();
        FileReader fr = new FileReader(file);

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
        
        SdnList sdnList = new SdnList();
        if(xmlfer.hasNext()){
	        StartElement startElement = (StartElement) xmlfer.peek();
	        
	        JAXBContext context = JAXBContext.newInstance(new Class[] { Records.class, Person.class });
	        String elementName = context.createJAXBIntrospector().getElementName(new Person()).getLocalPart();
	        
			if (elementName.equals(startElement.getName().getLocalPart())) {
	            sdnList = prepareRecordsList(xmler, xmlfer, context, sdnList);
	        }
        }
        
        return sdnList;
	}

	private static SdnList prepareRecordsList(XMLEventReader xmler, XMLEventReader xmlfer, JAXBContext context,
			SdnList sdnList) throws JAXBException, XMLStreamException {
		Unmarshaller unmarshaller = context.createUnmarshaller();
		BusinessUtils.INSTANCE.loadCountriesfile();

		while (xmlfer.peek() != null) {
		    Object object = unmarshaller.unmarshal(xmler);
		    
		    if (object instanceof Person) {
		    	Person person = (Person)object;
		    	
		    	if(PERSON_ID.equals(person.getId())) {
		    		log.info("Match Found");
		    		SdnEntry sdnEntry = BusinessUtils.INSTANCE.updatePerson(person);
		    		sdnList.getSdnEntry().add(sdnEntry);
		    	}
		    }
		}

		sdnList = BusinessUtils.INSTANCE.setMiscFields(sdnList);
		jaxbObjectToXML(sdnList, FILE_NAME);
		
		return sdnList;
		
	}

}