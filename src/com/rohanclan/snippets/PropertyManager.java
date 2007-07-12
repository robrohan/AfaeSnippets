/*
 * Created on Apr 29, 2004
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
package com.rohanclan.snippets;


import org.eclipse.jface.preference.PreferenceStore;
import java.io.IOException;

/**
 * @author Stephen Milligan
 *
 * This controls the properies for the per project settings
 */
public class PropertyManager 
{
	/**
	 * 
	 */	
	private PreferenceStore store;
	private PreferenceManager preferenceManager;
	
	public PropertyManager() 
	{
		super();
		
		this.store = SnippetsPlugin.getDefault().getPropertyStore();
		
		try
		{
			store.load();
		}
		catch(Exception e) 
		{
			//System.err.println("CFMLPropertyManager::CFMLPropertyManager() - Couldn't load property store");
			//e.printStackTrace();
		} 
		this.preferenceManager = new PreferenceManager();
	}
	
	/*
	 * 
	 */
	public void initializeDefaultValues() 
	{
		store.setDefault(
			PreferenceManager.P_SNIPPETS_PATH, preferenceManager.defaultSnippetsPath()
        	);
		
		//System.err.println(store.getDefaultString(PreferenceManager.P_SNIPPETS_PATH));
		
        /* store.setDefault(
        		PreferenceConstants.P_PROJECT_URL, preferenceManager.defaultProjectURL()
        	); */
	}
	
	/**
	 * 
	 * @return
	 */
	public String snippetsPath() 
	{
		return store.getString(PreferenceManager.P_SNIPPETS_PATH).trim();
	}
	
	/**
	 * 
	 * @return
	 */
	public String defaultSnippetsPath() 
	{
		return preferenceManager.snippetsPath();
	}
	
	/**
	 * 
	 * @param path
	 */
	public void setSnippetsPath(String path) 
	{
		store.setValue(PreferenceManager.P_SNIPPETS_PATH,path);
		
		try 
		{
			store.save();
		}
		catch (IOException e) 
		{
			System.err.println("Failed to save property store " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @return
	 */
	/* public String projectURL() 
	{
		return store.getString(PreferenceManager.P_PROJECT_URL).trim();
	} */
	
	/**
	 * 
	 * @return
	 */
	/* public String defaultProjectURL() 
	{
		return preferenceManager.defaultProjectURL();
	} */
	
	/**
	 * 
	 * @param path
	 */
	/* public void setProjectURL(String path) 
	{
		store.setValue(PreferenceManager.P_PROJECT_URL,path);
		
		try 
		{
			store.save();
		}
		catch(IOException e) 
		{
			System.err.println("Failed to save property store " + e.getMessage());
		}
	} */
}
