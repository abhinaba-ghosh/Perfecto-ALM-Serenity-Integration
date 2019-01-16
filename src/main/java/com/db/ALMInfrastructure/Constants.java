package com.db.ALMInfrastructure;

import java.util.Properties;

public class Constants {
	
	private Constants() {}
	
	static Properties props = Helper.readPropertiesFromFile(Helper.getCurrentDirectory()+"//config.properties");
	//initialize variables
    public static final String HOST = props.getProperty("alm_host");
    public static final String PORT = props.getProperty("alm_port");

    public static final String USERNAME = props.getProperty("alm_login_user_name");
    public static final String PASSWORD = props.getProperty("alm_login_user_pwd");

    public static final String DOMAIN = props.getProperty("alm_domain");
    public static final String PROJECT = props.getProperty("alm_project");
    
    public static final String SECURE_PROXY_HOST=props.getProperty("alm_secure_proxy_host");
    public static final int SECURE_PROXY_PORT=Integer.parseInt((props.getProperty("alm_secure_proxy_port")));
	

   /**
     * Supports running tests correctly on both versioned
     * and non-versioned projects.
     * @return true if entities of entityType support versioning
     */
    public static boolean isVersioned(String entityType,
        final String domain, final String project)
        throws Exception {

        RestConnector con = RestConnector.getInstance();
        String descriptorUrl =
            con.buildUrl("rest/domains/"
                 + domain
                 + "/projects/"
                 + project
                 + "/customization/entities/"
                 + entityType);

        String descriptorXml =
           con.httpGet(descriptorUrl, null, null).toString();
        EntityDescriptor descriptor =
                EntityMarshallingUtils.marshal
                    (EntityDescriptor.class, descriptorXml);

        boolean isVersioned = descriptor.getSupportsVC().getValue();

        return isVersioned;
    }

    public static String generateFieldXml(String field, String value) {
        return "<Field Name=\"" + field
           + "\"><Value>" + value
           + "</Value></Field>";
    }

    /**
     * This string used to create new "requirement" type entities.
     */
    public static final String entityToPostName = "req"
        + Double.toHexString(Math.random());
    public static final String entityToPostFieldName =
        "type-id";
    public static final String entityToPostFieldValue = "1";
    public static final String entityToPostFormat =
        "<Entity Type=\"requirement\">"
                + "<Fields>"
                + Constants.generateFieldXml("%s", "%s")
                + Constants.generateFieldXml("%s", "%s")
                + "</Fields>"
                + "</Entity>";
    public static final String appname="120219-1   RDS OTC";
//    public static final String entityToPostFormat="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Entity Type=\"defect\"> <ChildrenCount> <Value>0</Value> </ChildrenCount> <Fields> <Field Name=\"description\"> <Value>AbhiAutoTest</Value> </Field> <Field Name=\"name\"> <Value>Abhi Auto Test</Value> </Field> <Field Name=\"user-template-26\"> <Value>Defect</Value> </Field> <Field Name=\"user-template-01\"> <Value>"+appname+"</Value> </Field> <Field Name=\"owner\"> <Value>abhinaba.ghosh</Value> </Field> <Field Name=\"status\"> <Value>Closed</Value> </Field> <Field Name=\"priority\"> <Value>P4 - Low</Value> </Field> <Field Name=\"severity\"> <Value>S4 - Minor</Value> </Field> <Field Name=\"user-template-21\"> <Value>N</Value> </Field> <Field Name=\"user-template-17\"> <Value>N</Value> </Field> <Field Name=\"detected-by\"> <Value>abhinaba.ghosh</Value> </Field> <Field Name=\"user-template-02\"> <Value>2018-08-24</Value> </Field> <Field Name=\"detected-in-rel\"> <Value></Value> </Field> <Field Name=\"user-template-09\"> <Value>Smoke Testing</Value> </Field> <Field Name=\"user-template-23\"> <Value></Value> </Field> <Field Name=\"user-template-22\"> <Value>Internal</Value> </Field> <Field Name=\"creation-time\"> <Value>2018-08-24</Value> </Field> </Fields> <RelatedEntities/> </Entity>";
    public static final String entityToPostXml =
        String.format(
                entityToPostFormat,
                "name",
                entityToPostName,
                entityToPostFieldName,
                entityToPostFieldValue);

    public static final CharSequence entityToPostFieldXml =
        generateFieldXml(Constants.entityToPostFieldName,
        Constants.entityToPostFieldValue);


}
