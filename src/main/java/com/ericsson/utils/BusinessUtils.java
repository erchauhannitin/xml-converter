package com.ericsson.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import input.generated.Address;
import input.generated.BirthPlace;
import input.generated.CompanyDetails;
import input.generated.DateDetails;
import input.generated.DateValue;
import input.generated.Description;
import input.generated.Descriptions;
import input.generated.Entity;
import input.generated.IDNumberTypes;
import input.generated.Name;
import input.generated.NameDetails;
import input.generated.NameValue;
import input.generated.Person;
import input.generated.Place;
import output.org.tempuri.sdnlist.SdnList;
import output.org.tempuri.sdnlist.SdnList.SdnEntry;
import output.org.tempuri.sdnlist.SdnList.SdnEntry.AddressList;
import output.org.tempuri.sdnlist.SdnList.SdnEntry.AkaList;
import output.org.tempuri.sdnlist.SdnList.SdnEntry.AkaList.Aka;
import output.org.tempuri.sdnlist.SdnList.SdnEntry.DateOfBirthList;
import output.org.tempuri.sdnlist.SdnList.SdnEntry.DateOfBirthList.DateOfBirthItem;
import output.org.tempuri.sdnlist.SdnList.SdnEntry.IdList;
import output.org.tempuri.sdnlist.SdnList.SdnEntry.IdList.Id;
import output.org.tempuri.sdnlist.SdnList.SdnEntry.PlaceOfBirthList;
import output.org.tempuri.sdnlist.SdnList.SdnEntry.PlaceOfBirthList.PlaceOfBirthItem;
import output.org.tempuri.sdnlist.SdnList.SdnEntry.ProgramList;

public enum BusinessUtils {

	INSTANCE;

	private static final String DATE_OF_BIRTH = "Date of Birth";
	private static final String ALSO_KNOWN_AS = "Also Known As";
	private static final String PRIMARY_NAME = "Primary Name";
	
	private static Properties configProperty = new Properties();
	private static final String SRC_FOLDER_PATH = PropertyReader.getPropertyValue("sourceFolderPath");
	private static final String COUNTRY_FILE_PATH = PropertyReader.getPropertyValue("countryCodeFilePath");
	private static final String FILTER_CATEGORY = PropertyReader.getPropertyValue("filterCategory");
	static Map<String, String> boMap = new HashMap<>();
	private static final Logger log = LoggerFactory.getLogger(BusinessUtils.class);
	
	static {
		boMap = convertToBoMap(FILTER_CATEGORY);
	}
	
	public void updateIdTypes(SdnEntry sdnEntry, IDNumberTypes idNumberTypes) {
		if(idNumberTypes != null) {
			IdList idList = new IdList();
			Id id = new Id();
		
			id.setIdType(idNumberTypes.getID().get(0).getIDType());
			id.setIdNumber(idNumberTypes.getID().get(0).getIDValue().get(0).getValue());
			idList.getId().add(id);
			sdnEntry.setIdList(idList);
		}
	}

	public void updatePersonName(SdnEntry sdnEntry, List<Name> nameList) {
		
		for (Name name : nameList) {
			String nameType = name.getNameType();
			if(PRIMARY_NAME.equalsIgnoreCase(nameType)) {
				
				NameValue nameValue = nameList.get(0).getNameValue().get(0);
				sdnEntry.setFirstName(nameValue.getFirstName().isEmpty() ? null :nameValue.getFirstName().get(0));
				sdnEntry.setLastName(nameValue.getSurname().isEmpty() ? null : nameValue.getSurname().get(0));
				sdnEntry.setTitle(nameValue.getTitleHonorific().isEmpty() ? null : nameValue.getTitleHonorific().get(0));
				
			}
			else if (ALSO_KNOWN_AS.equalsIgnoreCase(nameType)) {
				
				List<NameValue> nameValues = name.getNameValue();
				for (NameValue nameValue : nameValues) {
					
					AkaList akaList = new AkaList();
					Aka aka = new Aka();
					
					aka.setFirstName(nameValue.getFirstName().isEmpty() ? null :nameValue.getFirstName().get(0));
					aka.setLastName(nameValue.getSurname().isEmpty() ? null : nameValue.getSurname().get(0));
					
					akaList.getAka().add(aka);
					sdnEntry.setAkaList(akaList );
				}
			}
		}
		
	} 
	
	public List<String> extractFiles(File[] listOfFiles, List<String> totalFiles) {
		for (File file : listOfFiles) {
		    if (file.isFile() && file.getName().endsWith(".xml")) {
		       totalFiles.add(file.getAbsolutePath());
		    }
		    else if(file.isDirectory()){
		    	extractFiles(file.listFiles(), totalFiles);
		    }
		}
		
		return totalFiles;
		
	}
	
	public boolean updateEntityAddress(Entity entity, SdnEntry sdnEntry) {
		List<CompanyDetails> companyDetails = entity.getCompanyDetails();
		
		if(!companyDetails.isEmpty()) {
			AddressList targetAddressList = new AddressList();
			
			for (CompanyDetails companyDetail : companyDetails) {
		
				SdnList.SdnEntry.AddressList.Address targetAddress = new SdnList.SdnEntry.AddressList.Address();
				targetAddress.setCity(companyDetail.getAddressCity());
				String code = countryCodes(companyDetail.getAddressCountry());
			
				if(code != null ){
					targetAddress.setCountry(code);
				}else{
					return false;
				}
				
				targetAddressList.getAddress().add(targetAddress);
				sdnEntry.setAddressList(targetAddressList);

			}
			
		}
		
		return true;
	}
	
	public String countryCodes(String code){
		
		String  countrycode = "";
		if(code != null) {
			countrycode = configProperty.getProperty(code.toUpperCase());
		}
		return countrycode;
		
	}
	
	public File readFile(){
		
		File file = new File(SRC_FOLDER_PATH, PropertyReader.getPropertyValue("sourceFileName")); 
		
		return file;
	}
	
   public void loadCountriesfile(){
		
		File file = new File(COUNTRY_FILE_PATH, PropertyReader.getPropertyValue("countryCodeFilePrefix")); 
		
		try (InputStream configInput = new FileInputStream(file)) {
			if(configProperty.isEmpty()){
				configProperty.load(configInput);			
			}
		} 
		catch (IOException ex) {
			log.error("Error while loading country file");
		} 
		
	}
   
   public String convertNullToEmpty(String input) {
		
		if(input == null) {
			return "";
		}
		
		return input;
	}
   
	public boolean updateDateOfBirthList(Person person, SdnEntry sdnEntry) {
		
		DateDetails dateDetails = person.getDateDetails();
		DateOfBirthList dateOfBirthList = new DateOfBirthList();
		if(dateDetails != null) {
			List<input.generated.Date> dateList = dateDetails.getDate();
			if(!dateList.isEmpty()) {
				
				for (input.generated.Date date : dateList) {
					String dateType = date.getDateType();
					if(DATE_OF_BIRTH.equalsIgnoreCase(dateType)) {
						for (DateValue dateValue : date.getDateValue()) {
		
							DateOfBirthItem dateOfBirthItem = new DateOfBirthItem();
							String day = convertNullToEmpty(dateValue.getDay());
							String month = convertNullToEmpty(dateValue.getMonth());
							String year = convertNullToEmpty(dateValue.getYear());
							StringBuffer dob = new StringBuffer();
							if(year != "" && Integer.parseInt(year) > 1900) { 
								if(!day.isEmpty()) {
									dob.append(day).append(" ");
								}
								if(!month.isEmpty()) {
									dob.append(month).append(" ");
								}
								if(!year.isEmpty()) {
									dob.append(year);
								}
								dateOfBirthItem.setDateOfBirth(dob.toString());
							}
							else {
								return false;
							}
							dateOfBirthList.getDateOfBirthItem().add(dateOfBirthItem);	
						}
					}
				}
			}
		}
		sdnEntry.setDateOfBirthList(dateOfBirthList);
		return true;
	}
	public void updateBirthPlace(Person person, SdnEntry sdnEntry) {
		BirthPlace birthPlace = person.getBirthPlace();
		if(birthPlace != null) {
			Place place = birthPlace.getPlace().get(0);
			PlaceOfBirthItem placeOfBirthItem = new SdnList.SdnEntry.PlaceOfBirthList.PlaceOfBirthItem();
			placeOfBirthItem.setPlaceOfBirth(place.getName());
		
			PlaceOfBirthList placeOfBirthList = new SdnList.SdnEntry.PlaceOfBirthList();
			placeOfBirthList.getPlaceOfBirthItem().add(placeOfBirthItem);
			sdnEntry.setPlaceOfBirthList(placeOfBirthList);
		}
	}
	
	public SdnEntry updatePerson(Person person) {
			
			SdnEntry sdnEntry = new SdnEntry();
			NameDetails nameDetails = person.getNameDetails();
			
			if(nameDetails  != null) {
				List<Name> nameList = nameDetails.getName();
				updatePersonName(sdnEntry, nameList);
			}
			
			sdnEntry.setUid(Integer.valueOf(person.getId()));
			sdnEntry.setSdnType("Individual");
			
			List<Address> srcAddressList = person.getAddress();
			AddressList targetAddressList = updatePersonAddress(srcAddressList);
			
			sdnEntry.setAddressList(targetAddressList);
			
			ProgramList programList = new ProgramList();
			programList.getProgram().add("Conversion from Dow Jones xml");
			sdnEntry.setProgramList(programList);
			
			updateBirthPlace(person, sdnEntry);
			boolean isValid = updateDateOfBirthList(person, sdnEntry);
			if(!isValid) {
				return null;
			}
			
			IDNumberTypes idNumberTypes = person.getIDNumberTypes();
			updateIdTypes(sdnEntry, idNumberTypes);
	
			return sdnEntry;
			
		}
	
	public AddressList updatePersonAddress(List<Address> srcAddressList) {
		AddressList targetAddressList = new AddressList();
	
		for (Address srcAddress : srcAddressList) {
			SdnList.SdnEntry.AddressList.Address targetAddress = new SdnList.SdnEntry.AddressList.Address();
			targetAddress.setCity(srcAddress.getAddressCity());
			String code = countryCodes(srcAddress.getAddressCountry());
			if(code != null ){
				targetAddress.setCountry(code);
			}				
			targetAddressList.getAddress().add(targetAddress);
		}
		return targetAddressList;
	}
	
	public SdnList setMiscFields(SdnList sdnList) {
		
		SdnList.PublshInformation publshInformation = new SdnList.PublshInformation();
		sdnList.setPublshInformation(publshInformation);
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
	    
	    String strDate = dateFormat.format(new Date());
		publshInformation.setPublishDate(strDate);
		publshInformation.setRecordCount(sdnList.getSdnEntry().size());
		
		return sdnList;
	}
	
	public SdnEntry updateEntity(Entity entity, String inputFileName) {
	
		SdnEntry sdnEntry = new SdnEntry();
		if(entity.getVesselDetails().isEmpty()) {	//If Vessel details are populated then need to ignore.
			
			List<Name> nameList = entity.getNameDetails().getName();
			sdnEntry.setUid(Integer.valueOf(entity.getId()));
			sdnEntry.setSdnType("Entity");
	
			updatePersonName(sdnEntry, nameList);	//TODO - to update entity name accordingly
			
			IDNumberTypes idNumberTypes = entity.getIDNumberTypes();
			updateIdTypes(sdnEntry, idNumberTypes);
			boolean isValid =  updateEntityAddress(entity, sdnEntry);
			if(!isValid) {
				return null;
			}
	
		}
		else{
			log.error("Entity vessel details not empty for file " + inputFileName + " and Entity Id " + entity.getId());
		}
	
		return sdnEntry;
		
	}
	
	public boolean filterRecords(Descriptions descriptions) {
	
		boolean result = false;
		List<Description> descriptionList = descriptions.getDescription();

		String filterCategory = FILTER_CATEGORY;
		if(!descriptionList.isEmpty() && filterCategory != null) {
			for (Description desc : descriptionList) {
				if(matchDesc1(desc) && matchDec2(desc) && matchDesc3(desc)) {
					return true;
				}
			}
		}
		
		return result;
		
	}

	private boolean matchDesc3(Description desc) {

		if(boMap.containsKey("D3")) {
			return boMap.get("D3").equalsIgnoreCase(desc.getDescription3());
		}
		else {
			return true;
		}
				
	}

	private boolean matchDec2(Description desc) {

		if(boMap.containsKey("D2")) {
			return boMap.get("D2").equalsIgnoreCase(desc.getDescription2());
		}
		else {
			return true;
		}
		
	}

	private boolean matchDesc1(Description desc) {

		if(boMap.containsKey("D1")) {
			return boMap.get("D1").equalsIgnoreCase(desc.getDescription1());
		}
		else {
			return true;
		}
	
	}
	
	public static Map<String, String> convertToBoMap(String input){
		
		Map<String, String> descMap = new HashMap<>();
		
		if(input != null && !input.isEmpty()) {
			
			for (String descBO : input.split(",")) {
				String[] descString = descBO.split("#");
				descMap.put(descString[0], descString[1]);
			}
		}
		return descMap;
		
	}
	
	public boolean countryValidation(Person person) {
		
		boolean result = false;
		List<Address> srcAddressList = person.getAddress();
		
		for (Address srcAddress : srcAddressList) {	
			String code = countryCodes(srcAddress.getAddressCountry());
			if(code == null){
				return true;
			}
			
		}
		
		return result;
	
	}
}
