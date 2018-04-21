package com.ericsson.xml.sim;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.utils.PropertyReader;

import input.generated.BirthPlace;
import input.generated.CompanyDetails;
import input.generated.Country;
import input.generated.CountryDetails;
import input.generated.CountryValue;
import input.generated.Date;
import input.generated.DateDetails;
import input.generated.DateValue;
import input.generated.Description;
import input.generated.Descriptions;
import input.generated.Entity;
import input.generated.ID;
import input.generated.IDNumberTypes;
import input.generated.IDValue;
import input.generated.Image;
import input.generated.Images;
import input.generated.Name;
import input.generated.NameDetails;
import input.generated.NameValue;
import input.generated.PFA;
import input.generated.Person;
import input.generated.Place;
import input.generated.Records;
import input.generated.Reference;
import input.generated.SanctionsReferences;
import input.generated.Source;
import input.generated.SourceDescription;

public final class GenerateDowJonesData {

	private static final Logger log = LoggerFactory.getLogger(GenerateDowJonesData.class);

	private GenerateDowJonesData() {
		//Epmty Constructor
	}
	
	private static final String TARGET_FOLDER_PATH = "targetFolderPath";
	private static final String FILE_NAME = "GeneratedData_";
	private static final String FILE_EXTENSION = ".xml";

	public static void main(String[] args) {

		Person person = preparePerson();
		Entity entity = prepareEntity();

		PFA pfa = new PFA();
		
		Records records = new Records();
		pfa.setRecords(records);
		
		List<Person> personList = new ArrayList<>();
		List<Entity> entityList = new ArrayList<>();
		
		for (int i = 0; i < 1; i++) {
			personList.add(person);
		}
		records.getPerson().addAll(personList);
		
		for (int i = 0; i < 3; i++) {
			entityList.add(entity);
		}
		records.getEntity().addAll(entityList);

		jaxbObjectToXML(pfa);
		
	}
	
	private static void jaxbObjectToXML(PFA pfa)  {

		try {
			JAXBContext context = JAXBContext.newInstance(PFA.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			DateFormat df = new SimpleDateFormat("yyyyMMddhhmmss", Locale.US); // add S if you need milliseconds
			String filename = FILE_NAME + df.format(new java.util.Date()) + FILE_EXTENSION;
			
			//Create folder
			File file = new File(PropertyReader.getPropertyValue(TARGET_FOLDER_PATH)+ "/" + filename);
			file.getParentFile().mkdir();
			
			m.marshal(pfa, file);
		} 
		catch (JAXBException e) {
			log.error("Error while marshalling");
		}
		
	}

	private static Person preparePerson() {
		Person person = new Person();
		person.setAction("add");
		person.setDate("31-May-2015");
		person.setId("1680823");
		
		Image image = new Image();
		image.setURL("http://www.cbsa.gc.ca/wc-cg/images/warcrime-crimeguerre/c003-hr.jpg");
		
		Images images = new Images();
		images.getImage().add(image);
		person.setImages(images);
		
		Source source = new Source();
		source.setName("http://www.cbsa.gc.ca/wc-cg/menu-eng.html");
		
		SourceDescription sourceDescription = new SourceDescription();
		sourceDescription.getSource().add(source);
		person.setSourceDescription(sourceDescription);
		
		CountryValue countryValue = new CountryValue();
		countryValue.setCode("NOTK");
		
		Country country = new Country();
		country.setCountryType("Citizenship");
		country.getCountryValue().add(countryValue);
		
		CountryDetails countryDetails = new CountryDetails();
		countryDetails.getCountry().add(country);
		person.setCountryDetails(countryDetails);
		
		Reference reference = new Reference();
		reference.setToDay("26");
		reference.setToMonth("Mar");
		reference.setToYear("2015");
		
		SanctionsReferences sanctionsReferences = new SanctionsReferences();
		sanctionsReferences.getReference().add(reference);
		person.setSanctionsReferences(sanctionsReferences);
		
		Place place = new Place();
		place.setName("Afghanistan");
		
		BirthPlace birthPlace = new BirthPlace();
		birthPlace.getPlace().add(place);
		person.setBirthPlace(birthPlace);
		
		DateValue dateValue = new DateValue();
		dateValue.setDay("21");
		dateValue.setMonth("Mar");
		dateValue.setYear("2015");
		
		Date date = new Date();
		date.setDateType("Date of Birth");
		date.getDateValue().add(dateValue);
		
		DateDetails dateDetails = new DateDetails();
		dateDetails.getDate().add(date);
		person.setDateDetails(dateDetails);
		
		Description description = new Description();
		description.setDescription1("1");
		description.setDescription2("2");
		
		Descriptions descriptions = new Descriptions();
		descriptions.getDescription().add(description);
		person.setDescriptions(descriptions);
		
		NameValue nameValue = new NameValue();
		nameValue.getFirstName().add("Abdullah");
		nameValue.getSurname().add("Khalid");
		
		Name name = new Name();
		name.setNameType("Primary Name");
		name.getNameValue().add(nameValue);
		
		NameDetails nameDetails = new NameDetails();
		nameDetails.getName().add(name);
		person.setNameDetails(nameDetails);
		
		return person;
		
	}

	private static Entity prepareEntity() {
		
		Entity entity = new Entity();
		entity.setAction("add");
		entity.setDate("31-May-2015");
		entity.setId("1680823");
		
		Source source = new Source();
		source.setName("http://www.cbsa.gc.ca/wc-cg/menu-eng.html");
		
		SourceDescription sourceDescription = new SourceDescription();
		sourceDescription.getSource().add(source);
		entity.setSourceDescription(sourceDescription);
		
		CountryValue countryValue = new CountryValue();
		countryValue.setCode("NOTK");
		
		Country country = new Country();
		country.setCountryType("Citizenship");
		country.getCountryValue().add(countryValue);
		
		CountryDetails countryDetails = new CountryDetails();
		countryDetails.getCountry().add(country);
		entity.setCountryDetails(countryDetails);
		
		Reference reference = new Reference();
		reference.setToDay("26");
		reference.setToMonth("Mar");
		reference.setToYear("2015");
		
		SanctionsReferences sanctionsReferences = new SanctionsReferences();
		sanctionsReferences.getReference().add(reference);
		entity.setSanctionsReferences(sanctionsReferences);
		
		Place place = new Place();
		place.setName("Afghanistan");
		
		BirthPlace birthPlace = new BirthPlace();
		birthPlace.getPlace().add(place);
		
		DateValue dateValue = new DateValue();
		dateValue.setDay("21");
		dateValue.setMonth("Mar");
		dateValue.setYear("2015");
		
		Date date = new Date();
		date.setDateType("Date of Birth");
		date.getDateValue().add(dateValue);
		
		DateDetails dateDetails = new DateDetails();
		dateDetails.getDate().add(date);
		entity.setDateDetails(dateDetails);
		
		Description description = new Description();
		description.setDescription1("1");
		description.setDescription2("2");
		
		Descriptions descriptions = new Descriptions();
		descriptions.getDescription().add(description);
		entity.setDescriptions(descriptions);
		
		NameValue nameValue = new NameValue();
		nameValue.getFirstName().add("Master");
		nameValue.getSurname().add("Card");
		
		Name name = new Name();
		name.setNameType("Primary Name");
		name.getNameValue().add(nameValue);
		
		NameDetails nameDetails = new NameDetails();
		nameDetails.getName().add(name);
		entity.setNameDetails(nameDetails);

		CompanyDetails companyDetails = new CompanyDetails();
		companyDetails.setAddressLine("Piazza Santa Barbara 7");
		companyDetails.setAddressCity("San Donato Milanese;Milan;20097");
		companyDetails.setAddressCountry("ITALY");
		companyDetails.setURL("http://www.snam.it");
		entity.getCompanyDetails().add(companyDetails);
		
		entity.setActiveStatus("Active");
		
		IDValue iDValue = new IDValue();
		iDValue.setValue("431144569");
		
		ID id = new ID();
		id.setIDType("DUNS Number");
		
		id.getIDValue().add(iDValue);
		
		IDNumberTypes iDNumberTypes = new IDNumberTypes();
		iDNumberTypes.getID().add(id);
		entity.setIDNumberTypes(iDNumberTypes);
		
		entity.setId("1046863");
		
		return entity;
		
	}

}
