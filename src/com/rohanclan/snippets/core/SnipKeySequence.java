/*
 * Created on Jul 12, 2004
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

import java.util.Properties;
import java.util.Enumeration;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.core.runtime.Path;

import com.rohanclan.snippets.PropertyManager;

/**
 * @author Stephen Milligan
 */
public class SnipKeySequence {
	private Properties keyCombos = new Properties();
	private String keyComboFilePath = "";
	private String snippetFilePath = "";
	private static String HEADER_TEXT = "This is the key sequence index file for snippets";
	private PropertyManager propertyManager;

	public SnipKeySequence() {
		propertyManager = new PropertyManager();

		// This ensures that we are notified when the properties are saved
		// SnippetsPlugin.getDefault().getPropertyStore().addPropertyChangeListener(this);

		this.keyComboFilePath = new Path(propertyManager.snippetsPath()
				.replace("file:", "")).toOSString()
				+ "/SequenceIndex.properties";

		// System.out.print(this.keyComboFilePath);

		// this.keyComboFilePath =
		// SnippetsPlugin.getDefault().getStateLocation().toString() +
		// "/SequenceIndex.properties";

		loadKeySequences();
	}

	/**
	 * Load the key combination index file
	 */
	private void loadKeySequences() {
		File f = new File(this.keyComboFilePath);
		// the index file doesn't exist try to make it
		if (!f.exists()) {
			try {
				f.createNewFile();
				FileOutputStream output = new FileOutputStream(f);
				keyCombos.store(output, HEADER_TEXT);
				output.flush();
				output.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		try {
			FileInputStream input = new FileInputStream(f);
			this.keyCombos.load(input);
			input.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sets the index between a sequence and a snippet
	 * 
	 * @param sequence
	 * @param snippetFile
	 */
	public void setKeySequence(String sequence, String snippetFile) {
		snippetFile = snippetFile.replace("file:", "");
		this.keyCombos.setProperty(sequence, snippetFile);

		try {
			FileOutputStream output = new FileOutputStream(
					this.keyComboFilePath);
			keyCombos.store(output, HEADER_TEXT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the snippet path based on the key sequence
	 * 
	 * @param sequence
	 * @return
	 */
	public String getKeySequence(String sequence) {
		return new Path(this.propertyManager.snippetsPath()
				.replace("file:", "")).toOSString()
				+ this.keyCombos.getProperty(sequence);
	}

	/**
	 * 
	 * @param sequence
	 */
	public void clearKeySequence(String sequence) {
		this.keyCombos.remove(sequence);
	}

	/**
	 * 
	 * @return
	 */
	public String getSnippetFolder() {
		return this.snippetFilePath;
	}

	/**
	 * 
	 * @param fileName
	 * @return
	 */
	public String getSequenceFullPath(String fileName) {
		return getSequence(getRelativeFromFullPath(fileName));
	}

	/**
	 * Takes a full path, and removes the directory offset. This is useful if
	 * you only know the full path to the snippet file.
	 * 
	 * @param path
	 * @return
	 */
	public String getRelativeFromFullPath(String path) {
		String snippath = new Path(propertyManager.snippetsPath().replace(
				"file:", "")).toOSString();
		path = path.replace(snippath, "");
		return path;
	}

	/**
	 * 
	 * @param fileName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getSequence(String fileName) {
		if (this.keyCombos.containsValue(fileName)) {
			Enumeration e = this.keyCombos.propertyNames();
			String sequence;

			while (e.hasMoreElements()) {
				sequence = e.nextElement().toString();

				if (this.keyCombos.getProperty(sequence).equals(fileName)) {
					return sequence;
				}
			}
		}

		return null;
	}
}
