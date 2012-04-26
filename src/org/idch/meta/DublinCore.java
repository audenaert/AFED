/**
 * 
 */
package org.idch.meta;

/**
 * A simple implementation of the classic 15 element Dublin Core metadata scheme.  
 * 
 * @author Neal Audenaert
 */
public class DublinCore {
    
    private String contributor;
    private String coverage;
    private String creator;
    private String date;
    private String description; 
    private String format;
    private String identifier;
    private String language;
    private String publisher;
    private String relation;
    private String rights;
    private String source;
    private String subject;
    private String title;
    private String type;
    
    /**
     * An entity responsible for making contributions to the resource. Examples of a 
     * Contributor include a person, an organization, or a service. Typically, the name of 
     * a Contributor should be used to indicate the entity.
     * 
     * @return the contributor
     */
    public String getContributor() {
        return contributor;
    }
    
    /**
     * Sets the contributor.
     * 
     * @param contributor the contributor to set
     */
    public void setContributor(String contributor) {
        this.contributor = contributor;
    }
    
    /**
     * The spatial or temporal topic of the resource, the spatial applicability of the 
     * resource, or the jurisdiction under which the resource is relevant.
     * 
     * <p>Spatial topic and spatial applicability may be a named place or a location 
     * specified by its geographic coordinates. Temporal topic may be a named period,
     * date, or date range. A jurisdiction may be a named administrative entity or a 
     * geographic place to which the resource applies. Recommended best practice is to 
     * use a controlled vocabulary such as the Thesaurus of Geographic Names [TGN]. 
     * Where appropriate, named places or time periods can be used in preference to
     * numeric identifiers such as sets of coordinates or date ranges.
     * 
     * @return the coverage
     */
    public String getCoverage() {
        return coverage;
    }
    
    /**
     * Sets the coverage.
     * 
     * @param coverage the coverage to set
     */
    public void setCoverage(String coverage) {
        this.coverage = coverage;
    }
    
    /**
     * An entity primarily responsible for making the resource. Examples of a Creator include 
     * a person, an organization, or a service. Typically, the name of a Creator should be 
     * used to indicate the entity.
     * 
     * @return the creator
     */
    public String getCreator() {
        return creator;
    }
    
    /**
     * Sets the creator of the resource.
     * 
     * @param creator the creator to set
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }
    
    /**
     * A point or period of time associated with an event in the lifecycle of the resource.
     * Date may be used to express temporal information at any level of granularity. 
     * Recommended best practice is to use an encoding scheme, such as the W3CDTF profile of 
     * ISO 8601 [W3CDTF].
     * 
     * @return the date
     * @see {@link http://www.w3.org/TR/NOTE-datetime}
     */
    public String getDate() {
        return date;
    }
    
    /**
     * Sets a point or period of time associate with an event in the lifecycle of this 
     * resource.
     * 
     * @param date the date to set
     */
    public void setDate(String date) {
        this.date = date;
    }
    
    /**
     * An account of the resource. Description may include but is not limited to: an abstract,
     * a table of contents, a graphical representation, or a free-text account of the resource.
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Sets a description of the resource.
     * 
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * The file format, physical medium, or dimensions of the resource. Examples of 
     * dimensions include size and duration. Recommended best practice is to use a controlled 
     * vocabulary such as the list of Internet Media Types [MIME].
     * 
     * @return the format
     */
    public String getFormat() {
        return format;
    }
    
    /**
     * Sets the format, physical medium, or dimensions of the resource.
     * 
     * @param format the format to set
     */
    public void setFormat(String format) {
        this.format = format;
    }
    
    /**
     * An unambiguous reference to the resource within a given context. Recommended best 
     * practice is to identify the resource by means of a string conforming to a formal
     * identification system.
     * 
     * @return the identifier
     */
    public String getIdentifier() {
        return identifier;
    }
    
    /**
     * Sets the identifier for the resource.
     * 
     * @param identifier the identifier to set
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
    
    /**
     * A language of the resource. Recommended best practice is to use a controlled vocabulary
     * such as RFC 4646 [RFC4646].
     * 
     * @return the language
     */
    public String getLanguage() {
        return language;
    }
    
    /**
     * Sets the language of the resource.
     * 
     * @param language the language to set
     */
    public void setLanguage(String language) {
        this.language = language;
    }
    
    /**
     * An entity responsible for making the resource available. Examples of a Publisher 
     * include a person, an organization, or a service. Typically, the name of a Publisher 
     * should be used to indicate the entity.
     * 
     * @return the publisher
     */
    public String getPublisher() {
        return publisher;
    }
    
    /**
     * Sets the publisher of a resource. 
     * 
     * @param publisher the publisher to set
     */
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
    
    /**
     * A related resource. Recommended best practice is to identify the related resource by 
     * means of a string conforming to a formal identification system.
     * 
     * @return the relation
     */
    public String getRelation() {
        return relation;
    }
    
    /**
     * Sets a related resource.
     * 
     * @param relation the relation to set
     */
    public void setRelation(String relation) {
        this.relation = relation;
    }
    
    /**
     * Information about rights held in and over the resource. Typically, rights information 
     * includes a statement about various property rights associated with the resource,
     * including intellectual property rights.
     * 
     * @return the rights
     */
    public String getRights() {
        return rights;
    }
    
    /**
     * Sets the information about rights held in and over the resource.
     * 
     * @param rights the rights to set
     */
    public void setRights(String rights) {
        this.rights = rights;
    }
    
    /**
     * A related resource from which the described resource is derived. The described 
     * resource may be derived from the related resource in whole or in part. Recommended best 
     * practice is to identify the related resource by means of a string conforming to a 
     * formal identification system.
     * 
     * @return the source
     */
    public String getSource() {
        return source;
    }
    
    /**
     * Sets the source of the resource.
     * 
     * @param source the source to set
     */
    public void setSource(String source) {
        this.source = source;
    }
    
    /**
     * The topic of the resource. Typically, the subject will be represented using 
     * keywords, key phrases, or classification codes. Recommended best practice is to use 
     * a controlled vocabulary. To describe the spatial or temporal topic of the 
     * resource, use the Coverage element.
     * 
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }
    
    /**
     *
     * 
     * @param subject the subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    /**
     * A name given to the resource.
     * @return the title
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Sets the tile of the resource.
     * 
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
    /**
     * The nature or genre of the resource. Recommended best practice is to use a controlled 
     * vocabulary such as the DCMI Type Vocabulary [DCMITYPE]. To describe the file format, 
     * physical medium, or dimensions of the resource, use the Format element.
     * 
     * @return the type
     */
    public String getType() {
        return type;
    }
    
    /**
     * Sets the type of the resource.
     * 
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

}
