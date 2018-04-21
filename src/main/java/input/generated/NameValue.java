//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2017.09.30 at 03:32:57 PM IST 
//


package input.generated;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}TitleHonorific" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}FirstName" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}MiddleName" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}Surname" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}MaidenName" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}Suffix" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}EntityName" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}SingleStringName" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{}OriginalScriptName" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "titleHonorific",
    "firstName",
    "middleName",
    "surname",
    "maidenName",
    "suffix",
    "entityName",
    "singleStringName",
    "originalScriptName"
})
@XmlRootElement(name = "NameValue")
public class NameValue {

    @XmlElement(name = "TitleHonorific")
    protected List<String> titleHonorific;
    @XmlElement(name = "FirstName")
    protected List<String> firstName;
    @XmlElement(name = "MiddleName")
    protected List<String> middleName;
    @XmlElement(name = "Surname")
    protected List<String> surname;
    @XmlElement(name = "MaidenName")
    protected List<String> maidenName;
    @XmlElement(name = "Suffix")
    protected List<String> suffix;
    @XmlElement(name = "EntityName")
    protected List<String> entityName;
    @XmlElement(name = "SingleStringName")
    protected List<String> singleStringName;
    @XmlElement(name = "OriginalScriptName")
    protected List<String> originalScriptName;

    /**
     * Gets the value of the titleHonorific property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the titleHonorific property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTitleHonorific().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getTitleHonorific() {
        if (titleHonorific == null) {
            titleHonorific = new ArrayList<String>();
        }
        return this.titleHonorific;
    }

    /**
     * Gets the value of the firstName property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the firstName property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFirstName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getFirstName() {
        if (firstName == null) {
            firstName = new ArrayList<String>();
        }
        return this.firstName;
    }

    /**
     * Gets the value of the middleName property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the middleName property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMiddleName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getMiddleName() {
        if (middleName == null) {
            middleName = new ArrayList<String>();
        }
        return this.middleName;
    }

    /**
     * Gets the value of the surname property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the surname property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSurname().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSurname() {
        if (surname == null) {
            surname = new ArrayList<String>();
        }
        return this.surname;
    }

    /**
     * Gets the value of the maidenName property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the maidenName property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMaidenName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getMaidenName() {
        if (maidenName == null) {
            maidenName = new ArrayList<String>();
        }
        return this.maidenName;
    }

    /**
     * Gets the value of the suffix property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the suffix property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSuffix().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSuffix() {
        if (suffix == null) {
            suffix = new ArrayList<String>();
        }
        return this.suffix;
    }

    /**
     * Gets the value of the entityName property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the entityName property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getEntityName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getEntityName() {
        if (entityName == null) {
            entityName = new ArrayList<String>();
        }
        return this.entityName;
    }

    /**
     * Gets the value of the singleStringName property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the singleStringName property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSingleStringName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getSingleStringName() {
        if (singleStringName == null) {
            singleStringName = new ArrayList<String>();
        }
        return this.singleStringName;
    }

    /**
     * Gets the value of the originalScriptName property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the originalScriptName property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOriginalScriptName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getOriginalScriptName() {
        if (originalScriptName == null) {
            originalScriptName = new ArrayList<String>();
        }
        return this.originalScriptName;
    }

}