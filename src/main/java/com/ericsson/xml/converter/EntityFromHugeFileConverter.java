package com.ericsson.xml.converter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;

import com.ericsson.utils.BusinessUtils;
import com.ericsson.utils.FileUtils;
import com.ericsson.utils.PropertyReader;

import input.generated.Entity;
import input.generated.PFA;
import input.generated.Records;
import output.org.tempuri.sdnlist.SdnList;
import output.org.tempuri.sdnlist.SdnList.SdnEntry;

public final class EntityFromHugeFileConverter extends FileUtils{
	
	private static final String END_TAG_ENTITY = "</Entity>";
	private static final String BEGIN_TAG_ENTITY = "<Entity ";
	private static final String INVAILD_COUNTRY_CODE = "InvaildCountry";
	private static final String FILE_NAME = "Entity_Output_";

	private static List<Entity> invalidEntityCodeList = new ArrayList<>();
	private static final String FILTER_CATEGORY = PropertyReader.getPropertyValue("filterCategory");
    private static final Logger log = LoggerFactory.getLogger(EntityFromHugeFileConverter.class);

	private EntityFromHugeFileConverter() {
		//Epmty Constructor
	}

	public static void main(String[] args) throws IOException {

		long startTime = System.currentTimeMillis();
		log.info("Starting execution -> Entity extraction from Huge file");
		
		validateCategory();
		SdnList sdnList = new SdnList();

		try {
			PFA pfa = extractEntityRecords(BusinessUtils.INSTANCE.readFile());
			List<Entity> entities = pfa.getRecords().getEntity();
			BusinessUtils.INSTANCE.loadCountriesfile();
			for (Entity entity : entities) {
				SdnEntry sdnEntry = BusinessUtils.INSTANCE.updateEntity(entity, null);
				if(sdnEntry != null){
					sdnList.getSdnEntry().add(sdnEntry);
					}else{
						
						invalidEntityCodeList.add(entity);
					}
			}
			BusinessUtils.INSTANCE.setMiscFields(sdnList);
			jaxbObjectToXML(sdnList, FILE_NAME);
			jaxbEntityObjectToXML(invalidEntityCodeList,INVAILD_COUNTRY_CODE);
		} catch (XMLStreamException | JAXBException e) {
			log.error("Error occured while transforming to xml ", e);
		}
	
		log.info("Execution ended successfully -> Entity extraction from Huge file");
		log.info("Execution time {} seconds", (System.currentTimeMillis() - startTime)/1000);
		
	}

	private static void validateCategory() {

		if(!FILTER_CATEGORY.isEmpty() &&
				!FILTER_CATEGORY.contains("D4"))
		{
			Scanner scan = new Scanner(System.in);
			log.info("Filter category used would probably not result in any output data. \n"
					+ "Please change the category used or use Person program. \n"
					+ "Press any key if you still want to continue or N to stop execution ?");
			

			if("n".equalsIgnoreCase(scan.nextLine())) {
				System.exit(0);
			}
			
			scan.close();
		}
	}

	private static PFA extractEntityRecords(File file) throws XMLStreamException, JAXBException, IOException {
        
        JAXBContext context = JAXBContext.newInstance(PFA.class);
        Unmarshaller un = context.createUnmarshaller();

        FileReader fr = new FileReader(file);
        StringBuffer sb = new StringBuffer(5000);
        boolean entityFound = false;
        String readLine = "";
        
        PFA pfa = new PFA();
        Records records = new Records();
        List<Entity> entities = null;
        pfa.setRecords(records);
        
	    try (BufferedReader br = new BufferedReader(fr)){
	        while((readLine = br.readLine()) != null) {
	        	if(readLine != null && readLine.contains(BEGIN_TAG_ENTITY) && !entityFound) {
	        		entityFound = true;
	        	}
	        	if(entityFound) {
	        		sb.append(readLine);
	        		if(readLine.contains(END_TAG_ENTITY)) {
	        			entities = convertToEntityObject(sb.toString(), un);
	        			if(entities != null) {
	        				for(Entity entity : entities) {
	        					boolean toBeIncluded = BusinessUtils.INSTANCE.filterRecords(entity.getDescriptions());
		    		    		if(toBeIncluded) {
		    		    			records.getEntity().add(entity);	
		    		    		}	   
	        				}
	        			}
	        			sb = new StringBuffer(5000);
	        			if(!readLine.endsWith(END_TAG_ENTITY)) {
	        				sb.append(readLine);
	        			}
	        		}
	        	}
	        }
	    }
        return pfa;
        
	}

	private static List<Entity> convertToEntityObject(String input, Unmarshaller un) {
	
		String beginTags = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n" + 
        		"<PFA date=\"201505312200\" type=\"full\">\r\n" + 
        		"	<Records>\r\n";
		
		String endTags = "</Entity></Records></PFA>";
		
		int beginIndex = input.indexOf(BEGIN_TAG_ENTITY);
		int endIndex = input.lastIndexOf(END_TAG_ENTITY);
		
		String entityString = input.substring(beginIndex, endIndex);
		
		StringBuffer sb = new StringBuffer(beginTags);
		sb.append(entityString);
		sb.append(endTags);
		
		List<Entity> entities = null;
		try {
            StringReader reader = new StringReader(sb.toString());
            PFA pfa = (PFA) un.unmarshal(reader);
            
            entities = pfa.getRecords().getEntity();
            
        } catch (JAXBException e) {
        	log.error("Exception occured while converting XML to object");
        }
		
		return entities;
		
	}

}