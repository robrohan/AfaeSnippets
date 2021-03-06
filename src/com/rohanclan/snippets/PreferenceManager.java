/*
 * Created on Apr 22, 2004
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

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
//import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.RGB;

/**
 * @author Stephen Milligan
 */
public class PreferenceManager 
{
	/** Preference key identifier for path to the directory that contains your snippets */
	public static final String P_SNIPPETS_PATH 			= "snippetPath";
	
	private IPreferenceStore store;
	
	/**
	 * 
	 *
	 */
	public PreferenceManager() 
	{
		store = SnippetsPlugin.getDefault().getPreferenceStore();
	}

	/**
	 * 
	 * @param prefKey
	 * @return
	 */
	public boolean getBooleanPref(String prefKey) 
	{
		return store.getBoolean(prefKey);
	}
	
	/**
	 * 
	 * @param color
	 * @return
	 */
	/* private String getColorString(RGB color) 
	{
	    return color.red + "," + color.green + "," + color.blue;
	} */
	
	/**
	 * 
	 *
	 */
	public void init() 
	{
		//AutoIndentPreferenceConstants.setDefaults(store);
		//CFMLColorsPreferenceConstants.setDefaults(store);
		//PreferenceConstants.setDefaults(store);
		//EditorPreferenceConstants.setDefaults(store);
		//FoldingPreferenceConstants.setDefaults(store);
		//HTMLColorsPreferenceConstants.setDefaults(store);
		//CFMLColorsPreferenceConstants.setDefaults(store);
		//ParserPreferenceConstants.setDefaults(store);
		//ScribblePadPreferenceConstants.setDefaults(store);
	}
	
	/**
	 * Gets an RGB from the preference store using key as the key. If the key
	 * does not exist, it returns 0,0,0
	 * @param key
	 * @return
	 */
	public RGB getColor(String key)
	{
		//try to get the color as a string from the store
		String rgbString = store.getString(key);
		//System.err.println(key + " :: " + rgbString);
		
		//if we didnt get anything back...
		if(rgbString.length() <= 0)
		{
			//try to get it from the default settings
			rgbString = store.getDefaultString(key);
			
			//if we still didnt get anything use black
			if(rgbString.length() <= 0)
			{
				// Force a stack trace to see what called this.
				try {
					rgbString = null;
					//System.out.println(rgbString.length());
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				//System.err.println("Color key: " + key + " is a no show using black");
				rgbString = "0,0,0";
			}
		}
		
		//make sure we get an ok string
		rgbString = deParen(rgbString);
		
		RGB newcolor = null;
		try
		{
			newcolor = StringConverter.asRGB(deParen(rgbString));
		}
		catch(Exception e)
		{
			//System.err.println("Woah... got an odd color passed: " + key);
			e.printStackTrace(System.err);
		}
		
		return newcolor;
	}
	
	/**
	 * for some reason the color can get stored as  {RGB 12, 1, 1} and the rbg maker
	 * thingy expects them in 12,1,1, format so this cleans up the string a bit
	 * @param item
	 * @return
	 */
	private String deParen(String item)
	{
		String d = item.replace('{',' ').replace('}',' '); 
		d = d.replaceAll("[RGB ]","").trim();
		return d;
	}
	
	/**
	 * 
	 * @return
	 */
	public int tabWidth() 
	{
	    //System.out.println("Tab width retrieved as: " + Integer.parseInt(store.getString(ICFMLPreferenceConstants.P_TAB_WIDTH).trim()));
		//return Integer.parseInt(store.getString(EditorPreferenceConstants.P_TAB_WIDTH).trim());
		return 4;
	}
	
	/**
	 * 
	 * @return
	 */
	public int defaultTabWidth() 
	{
		//return store.getDefaultInt(EditorPreferenceConstants.P_TAB_WIDTH);
		return 4;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean insertSpacesForTabs() 
	{
		//return store.getString(EditorPreferenceConstants.P_INSERT_SPACES_FOR_TABS).trim().equalsIgnoreCase("true");
		return false;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean defaultSpacesForTabs() 
	{
		//return store.getDefaultBoolean(EditorPreferenceConstants.P_INSERT_SPACES_FOR_TABS);
		return false;
	}
	
	/**
	 * 
	 * @return
	 
	public int insightDelay() 
	{
		//return Integer.parseInt(store.getString(EditorPreferenceConstants.P_INSIGHT_DELAY).trim());
		return 500;
	}*/
	
	/* public int defaultInsightDelay() {
		return store.getDefaultInt(EditorPreferenceConstants.P_INSIGHT_DELAY);
	} */
	
	/* 
	public boolean tabIndentSingleLine() {
		return store.getString(EditorPreferenceConstants.P_TAB_INDENTS_CURRENT_LINE).trim().equalsIgnoreCase("true");
	} */
	
	 
	public String snippetsPath() 
	{
		return store.getString(PreferenceManager.P_SNIPPETS_PATH).trim();
	}
	
	
	/*
	public int maxUndoSteps() {
		return Integer.parseInt(store.getString(EditorPreferenceConstants.P_MAX_UNDO_STEPS).trim());
	}
	*/
	
	public String defaultSnippetsPath() 
	{
		try
		{
			String snipPath = "snippets/";
			//URL installURL = AfaePlugin.getDefault().getBundle().getEntry("/");
			//URL url = new URL(installURL, snipPath);
						
			URL iconBaseURL = new URL(
				SnippetsPlugin.getDefault().getBundle().getEntry("/"),
				snipPath
			);
			//URL local = Platform.asLocalURL(iconBaseURL);
			URL local = FileLocator.toFileURL(iconBaseURL);
			
			return local.toString();
		}
		catch(Exception e)
		{
			return SnippetsPlugin.getDefault().getStateLocation().toString();
		}
	}
	
	/* 
	public String defaultProjectURL() {
		return store.getDefaultString(PreferenceConstants.P_PROJECT_URL);
	}
	*/
	/*
	public boolean defaultTabbedBrowser() {
	    return store.getDefaultBoolean(PreferenceConstants.P_TABBED_BROWSER);
	}
	
	public boolean tabbedBrowser() {
	    return store.getBoolean(PreferenceConstants.P_TABBED_BROWSER);
	}
*/
	/*
	public boolean enableFolding() {
		return store.getBoolean(FoldingPreferenceConstants.P_ENABLE_CODE_FOLDING);
	}
	
	public int minimumFoldingLines() {
		return store.getInt(FoldingPreferenceConstants.P_MINIMUM_CODE_FOLDING_LINES);
	}


	public boolean foldCFMLComments() {
		return store.getBoolean(FoldingPreferenceConstants.P_FOLDING_CFMLCOMMENTS_FOLD);
	}
	
	public boolean collapseCFMLComments() {
		return store.getBoolean(FoldingPreferenceConstants.P_FOLDING_CFMLCOMMENTS_COLLAPSE);
	}


	public boolean foldHTMLComments() {
		return store.getBoolean(FoldingPreferenceConstants.P_FOLDING_HTMLCOMMENTS_FOLD);
	}
	
	public boolean collapseHTMLComments() {
		return store.getBoolean(FoldingPreferenceConstants.P_FOLDING_HTMLCOMMENTS_COLLAPSE);
	}

	public boolean foldTag(int tagNumber) {
	    boolean val = false;
		switch (tagNumber) {
			case 1: val = store.getBoolean(FoldingPreferenceConstants.P_FOLDING_TAG1_FOLD); 
			break;
			case 2: val = store.getBoolean(FoldingPreferenceConstants.P_FOLDING_TAG2_FOLD); 
			break; 
			case 3: val = store.getBoolean(FoldingPreferenceConstants.P_FOLDING_TAG3_FOLD); 
			break;
			case 4: val = store.getBoolean(FoldingPreferenceConstants.P_FOLDING_TAG4_FOLD); 
			break;
			case 5: val = store.getBoolean(FoldingPreferenceConstants.P_FOLDING_TAG5_FOLD); 
			break;
			case 6: val = store.getBoolean(FoldingPreferenceConstants.P_FOLDING_TAG6_FOLD); 
			break;
			case 7: val = store.getBoolean(FoldingPreferenceConstants.P_FOLDING_TAG7_FOLD); 
			break;
			case 8: val = store.getBoolean(FoldingPreferenceConstants.P_FOLDING_TAG8_FOLD); 
			break;
		}
	    return val;
	}

	public boolean collapseTag(int tagNumber) {
	    boolean val = false;
		switch (tagNumber) {
			case 1: val = store.getBoolean(FoldingPreferenceConstants.P_FOLDING_TAG1_COLLAPSE);
			break;
			case 2: val = store.getBoolean(FoldingPreferenceConstants.P_FOLDING_TAG2_COLLAPSE);
			break;
			case 3: val = store.getBoolean(FoldingPreferenceConstants.P_FOLDING_TAG3_COLLAPSE);
			break;
			case 4: val = store.getBoolean(FoldingPreferenceConstants.P_FOLDING_TAG4_COLLAPSE);
			break;
			case 5: val = store.getBoolean(FoldingPreferenceConstants.P_FOLDING_TAG5_COLLAPSE);
			break;
			case 6: val = store.getBoolean(FoldingPreferenceConstants.P_FOLDING_TAG6_COLLAPSE);
			break;
			case 7: val = store.getBoolean(FoldingPreferenceConstants.P_FOLDING_TAG7_COLLAPSE);
			break;
			case 8: val = store.getBoolean(FoldingPreferenceConstants.P_FOLDING_TAG8_COLLAPSE);
			break;
		}
	    return val;
	}

	public String foldingTagName(int tagNumber) {
	    //System.out.println("TEST : " + store.getString(ICFMLPreferenceConstants.P_FOLDING_TAG1_NAME));
	    String val = "";
		switch (tagNumber) {
			case 1: val = store.getString(FoldingPreferenceConstants.P_FOLDING_TAG1_NAME);
			break;
			case 2: val = store.getString(FoldingPreferenceConstants.P_FOLDING_TAG2_NAME);
			break;
			case 3: val = store.getString(FoldingPreferenceConstants.P_FOLDING_TAG3_NAME);
			break;
			case 4: val = store.getString(FoldingPreferenceConstants.P_FOLDING_TAG4_NAME);
			break;
			case 5: val = store.getString(FoldingPreferenceConstants.P_FOLDING_TAG5_NAME);
			break;
			case 6: val = store.getString(FoldingPreferenceConstants.P_FOLDING_TAG6_NAME);
			break;
			case 7: val = store.getString(FoldingPreferenceConstants.P_FOLDING_TAG7_NAME);
			break;
			case 8: val = store.getString(FoldingPreferenceConstants.P_FOLDING_TAG8_NAME);
			break;
		}
	    return val;
	}
	
	
	
	public boolean useFunkyContentAssist() {
		return true;
		//return true;
	}
	*/
}
