/*
 * Created on May 6, 2004
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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
//import org.eclipse.swt.events.SelectionEvent;
//import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.*;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Color;

/**
 * @author Stephen Milligan
 */
public class SnipFileDialog extends Dialog 
{
	private static String dialogTitle = "New Snippet";
	private static String snippetNameLabel = "Snippet name: ";
	private static String snippetKeyComboLabel = "Trigger text: ";
	private static String snippetDescriptionLabel = "Snippet description: ";
	private static String snippetCodeStartLabel = "Snippet starting block: ";
	private static String snippetCodeEndLabel = "Snippet closing block: ";
	private TreeViewer treeView;
	private Text snippetNameText, snippetDescriptionText, snippetStartText, snippetEndText, snippetKeyComboText;
	private String snippetNameValue = "";
	private String snippetKeyComboValue = "";
	private String snippetDescriptionValue = "";
	private String snippetStartValue = "";
	private String snippetEndValue = "";
	private SnipWriter writer;
	
	/**
	 * Ok button widget.
	 */
	private Button okButton;

	/**
	 * Error message label widget.
	 */
	private Label errorMessageLabel;
		
	public SnipFileDialog(Shell parent, SnipWriter fileWriter, TreeViewer treeView, 
		String snippetNameInitialValue, String snippetKeyComboInitialValue, 
		String snippetDescriptionInitialValue, String startTextInitialValue, 
		String endTextInitialValue, boolean useAsTemplateInitialValue, 
		String templateExtensionInitialValue) 
	{
		super(parent);
		if(snippetNameInitialValue != null)
		{
			snippetNameValue = snippetNameInitialValue;
		}
		
		if(snippetKeyComboInitialValue != null)
		{
			snippetKeyComboValue = snippetKeyComboInitialValue;
		}
		
		if(snippetDescriptionInitialValue != null)
		{
			snippetDescriptionValue = snippetDescriptionInitialValue;
		}
		
		if(startTextInitialValue != null) 
		{
			snippetStartValue = startTextInitialValue;
		}
		
		if(endTextInitialValue != null) 
		{
			snippetEndValue = endTextInitialValue;
		}
		
		this.treeView = treeView;
		writer = fileWriter;
	}
	
	protected Control createDialogArea(Composite parent) 
	{	
		Composite composite = (Composite)super.createDialogArea(parent);

		// Snippet name text box label
		Label label = new Label(composite, SWT.WRAP);
		label.setText(snippetNameLabel);
		GridData data = new GridData(
			GridData.GRAB_HORIZONTAL |
			GridData.GRAB_VERTICAL |
			GridData.HORIZONTAL_ALIGN_FILL |
			GridData.VERTICAL_ALIGN_CENTER);
		data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
		label.setLayoutData(data);
		label.setFont(parent.getFont());
		
		
		// Snippet name text box
		snippetNameText= new Text(composite, SWT.SINGLE | SWT.BORDER);
		data = new GridData(
				GridData.GRAB_HORIZONTAL |
				GridData.HORIZONTAL_ALIGN_FILL);
		data.widthHint = convertWidthInCharsToPixels(20);
		snippetNameText.setLayoutData(data);
		snippetNameText.addModifyListener(
			new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					validateInput();
				}
			}
		);
		snippetNameText.setFont(parent.getFont());
		
		//Snippet key combo text box label
		label = new Label(composite, SWT.WRAP);
		label.setText(snippetKeyComboLabel);
		data = new GridData(
			GridData.GRAB_HORIZONTAL |
			GridData.GRAB_VERTICAL |
			GridData.HORIZONTAL_ALIGN_FILL |
			GridData.VERTICAL_ALIGN_CENTER);
		data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
		label.setLayoutData(data);
		label.setFont(parent.getFont());
		
		
		// Snippet key combo text box
		snippetKeyComboText= new Text(composite, SWT.SINGLE | SWT.BORDER);
		data = new GridData(
				GridData.GRAB_HORIZONTAL |
				GridData.HORIZONTAL_ALIGN_FILL);
		data.widthHint = convertWidthInCharsToPixels(20);
		snippetKeyComboText.setLayoutData(data);
		snippetKeyComboText.addModifyListener(
			new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					validateInput();
				}
			}
		);
		snippetKeyComboText.setFont(parent.getFont());

		//Snippet description text box label
		label = new Label(composite, SWT.WRAP);
		label.setText(snippetDescriptionLabel);
		data = new GridData(
			GridData.GRAB_HORIZONTAL |
			GridData.GRAB_VERTICAL |
			GridData.HORIZONTAL_ALIGN_FILL |
			GridData.VERTICAL_ALIGN_CENTER);
		data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
		label.setLayoutData(data);
		label.setFont(parent.getFont());
		
		
		// Snippet description text box
		snippetDescriptionText= new Text(composite, SWT.SINGLE | SWT.BORDER | SWT.WRAP);
		snippetDescriptionText.setLayoutData(new GridData(
			GridData.GRAB_HORIZONTAL |
			GridData.HORIZONTAL_ALIGN_FILL));
		snippetDescriptionText.addModifyListener(
			new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					validateInput();
				}
			}
		);
		snippetDescriptionText.setFont(parent.getFont());
		
	
		//Snippet start block label
		label = new Label(composite, SWT.WRAP);
		label.setText(snippetCodeStartLabel);
		data = new GridData(
			GridData.GRAB_HORIZONTAL |
			GridData.GRAB_VERTICAL |
			GridData.HORIZONTAL_ALIGN_FILL |
			GridData.VERTICAL_ALIGN_CENTER);
		data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
		label.setLayoutData(data);
		label.setFont(parent.getFont());

		// Snippet start block text
		snippetStartText= new Text(composite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		data = new GridData(
				GridData.GRAB_HORIZONTAL |
				GridData.HORIZONTAL_ALIGN_FILL |
				GridData.GRAB_VERTICAL | 
				GridData.VERTICAL_ALIGN_FILL);
		data.heightHint = convertHeightInCharsToPixels(6);
		snippetStartText.setLayoutData(data);
		snippetStartText.addModifyListener(
			new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					validateInput();
				}
			}
		);
		snippetStartText.setFont(parent.getFont());
		
		//Snippet end block label
		label = new Label(composite, SWT.WRAP);
		label.setText(snippetCodeEndLabel);
		data = new GridData(
			GridData.GRAB_HORIZONTAL |
			GridData.GRAB_VERTICAL |
			GridData.HORIZONTAL_ALIGN_FILL |
			GridData.VERTICAL_ALIGN_CENTER);
		data.widthHint = convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH);
		label.setLayoutData(data);
		label.setFont(parent.getFont());

		//Snippet end block text
		snippetEndText= new Text(composite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		data = new GridData(
				GridData.GRAB_HORIZONTAL |
				GridData.HORIZONTAL_ALIGN_FILL |
				GridData.GRAB_VERTICAL | 
				GridData.VERTICAL_ALIGN_FILL);
		data.heightHint = convertHeightInCharsToPixels(6);
		snippetEndText.setLayoutData(data);
		snippetEndText.addModifyListener(
			new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					validateInput();
				}
			}
		);
		snippetEndText.setFont(parent.getFont());
		
		errorMessageLabel = new Label(composite, SWT.WRAP);
		data = new GridData(
				GridData.GRAB_HORIZONTAL |
				GridData.GRAB_VERTICAL |
				GridData.HORIZONTAL_ALIGN_FILL |
				GridData.VERTICAL_ALIGN_CENTER);
		data.heightHint = convertHeightInCharsToPixels(2);
		errorMessageLabel.setLayoutData(data);
		errorMessageLabel.setFont(parent.getFont());
		Color color = new Color(Display.getCurrent(),255,0,0);
		errorMessageLabel.setForeground(color);
		
	 	return composite;
	}
	
	/*
	 * 
	 */
	protected void okPressed() 
	{   
		writer.writeSnippet(
			snippetNameText.getText(),
			snippetKeyComboText.getText(),
			snippetDescriptionText.getText(), 
			snippetStartText.getText(),
			snippetEndText.getText(),
			false,
			"txt"
		);
		
		close();
		treeView.refresh();
	}

	/**
	 * 
	 */
	protected void buttonPressed(int buttonId) 
	{
		if (buttonId == IDialogConstants.OK_ID) 
		{
			snippetNameValue = snippetNameText.getText();
			snippetKeyComboValue = snippetKeyComboText.getText();
			snippetDescriptionValue = snippetDescriptionText.getText();
			snippetStartValue = snippetStartText.getText();
			snippetEndValue = snippetEndText.getText();
		}
		else 
		{
			snippetNameValue= null;
			snippetKeyComboValue = null;
			snippetDescriptionValue = null;
			snippetStartValue = null;
			snippetEndValue = null;
		}
		super.buttonPressed(buttonId);
	}
	
	/*
	 * 
	 */
	protected void configureShell(Shell shell) 
	{
		super.configureShell(shell);
		
		if (dialogTitle != null)
			shell.setText(dialogTitle);
	}
	
	/*
	 * 
	 */
	protected void createButtonsForButtonBar(Composite parent) 
	{
		// create OK and Cancel buttons by default
		okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	
		//do this here because setting the text will set enablement on the ok button
		snippetNameText.setFocus();
		
		if(snippetNameValue != null) 
		{
			snippetNameText.setText(snippetNameValue);
			snippetNameText.selectAll();
		}

		if(snippetKeyComboValue != null) 
		{
			snippetKeyComboText.setText(snippetKeyComboValue);
		} 
		
		if (snippetDescriptionValue != null) 
		{
			snippetDescriptionText.setText(snippetDescriptionValue);
		}
		
		if (snippetStartValue != null) 
		{
			snippetStartText.setText(snippetStartValue);
		}
		
		if (snippetEndValue != null) 
		{
			snippetEndText.setText(snippetEndValue);
		}
	}
	
	protected Label getErrorMessageLabel() 
	{
		return errorMessageLabel;
	}
	
	protected Button getOkButton() 
	{
		return okButton;
	}
		
	protected void validateInput() 
	{
		String errorMessage = null;
		
		if (!snippetNameText.getText().matches("[0-9a-zA-Z _-]+")) 
		{
		    errorMessage = "The snippet name can only contain numbers, alphabetic characters, space underscore and dash.";
		}
		else if (snippetKeyComboText.getText().length() > 0 && !snippetKeyComboText.getText().matches("[0-9a-zA-Z_-]+")) 
		{
		    errorMessage = "The trigger text can only contain numbers, alphabetic characters, underscore and dash.";
		}
		
		errorMessageLabel.setText(errorMessage == null ? "" : errorMessage);
		okButton.setEnabled(errorMessage == null);
		errorMessageLabel.getParent().update();
	}
}