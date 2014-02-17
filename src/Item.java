import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;

public class Item {

	private static Namespace nameSpace = Namespace.getNamespace("w","http://www.cs.au.dk/dWebTek/2014");
	private static String shopKey = "830483C3F6D1322EA1F772C6";
	private static SAXBuilder builder = new SAXBuilder();
	
	
	public static void main(String[] args) throws Exception {
		File file = new File(args[0]);
		Document input = (Document) builder.build(file);
		String cIResult = createItem(input);
		modifyItem(cIResult, input);
		
	}
		
	public static String createItem(Document d) throws Exception {
		
		// Generate XML
		Element inputRoot = d.getRootElement();
		Document document = new Document();
		Element root = new Element("createItem", nameSpace);
		Element itemName = new Element("itemName", nameSpace);
		itemName.addContent(inputRoot.getChildText("itemName", nameSpace));
		root.addContent(itemName);
		root.addContent(new Element("shopKey", nameSpace).addContent(shopKey));
		document.setRootElement(root);
		System.out.println(root);
		new XMLOutputter().output(document, System.out);

		// Connect to Server
		Document responseDoc = urlConnection("http://services.brics.dk/java4/cloud/createItem", document, "POST");

		// Get desired element
		Element responseRoot = responseDoc.getRootElement();

		return responseRoot.getText();
	
	}

	public static void modifyItem(String itemID, Document d) throws JDOMException,
			IOException {

		Element xmlRoot = d.getRootElement();
		String inputRoot = itemID;
		
		Document document = new Document();
		Element root = new Element("modifyItem", nameSpace);
		document.setRootElement(root);
		root.addContent(new Element("itemID", nameSpace).addContent(inputRoot));
		root.addContent(new Element("shopKey", nameSpace).addContent(shopKey));
		root.addContent(new Element("itemName", nameSpace).addContent(xmlRoot
				.getChildText("itemName", nameSpace)));
		
		
		root.addContent(new Element("itemPrice", nameSpace).addContent(xmlRoot
				.getChildText("itemPrice", nameSpace)));
		
		root.addContent(xmlRoot.getChild("itemDescription", nameSpace).clone());
		
		System.out.println(xmlRoot.getChild("itemDescription", nameSpace));
		root.addContent(new Element("itemURL", nameSpace).addContent(xmlRoot
				.getChildText("itemURL", nameSpace)));

		new XMLOutputter().output(document, System.out);
		
		
		URL url = new URL("http://services.brics.dk/java4/cloud/modifyItem");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setDoInput(true);
		con.connect();
		new XMLOutputter().output(root, con.getOutputStream());

		int responseCode = con.getResponseCode();
		System.out.println(responseCode); 
		
		con.disconnect();
		

	}
	
	public static Document urlConnection(String urlRequest, Document doc, String type) throws Exception {
		URL url = new URL(urlRequest);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		
		con.setDoOutput(true);
		con.setDoInput(true);
		con.connect();
		new XMLOutputter().output(doc, con.getOutputStream());
		
		InputStream response = con.getInputStream();
		
		Document responseDoc = builder.build(response);
		System.out.println(con.getResponseCode());
		con.disconnect();
		return responseDoc;
	}
}
