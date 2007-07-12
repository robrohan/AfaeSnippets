/*
 * Created on May 7, 2004
 *
 * The MIT License
 * Copyright (c) 2004 Stephen Milligan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the "Software"), 
 * to deal in the Software without restriction, including without limitation 
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 * and/or sell copies of the Software, and to permit persons to whom the Software 
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, 
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE 
 * SOFTWARE.
 */
package com.rohanclan.snippets.core;

import java.io.File;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Text;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

/**
 * @author Stephen Milligan
 */
public class SnipReader 
{	
	protected javax.xml.parsers.DocumentBuilderFactory factory;
	protected javax.xml.parsers.DocumentBuilder builder;

	private Document document = null;
	
	private String snipDescription, snipStartBlock, snipEndBlock;
	private boolean useAsTemplate = false;
	
	protected File snippetFile;
	private String templateExtension;
    
	/**
	 * 
	 */
	public SnipReader() 
	{
		super();

		try
		{
			factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
			factory.setIgnoringComments(true);
			factory.setIgnoringElementContentWhitespace(true);
			factory.setCoalescing(true);
			builder = factory.newDocumentBuilder();	
		}
		catch(ParserConfigurationException pce)
		{
			pce.printStackTrace(System.err);
		}
	}
	
	/**
	 * 
	 * @param fileName
	 */
	public void read(String fileName) 
	{
		this.snippetFile = new File(fileName);		
		if (snippetFile.exists()) 
		{
			try 
			{
				FileInputStream fis = new FileInputStream(snippetFile);
				BufferedInputStream bis = new BufferedInputStream(fis);
				
				bis.mark(0);
				try 
				{
					document = builder.parse(bis);
					parseDocument();
				}
				catch (SAXException saxx)
				{
					saxx.printStackTrace(System.err);
					this.snipDescription = saxx.getMessage();
				}
				bis.close();
			}
			catch (IOException iox) 
			{
				iox.printStackTrace(System.err);
				this.snipDescription = iox.getMessage();
			}	
		}
	}
	
	/**
	 * 
	 *
	 */
	private void parseDocument() 
	{	
		// Make sure the document has been initialized
		if (document == null) 
		{
			return;
		}
		
		parseSnipDescription();
		parseSnipStartBlock();
		parseSnipEndBlock();
		parseSnipUseAsTemplate();
		parseTemplateExtension();
	}
	
	/**
	 * 
	 *
	 */
	private void parseSnipDescription() 
	{
		this.snipDescription = "";	
		this.snipDescription = getValue("help",0);
		
		if (this.snipDescription.length() == 0) 
		{
			NodeList nodes = document.getElementsByTagName("snippet");
			
			if (nodes.getLength() == 0) 
			{
				return;
			}
			
			Node workingNode = nodes.item(0);
			
			if (workingNode.getNodeName().equalsIgnoreCase("snippet")) 
			{
				NamedNodeMap attributes = workingNode.getAttributes();
				workingNode = attributes.getNamedItem("name");
				if (workingNode != null) 
				{
					this.snipDescription = workingNode.getNodeValue();
				}
			}
		}
	}
	
	/**
	 * 
	 *
	 */
	private void parseSnipUseAsTemplate() 
	{	
		this.useAsTemplate = false;
		
		NodeList nodes = document.getElementsByTagName("snippet");
		
		if (nodes.getLength() == 0) 
		{
			return;
		}
		
		Node workingNode = nodes.item(0);
		
		if (workingNode.getNodeName().equalsIgnoreCase("snippet")) 
		{
			NamedNodeMap attributes = workingNode.getAttributes();
			workingNode = attributes.getNamedItem("filetemplate");
			if (workingNode != null) 
			{
				String isFileTemplate = workingNode.getNodeValue();
				this.useAsTemplate = "true".equalsIgnoreCase(isFileTemplate);
			}
		}
	}
	
	/**
	 * 
	 *
	 */
	private void parseTemplateExtension() 
	{	
		this.templateExtension = "cfm";
		
		NodeList nodes = document.getElementsByTagName("snippet");
		
		if (nodes.getLength() == 0) 
		{
			return;
		}
		
		Node workingNode = nodes.item(0);
		
		if (workingNode.getNodeName().equalsIgnoreCase("snippet")) 
		{
			NamedNodeMap attributes = workingNode.getAttributes();
			workingNode = attributes.getNamedItem("extension");
			if (workingNode != null) 
			{
				templateExtension = workingNode.getNodeValue();
			}
		}
	}
	
	/**
	 * 
	 *
	 */
	private void parseSnipStartBlock() 
	{	
		this.snipStartBlock = getValue("starttext",0);
		
		if (this.snipStartBlock.length() == 0) 
		{
			this.snipStartBlock = getValue("insertText",0);
		}
	}
	
	/**
	 * 
	 *
	 */
	private void parseSnipEndBlock() 
	{	
		this.snipEndBlock = getValue("endtext",0);
		
		if (this.snipEndBlock.length() == 0) 
		{
			this.snipEndBlock = getValue("insertText",1);
		}
	}
	
	/**
	 * 
	 * @param key
	 * @param iteration
	 * @return
	 */
	private String getValue(String key, int iteration) 
	{	
		short nodetype = 0;
			
		try
		{
			nodetype = document.getElementsByTagName(key).item(iteration).getFirstChild().getNodeType();
		}
		catch (Exception e) 
		{
			return "";
		}
			
		String val = null;
			
		if(nodetype == Document.TEXT_NODE)
		{
			val = ((Text)document.getElementsByTagName(key).item(iteration).getFirstChild()).getData();	
		}
		else if(nodetype == Document.CDATA_SECTION_NODE)
		{
			CDATASection cds = (CDATASection)document.getElementsByTagName(key).item(iteration).getFirstChild();
			val = cds.getNodeValue();
			if (val.endsWith(" ")) 
			{
			    val = val.substring(0,val.length()-1);
			}
		}
		else 
		{
			val = "";
		}
		return val; 
	}
	
	public String getSnipDescription() {
		if (this.snipDescription == null) {
			this.snipDescription = "";
		}
		return this.snipDescription;
	}
	
	public String getSnipStartBlock() {
		if (this.snipStartBlock == null || this.snipStartBlock.trim().length() == 0) {
			this.snipStartBlock = "";
		}		
		return this.snipStartBlock;
	}
	
	public String getSnipEndBlock() {
		if (this.snipEndBlock == null || this.snipEndBlock.trim().length() == 0) {
			this.snipEndBlock = "";
		}
		return this.snipEndBlock;
	}
	
	public boolean isFileTemplate() {
		return this.useAsTemplate;
	}

    /**
     * @return
     */
    public String getTemplateExtension() {
        return this.templateExtension;
    }
}
