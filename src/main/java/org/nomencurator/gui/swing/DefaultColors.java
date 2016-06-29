/*
 * DefaultColors.java: a utlilty class to manage default colors depending on the UI
 *
 * Copyright (c) 2015 Nozomi `James' Ytow
 * All rights reserved.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.nomencurator.gui.swing;

import java.awt.Color;

import java.util.HashMap;
import java.util.Map;

import javax.swing.UIDefaults;
import javax.swing.UIManager;

import lombok.Getter;
/**
 * <tt>DefaultColors</code> provides utlity methods to get UI defalut colros..
 *
 * @version 	29 Aug. 2015
 * @author 	Nozomi `James' Ytow
 */
public class DefaultColors
{
    public enum ComponentState {
	ENABLED, DISABLED, SELECTED;
    }

    public enum ComponentName {
	BUTTON("Button"),
	BUTTON_TOOLBAR_BORDER("Button.toolBarBorder"),
	CHECKBOX("CheckBox"),
	CHECKBOX_MENU_ITEM("CheckBoxMenuItem"),
	CHECKBOX_MENU_ITEM_ACCELERATOR("CheckBoxMenuItem.accelerator"),
	COLORCHOOSER("ColorChooser"),
	COMBOBOX("ComboBox"),
	COMBOBOX_BUTTON("ComboBox.button"),
	DESKTOP("Desktop"),
	DESKTOP_ICON("DesktopIcon"),
	EDITOR_PANE("EditorPane"),
	EDITOR_PANE_CARET("EditorPane.caret"),
	FORMATTED_TEXTFIELD("FormattedTextField"),
	FORMATTED_TEXTFIELD_CARET("FormattedTextField.caret"),
	INTERNALFRAME("InternalFrame"),
	INTERNALFRAME_BORDER("InternalFrame.border"),
	LABEL("Label"),
	LIST("List"),
	MENU("Menu"),
	MENU_ACCELERATOR("Menu.accelerator"),
	MENUBAR("MenuBar"),
	MENU_ITEM("MenuItem"),
	MENU_ITEM_ACCELERATOR("MenuItem.accelerator"),
	OPTIONPANE("OptionPane"),
	OPTIONPANE_ERRORDIALOG("OptionPane.errorDialog"),
	OPTIONPANE_ERRORDIALOG_BORDER("OptionPane.errorDialog.border"),
	OPTIONPANE_ERRORDIALOG_TITLEPANE("OptionPane.errorDialog.titlePane"),
	OPTIONPANE_QUESTIONDIALOG_BORDER("OptionPane.questionDialog.border"),
	OPTIONPANE_QUESTIONDIALOG_TITLEPANE("OptionPane.questionDialog.titlePane"),
	OPTIONPANE_WARNINGDIALOG_BORDER("OptionPane.warningDialog.border"),
	OPTIONPANE_WARNINGDIALOG_TITLEPANE("OptionPane.warningDialog.titlePane"),
	PANEL("Panel"),
	PASSWORDFIELD("PasswordField"),
	PASSWORDFIELD_CARET("PasswordField.caret"),
	POPUPMENU("PopupMenu"),
	PROGRESSBAR("ProgressBar"),
	RADIOBUTTON("RadioButton"),
	RADIOBUTTON_MENU_ITEM("RadioButtonMenuItem"),
	RADIOBUTTON_MENU_ITEM_ACCELERATOR("RadioButtonMenuItem.accelerator"),
	SCROLLBAR("ScrollBar"),
	SCROLLBAR_THUMB("ScrollBar.thumb"),
	SCROLLBAR_TRACK("ScrollBar.track"),
	SCROLLPANE("ScrollPane"),
	SEPARATOR("Separator"),
	SLIDER("Slider"),
	SLIDER_TICK("Slider.tick"),
	SPINNER("Spinner"),
	SPLITPANE("SplitPane"),
	SPLITPANEDIVIDER("SplitPaneDivider"),
	TABBEDPANE("TabbedPane"),
	TABBEDPANE_TABAREA("TabbedPane.tabArea"),
	TABLE("Table"),
	TABLE_FOCUS_CELL("Table.focusCell"),
	TABLEHEADER("TableHeader"),
	TABLEHEADER_FOCUS_CELL("TableHeader.focusCell"),
	TEXTAREA("TextArea"),
	TEXTAREA_CARET("TextArea.caret"),
	TEXTFIELD("TextField"),
	TEXTFIELD_CARET("TextField.caret"),
	TEXTPANE("TextPane"),
	TEXTPANE_CARET("TextPane.caret"),
	TITLEDBORDER("TitledBorder"),
	TOGGLEBUTTON("ToggleButton"),
	TOOLBAR("ToolBar"),
	TOOLBAR_DOCKING("ToolBar.docking"),
	TOOLBAR_FLOATING("ToolBar.floating"),

	TOOLTIP("ToolTip"),
	TREE("Tree"),
	TREE_TEXT("Tree.text"),
	VIEWPORT("Viewport");
	
	@Getter
	private String name;

	ComponentName(String name)
	{
	    this.name = name;
	}
    }

    protected static Map<ComponentName, String> backgrounds = new HashMap<ComponentName, String>();
    protected static Map<ComponentName, String> borders = new HashMap<ComponentName, String>();
    protected static Map<ComponentName, String> darkShadows = new HashMap<ComponentName, String>();
    protected static Map<ComponentName, String> disabledForegrounds = new HashMap<ComponentName, String>();
    protected static Map<ComponentName, String> disabledBackgrounds = new HashMap<ComponentName, String>();
    protected static Map<ComponentName, String> focuses = new HashMap<ComponentName, String>();
    protected static Map<ComponentName, String> foregrounds = new HashMap<ComponentName, String>();
    protected static Map<ComponentName, String> highlights = new HashMap<ComponentName, String>();
    protected static Map<ComponentName, String> lights = new HashMap<ComponentName, String>();
    protected static Map<ComponentName, String> selectedForegrounds = new HashMap<ComponentName, String>();
    protected static Map<ComponentName, String> selectedBackgrounds = new HashMap<ComponentName, String>();
    protected static Map<ComponentName, String> shadows = new HashMap<ComponentName, String>();

    static {
	backgrounds.put(ComponentName.BUTTON,"Button.background");
	disabledForegrounds.put(ComponentName.BUTTON,"Button.disabledText");
	foregrounds.put(ComponentName.BUTTON,"Button.foreground");
	selectedForegrounds.put(ComponentName.BUTTON,"Button.select");
	darkShadows.put(ComponentName.BUTTON,"Button.darkShadow");
	highlights.put(ComponentName.BUTTON,"Button.highlight");
	lights.put(ComponentName.BUTTON,"Button.light");
	shadows.put(ComponentName.BUTTON,"Button.shadow");
	focuses.put(ComponentName.BUTTON,"Button.focus");

	disabledBackgrounds.put(ComponentName.BUTTON_TOOLBAR_BORDER,"Button.disabledToolBarBorderBackground");
	backgrounds.put(ComponentName.BUTTON_TOOLBAR_BORDER,"Button.toolBarBorderBackground");

	backgrounds.put(ComponentName.CHECKBOX,"CheckBox.background");
	disabledForegrounds.put(ComponentName.CHECKBOX,"CheckBox.disabledText");
	foregrounds.put(ComponentName.CHECKBOX,"CheckBox.foreground");
	selectedForegrounds.put(ComponentName.CHECKBOX,"Checkbox.select");
	backgrounds.put(ComponentName.CHECKBOX_MENU_ITEM,"CheckBoxMenuItem.background");
	disabledForegrounds.put(ComponentName.CHECKBOX_MENU_ITEM,"CheckBoxMenuItem.disabledForeground");
	foregrounds.put(ComponentName.CHECKBOX_MENU_ITEM,"CheckBoxMenuItem.foreground");
	selectedBackgrounds.put(ComponentName.CHECKBOX_MENU_ITEM,"CheckBoxMenuItem.selectionBackground");
	selectedForegrounds.put(ComponentName.CHECKBOX_MENU_ITEM,"CheckBoxMenuItem.selectionForeground");
	foregrounds.put(ComponentName.CHECKBOX_MENU_ITEM_ACCELERATOR,"CheckBoxMenuItem.acceleratorForeground");
	selectedForegrounds.put(ComponentName.CHECKBOX_MENU_ITEM_ACCELERATOR,"CheckBoxMenuItem.acceleratorSelectionForeground");
	focuses.put(ComponentName.CHECKBOX,"CheckBox.focus");
	
	backgrounds.put(ComponentName.COLORCHOOSER,"ColorChooser.background");
	foregrounds.put(ComponentName.COLORCHOOSER,"ColorChooser.foreground");
	
	backgrounds.put(ComponentName.COMBOBOX,"ComboBox.background");
	disabledBackgrounds.put(ComponentName.COMBOBOX,"ComboBox.disabledBackground");
	disabledForegrounds.put(ComponentName.COMBOBOX,"ComboBox.disabledForeground");
	foregrounds.put(ComponentName.COMBOBOX,"ComboBox.foreground");
	selectedBackgrounds.put(ComponentName.COMBOBOX,"ComboBox.selectionBackground");
	selectedForegrounds.put(ComponentName.COMBOBOX,"ComboBox.selectionForeground");
	
	backgrounds.put(ComponentName.COMBOBOX_BUTTON,"ComboBox.buttonBackground");
	darkShadows.put(ComponentName.COMBOBOX_BUTTON,"ComboBox.buttonDarkShadow");
	highlights.put(ComponentName.COMBOBOX_BUTTON,"ComboBox.buttonHighlight");
	shadows.put(ComponentName.COMBOBOX_BUTTON,"ComboBox.buttonShadow");

	backgrounds.put(ComponentName.DESKTOP,"Desktop.background");
	backgrounds.put(ComponentName.DESKTOP_ICON,"DesktopIcon.background");
	foregrounds.put(ComponentName.DESKTOP_ICON,"DesktopIcon.foreground");
	
	backgrounds.put(ComponentName.EDITOR_PANE,"EditorPane.background");
	foregrounds.put(ComponentName.EDITOR_PANE,"EditorPane.foreground");
	disabledForegrounds.put(ComponentName.EDITOR_PANE,"EditorPane.inactiveForeground");
	selectedBackgrounds.put(ComponentName.EDITOR_PANE,"EditorPane.selectionBackground");
	selectedForegrounds.put(ComponentName.EDITOR_PANE,"EditorPane.selectionForeground");
	foregrounds.put(ComponentName.EDITOR_PANE_CARET,"EditorPane.caretForeground");

	backgrounds.put(ComponentName.FORMATTED_TEXTFIELD,"FormattedTextField.background");
	foregrounds.put(ComponentName.FORMATTED_TEXTFIELD,"FormattedTextField.foreground");
	disabledForegrounds.put(ComponentName.FORMATTED_TEXTFIELD,"FormattedTextField.inactiveBackground");
	disabledForegrounds.put(ComponentName.FORMATTED_TEXTFIELD,"FormattedTextField.inactiveForeground");
	selectedBackgrounds.put(ComponentName.FORMATTED_TEXTFIELD,"FormattedTextField.selectionBackground");
	selectedForegrounds.put(ComponentName.FORMATTED_TEXTFIELD,"FormattedTextField.selectionForeground");
	backgrounds.put(ComponentName.INTERNALFRAME,"InternalFrame.activeTitleBackground");
	foregrounds.put(ComponentName.INTERNALFRAME,"InternalFrame.activeTitleForeground");
	disabledBackgrounds.put(ComponentName.INTERNALFRAME,"InternalFrame.inactiveTitleBackground");
	disabledForegrounds.put(ComponentName.INTERNALFRAME,"InternalFrame.inactiveTitleForeground");
	borders.put(ComponentName.INTERNALFRAME,"InternalFrame.borderColor");
	darkShadows.put(ComponentName.INTERNALFRAME_BORDER,"InternalFrame.borderDarkShadow");
	highlights.put(ComponentName.INTERNALFRAME_BORDER,"InternalFrame.borderHighlight");
	lights.put(ComponentName.INTERNALFRAME_BORDER,"InternalFrame.borderLight");
	shadows.put(ComponentName.INTERNALFRAME_BORDER,"InternalFrame.borderShadow");
	backgrounds.put(ComponentName.LABEL,"Label.background");
	disabledForegrounds.put(ComponentName.LABEL,"Label.disabledForeground");
	foregrounds.put(ComponentName.LABEL,"Label.foreground");
	
	backgrounds.put(ComponentName.LIST,"List.background");
	foregrounds.put(ComponentName.LIST,"List.foreground");
	selectedBackgrounds.put(ComponentName.LIST,"List.selectionBackground");
	selectedForegrounds.put(ComponentName.LIST,"List.selectionForeground");
	
	backgrounds.put(ComponentName.MENU,"Menu.background");
	disabledForegrounds.put(ComponentName.MENU,"Menu.disabledForeground");
	foregrounds.put(ComponentName.MENU,"Menu.foreground");
	
	foregrounds.put(ComponentName.MENU_ACCELERATOR,"Menu.acceleratorForeground");
	selectedForegrounds.put(ComponentName.MENU_ACCELERATOR,"Menu.acceleratorSelectionForeground");
	
	backgrounds.put(ComponentName.MENUBAR,"MenuBar.background");
	foregrounds.put(ComponentName.MENUBAR,"MenuBar.foreground");
	borders.put(ComponentName.MENUBAR,"MenuBar.borderColor");
	highlights.put(ComponentName.MENUBAR,"MenuBar.highlight");
	shadows.put(ComponentName.MENUBAR,"MenuBar.shadow");
	
	backgrounds.put(ComponentName.MENU_ITEM,"MenuItem.background");
	disabledForegrounds.put(ComponentName.MENU_ITEM,"MenuItem.disabledForeground");
	foregrounds.put(ComponentName.MENU_ITEM,"MenuItem.foreground");
	selectedBackgrounds.put(ComponentName.MENU_ITEM,"MenuItem.selectionBackground");
	selectedForegrounds.put(ComponentName.MENU_ITEM,"MenuItem.selectionForeground");
	foregrounds.put(ComponentName.MENU_ITEM_ACCELERATOR,"MenuItem.acceleratorForeground");
	selectedForegrounds.put(ComponentName.MENU_ITEM_ACCELERATOR,"MenuItem.acceleratorSelectionForeground");
	backgrounds.put(ComponentName.OPTIONPANE,"OptionPane.background");
	backgrounds.put(ComponentName.OPTIONPANE_ERRORDIALOG_BORDER,"OptionPane.errorDialog.border.background");
	backgrounds.put(ComponentName.OPTIONPANE_ERRORDIALOG_TITLEPANE,"OptionPane.errorDialog.titlePane.background");
	foregrounds.put(ComponentName.OPTIONPANE_ERRORDIALOG_TITLEPANE,"OptionPane.errorDialog.titlePane.foreground");
	foregrounds.put(ComponentName.OPTIONPANE,"OptionPane.foreground");
	backgrounds.put(ComponentName.OPTIONPANE_QUESTIONDIALOG_BORDER,"OptionPane.questionDialog.border.background");
	backgrounds.put(ComponentName.OPTIONPANE_QUESTIONDIALOG_TITLEPANE,"OptionPane.questionDialog.titlePane.background");
	foregrounds.put(ComponentName.OPTIONPANE_QUESTIONDIALOG_TITLEPANE,"OptionPane.questionDialog.titlePane.foreground");
	backgrounds.put(ComponentName.OPTIONPANE_WARNINGDIALOG_BORDER,"OptionPane.warningDialog.border.background");
	backgrounds.put(ComponentName.OPTIONPANE_WARNINGDIALOG_TITLEPANE,"OptionPane.warningDialog.titlePane.background");
	foregrounds.put(ComponentName.OPTIONPANE_WARNINGDIALOG_TITLEPANE,"OptionPane.warningDialog.titlePane.foreground");
	backgrounds.put(ComponentName.PANEL,"Panel.background");
	foregrounds.put(ComponentName.PANEL,"Panel.foreground");
	backgrounds.put(ComponentName.PASSWORDFIELD,"PasswordField.background");
	foregrounds.put(ComponentName.PASSWORDFIELD,"PasswordField.foreground");
	disabledBackgrounds.put(ComponentName.PASSWORDFIELD,"PasswordField.inactiveBackground");
	disabledForegrounds.put(ComponentName.PASSWORDFIELD,"PasswordField.inactiveForeground");
	selectedBackgrounds.put(ComponentName.PASSWORDFIELD,"PasswordField.selectionBackground");
	selectedForegrounds.put(ComponentName.PASSWORDFIELD,"PasswordField.selectionForeground");
	backgrounds.put(ComponentName.POPUPMENU,"PopupMenu.background");
	foregrounds.put(ComponentName.POPUPMENU,"PopupMenu.foreground");
	backgrounds.put(ComponentName.PROGRESSBAR,"ProgressBar.background");
	foregrounds.put(ComponentName.PROGRESSBAR,"ProgressBar.foreground");
	selectedBackgrounds.put(ComponentName.PROGRESSBAR,"ProgressBar.selectionBackground");
	selectedForegrounds.put(ComponentName.PROGRESSBAR,"ProgressBar.selectionForeground");
	backgrounds.put(ComponentName.RADIOBUTTON,"RadioButton.background");
	disabledForegrounds.put(ComponentName.RADIOBUTTON,"RadioButton.disabledText");
	foregrounds.put(ComponentName.RADIOBUTTON,"RadioButton.foreground");
	selectedForegrounds.put(ComponentName.RADIOBUTTON,"RadioButton.select");
	backgrounds.put(ComponentName.RADIOBUTTON_MENU_ITEM,"RadioButtonMenuItem.background");
	disabledForegrounds.put(ComponentName.RADIOBUTTON_MENU_ITEM,"RadioButtonMenuItem.disabledForeground");
	foregrounds.put(ComponentName.RADIOBUTTON_MENU_ITEM,"RadioButtonMenuItem.foreground");
	selectedBackgrounds.put(ComponentName.RADIOBUTTON_MENU_ITEM,"RadioButtonMenuItem.selectionBackground");
	selectedForegrounds.put(ComponentName.RADIOBUTTON_MENU_ITEM,"RadioButtonMenuItem.selectionForeground");
	foregrounds.put(ComponentName.RADIOBUTTON_MENU_ITEM_ACCELERATOR,"RadioButtonMenuItem.acceleratorForeground");
	selectedForegrounds.put(ComponentName.RADIOBUTTON_MENU_ITEM_ACCELERATOR,"RadioButtonMenuItem.acceleratorSelectionForeground");
	backgrounds.put(ComponentName.SCROLLBAR,"ScrollBar.background");
	foregrounds.put(ComponentName.SCROLLBAR,"ScrollBar.foreground");
	backgrounds.put(ComponentName.SCROLLPANE,"ScrollPane.background");
	foregrounds.put(ComponentName.SCROLLPANE,"ScrollPane.foreground");
	backgrounds.put(ComponentName.SEPARATOR,"Separator.background");
	foregrounds.put(ComponentName.SEPARATOR,"Separator.foreground");
	backgrounds.put(ComponentName.SLIDER,"Slider.background");
	foregrounds.put(ComponentName.SLIDER,"Slider.foreground");
	backgrounds.put(ComponentName.SPINNER,"Spinner.background");
	foregrounds.put(ComponentName.SPINNER,"Spinner.foreground");
	backgrounds.put(ComponentName.SPLITPANE,"SplitPane.background");
	selectedBackgrounds.put(ComponentName.TABBEDPANE,"TabbedPane.background");
	foregrounds.put(ComponentName.TABBEDPANE,"TabbedPane.foreground");
	selectedForegrounds.put(ComponentName.TABBEDPANE,"TabbedPane.selected");
	backgrounds.put(ComponentName.TABBEDPANE_TABAREA,"TabbedPane.tabAreaBackground");
	backgrounds.put(ComponentName.TABBEDPANE,"TabbedPane.unselectedBackground");
	backgrounds.put(ComponentName.TABLE,"Table.background");
	foregrounds.put(ComponentName.TABLE,"Table.foreground");
	selectedBackgrounds.put(ComponentName.TABLE,"Table.selectionBackground");
	selectedForegrounds.put(ComponentName.TABLE,"Table.selectionForeground");
	backgrounds.put(ComponentName.TABLE_FOCUS_CELL,"Table.focusCellBackground");
	foregrounds.put(ComponentName.TABLE_FOCUS_CELL,"Table.focusCellForeground");
	backgrounds.put(ComponentName.TABLEHEADER,"TableHeader.background");
	foregrounds.put(ComponentName.TABLEHEADER,"TableHeader.foreground");
	backgrounds.put(ComponentName.TABLEHEADER_FOCUS_CELL,"TableHeader.focusCellBackground");
	backgrounds.put(ComponentName.TEXTAREA,"TextArea.background");
	foregrounds.put(ComponentName.TEXTAREA,"TextArea.foreground");
	disabledForegrounds.put(ComponentName.TEXTAREA,"TextArea.inactiveForeground");
	selectedBackgrounds.put(ComponentName.TEXTAREA,"TextArea.selectionBackground");
	selectedForegrounds.put(ComponentName.TEXTAREA,"TextArea.selectionForeground");
	backgrounds.put(ComponentName.TEXTFIELD,"TextField.background");
	foregrounds.put(ComponentName.TEXTFIELD,"TextField.foreground");
	disabledBackgrounds.put(ComponentName.TEXTFIELD,"TextField.inactiveBackground");
	disabledForegrounds.put(ComponentName.TEXTFIELD,"TextField.inactiveForeground");
	selectedBackgrounds.put(ComponentName.TEXTFIELD,"TextField.selectionBackground");
	selectedForegrounds.put(ComponentName.TEXTFIELD,"TextField.selectionForeground");
	backgrounds.put(ComponentName.TEXTPANE,"TextPane.background");
	foregrounds.put(ComponentName.TEXTPANE,"TextPane.foreground");
	disabledForegrounds.put(ComponentName.TEXTPANE,"TextPane.inactiveForeground");
	selectedBackgrounds.put(ComponentName.TEXTPANE,"TextPane.selectionBackground");
	selectedForegrounds.put(ComponentName.TEXTPANE,"TextPane.selectionForeground");
	foregrounds.put(ComponentName.TITLEDBORDER,"TitledBorder.titleColor");
	backgrounds.put(ComponentName.TOGGLEBUTTON,"ToggleButton.background");
	disabledForegrounds.put(ComponentName.TOGGLEBUTTON,"ToggleButton.disabledText");
	foregrounds.put(ComponentName.TOGGLEBUTTON,"ToggleButton.foreground");
	selectedForegrounds.put(ComponentName.TOGGLEBUTTON,"ToggleButton.select");
	backgrounds.put(ComponentName.TOOLBAR,"ToolBar.background");
	foregrounds.put(ComponentName.TOOLBAR,"ToolBar.foreground");
	borders.put(ComponentName.TOOLBAR,"ToolBar.borderColor");
	darkShadows.put(ComponentName.TOOLBAR,"ToolBar.darkShadow");
	backgrounds.put(ComponentName.TOOLBAR_DOCKING,"ToolBar.dockingBackground");
	foregrounds.put(ComponentName.TOOLBAR_DOCKING,"ToolBar.dockingForeground");
	backgrounds.put(ComponentName.TOOLBAR_FLOATING,"ToolBar.floatingBackground");
	foregrounds.put(ComponentName.TOOLBAR_FLOATING,"ToolBar.floatingForeground");
	highlights.put(ComponentName.TOOLBAR,"ToolBar.highlight");
	lights.put(ComponentName.TOOLBAR,"ToolBar.light");
	shadows.put(ComponentName.TOOLBAR,"ToolBar.shadow");
	backgrounds.put(ComponentName.TOOLTIP,"ToolTip.background");
	disabledBackgrounds.put(ComponentName.TOOLTIP,"ToolTip.backgroundInactive");
	foregrounds.put(ComponentName.TOOLTIP,"ToolTip.foreground");
	disabledForegrounds.put(ComponentName.TOOLTIP,"ToolTip.foregroundInactive");
	backgrounds.put(ComponentName.TREE,"Tree.background");
	foregrounds.put(ComponentName.TREE,"Tree.foreground");
	selectedBackgrounds.put(ComponentName.TREE,"Tree.selectionBackground");
	selectedForegrounds.put(ComponentName.TREE,"Tree.selectionForeground");
	backgrounds.put(ComponentName.TREE_TEXT,"Tree.textBackground");
	foregrounds.put(ComponentName.TREE_TEXT,"Tree.textForeground");
	backgrounds.put(ComponentName.VIEWPORT,"Viewport.background");
	foregrounds.put(ComponentName.VIEWPORT,"Viewport.foreground");
	
	foregrounds.put(ComponentName.FORMATTED_TEXTFIELD_CARET,"FormattedTextField.caretForeground");
	foregrounds.put(ComponentName.PASSWORDFIELD_CARET,"PasswordField.caretForeground");
	foregrounds.put(ComponentName.TEXTAREA_CARET,"TextArea.caretForeground");
	foregrounds.put(ComponentName.TEXTFIELD_CARET,"TextField.caretForeground");
	foregrounds.put(ComponentName.TEXTPANE_CARET,"TextPane.caretForeground");
	
	focuses.put(ComponentName.RADIOBUTTON,"RadioButton.focus");
	focuses.put(ComponentName.SLIDER,"Slider.focus");
	focuses.put(ComponentName.SPLITPANE,"SplitPane.dividerFocusColor");
	focuses.put(ComponentName.TABBEDPANE,"TabbedPane.focus");
	focuses.put(ComponentName.TOGGLEBUTTON,"ToggleButton.focus");
	
	
	shadows.put(ComponentName.OPTIONPANE_ERRORDIALOG_TITLEPANE,"OptionPane.errorDialog.titlePane.shadow");
	shadows.put(ComponentName.OPTIONPANE_QUESTIONDIALOG_TITLEPANE,"OptionPane.questionDialog.titlePane.shadow");
	shadows.put(ComponentName.OPTIONPANE_WARNINGDIALOG_TITLEPANE,"OptionPane.warningDialog.titlePane.shadow");
	darkShadows.put(ComponentName.RADIOBUTTON,"RadioButton.darkShadow");
	highlights.put(ComponentName.RADIOBUTTON,"RadioButton.highlight");
	lights.put(ComponentName.RADIOBUTTON,"RadioButton.light");
	shadows.put(ComponentName.RADIOBUTTON,"RadioButton.shadow");
	darkShadows.put(ComponentName.SCROLLBAR,"ScrollBar.darkShadow");
	highlights.put(ComponentName.SCROLLBAR,"ScrollBar.highlight");
	shadows.put(ComponentName.SCROLLBAR,"ScrollBar.shadow");
	backgrounds.put(ComponentName.SCROLLBAR_THUMB,"ScrollBar.thumb");
	foregrounds.put(ComponentName.SCROLLBAR_THUMB,"ScrollBar.thumb");
	darkShadows.put(ComponentName.SCROLLBAR_THUMB,"ScrollBar.thumbDarkShadow");
	highlights.put(ComponentName.SCROLLBAR_THUMB,"ScrollBar.thumbHighlight");
	shadows.put(ComponentName.SCROLLBAR_THUMB,"ScrollBar.thumbShadow");
	backgrounds.put(ComponentName.SCROLLBAR_TRACK,"ScrollBar.track");
	foregrounds.put(ComponentName.SCROLLBAR_TRACK,"ScrollBar.track");
	highlights.put(ComponentName.SCROLLBAR_TRACK,"ScrollBar.trackHighlight");
	highlights.put(ComponentName.SEPARATOR,"Separator.highlight");
	shadows.put(ComponentName.SEPARATOR,"Separator.shadow");
	highlights.put(ComponentName.SLIDER,"Slider.highlight");
	shadows.put(ComponentName.SLIDER,"Slider.shadow");
	foregrounds.put(ComponentName.SLIDER_TICK,"Slider.tickColor");
	backgrounds.put(ComponentName.SLIDER_TICK,"Slider.altTrackColor");
	darkShadows.put(ComponentName.SPLITPANE,"SplitPane.darkShadow");
	highlights.put(ComponentName.SPLITPANE,"SplitPane.highlight");
	shadows.put(ComponentName.SPLITPANE,"SplitPane.shadow");
	darkShadows.put(ComponentName.TABBEDPANE,"TabbedPane.darkShadow");
	highlights.put(ComponentName.TABBEDPANE,"TabbedPane.highlight");
	lights.put(ComponentName.TABBEDPANE,"TabbedPane.light");
	shadows.put(ComponentName.TABBEDPANE,"TabbedPane.shadow");
	darkShadows.put(ComponentName.TEXTFIELD,"TextField.darkShadow");
	highlights.put(ComponentName.TEXTFIELD,"TextField.highlight");
	lights.put(ComponentName.TEXTFIELD,"TextField.light");
	shadows.put(ComponentName.TEXTFIELD,"TextField.shadow");
	
	darkShadows.put(ComponentName.TOGGLEBUTTON,"ToggleButton.darkShadow");
	highlights.put(ComponentName.TOGGLEBUTTON,"ToggleButton.highlight");
	lights.put(ComponentName.TOGGLEBUTTON,"ToggleButton.light");
	shadows.put(ComponentName.TOGGLEBUTTON,"ToggleButton.shadow");
/*
grounds.put(ComponentName.COLORCHOOSER,"ColorChooser.swatchesDefaultRecentColor");
grounds.put(ComponentName.LABEL,"Label.disabledShadow");
grounds.put(ComponentName.LIST,"List.dropCellBackground");
grounds.put(ComponentName.LIST,"List.dropLineColor");
grounds.put(ComponentName.SPLITPANEDIVIDER,"SplitPaneDivider.draggingColor");
grounds.put(ComponentName.TABBEDPANE,"TabbedPane.borderHightlightColor");
grounds.put(ComponentName.TABBEDPANE,"TabbedPane.contentAreaColor");
grounds.put(ComponentName.TABBEDPANE,"TabbedPane.selectHighlight");
grounds.put(ComponentName.TABLE,"Table.dropCellBackground");
grounds.put(ComponentName.TABLE,"Table.dropLineColor");
grounds.put(ComponentName.TABLE,"Table.dropLineShortColor");
grounds.put(ComponentName.TABLE,"Table.gridColor");
grounds.put(ComponentName.TABLE,"Table.sortIconColor");
grounds.put(ComponentName.TREE,"Tree.dropCellBackground");
grounds.put(ComponentName.TREE,"Tree.dropLineColor");
grounds.put(ComponentName.TREE,"Tree.hash");
grounds.put(ComponentName.TREE,"Tree.line");
grounds.put(ComponentName.TREE,"Tree.selectionBorderColor");
*/
    }

    @Getter
    protected ComponentName componentName;

    @Getter
    protected Color background;
    @Getter
    protected Color border;
    @Getter
    protected Color darkShadow;
    @Getter
    protected Color disabledForeground;
    @Getter
    protected Color disabledBackground;
    @Getter
    protected Color focus;
    @Getter
    protected Color foreground;
    @Getter
    protected Color highlight;
    @Getter
    protected Color light;
    @Getter
    protected Color selectedForeground;
    @Getter
    protected Color selectedBackground;
    @Getter
    protected Color shadow;


    public DefaultColors(ComponentName componentName)
    {
	this.componentName = componentName;
	setDefaultColors();
    }

    public Color getBackground(ComponentState state)
    {
	switch(state) {
	case ENABLED:
	    return getBackground();
	case DISABLED:
	    return getDisabledBackground();
	case SELECTED:
	    return getSelectedBackground();
	}
	return null;
    }

    public Color getForeground(ComponentState state)
    {
	switch(state) {
	case ENABLED:
	    return getForeground();
	case DISABLED:
	    return getDisabledForeground();
	case SELECTED:
	    return getSelectedForeground();
	}
	return null;
    }

    public void updateUI()
    {
	setDefaultColors();
    }

    protected void setDefaultColors()
    {
	UIDefaults defaults = UIManager.getDefaults();
	ComponentName componentKey = getComponentName();
	background = defaults.getColor(backgrounds.get(componentKey));
	border = defaults.getColor(borders.get(componentKey));
	darkShadow = defaults.getColor(darkShadows.get(componentKey));
	disabledForeground = defaults.getColor(disabledForegrounds.get(componentKey));
	disabledBackground = defaults.getColor(disabledBackgrounds.get(componentKey));
	focus = defaults.getColor(focuses.get(componentKey));
	foreground = defaults.getColor(foregrounds.get(componentKey));
	highlight = defaults.getColor(highlights.get(componentKey));
	light = defaults.getColor(lights.get(componentKey));
	selectedForeground = defaults.getColor(selectedForegrounds.get(componentKey));
	selectedBackground = defaults.getColor(selectedBackgrounds.get(componentKey));
	shadow = defaults.getColor(shadows.get(componentKey));
    }

    public static Color getBackground(ComponentName componentKey, ComponentState state)
    {
	switch(state) {
	case ENABLED:
	    return getBackground(componentKey);
	case DISABLED:
	    return getDisabledBackground(componentKey);
	case SELECTED:
	    return getSelectedBackground(componentKey);
	}
	return null;
    }

    public static Color getForeground(ComponentName componentKey, ComponentState state)
    {
	switch(state) {
	case ENABLED:
	    return getForeground(componentKey);
	case DISABLED:
	    return getDisabledForeground(componentKey);
	case SELECTED:
	    return getSelectedForeground(componentKey);
	}
	return null;
    }


    public static Color getBackground(ComponentName componentKey)
    {
	return UIManager.getDefaults().getColor(backgrounds.get(componentKey));
    }

    public static Color getBorder(ComponentName componentKey)
    {
	return UIManager.getDefaults().getColor(borders.get(componentKey));
    }

    public static Color getDarkShadow(ComponentName componentKey)
    {
	return UIManager.getDefaults().getColor(darkShadows.get(componentKey));
    }

    public static Color getDisabledForeground(ComponentName componentKey)
    {
	return UIManager.getDefaults().getColor(disabledForegrounds.get(componentKey));
    }

    public static Color getDisabledBackground(ComponentName componentKey)
    {
	return UIManager.getDefaults().getColor(disabledBackgrounds.get(componentKey));
    }

    public static Color getFocus(ComponentName componentKey)
    {
	return UIManager.getDefaults().getColor(focuses.get(componentKey));
    }

    public static Color getForeground(ComponentName componentKey)
    {
	return UIManager.getDefaults().getColor(foregrounds.get(componentKey));
    }

    public static Color getHighlight(ComponentName componentKey)
    {
	return UIManager.getDefaults().getColor(highlights.get(componentKey));
    }

    public static Color getLight(ComponentName componentKey)
    {
	return UIManager.getDefaults().getColor(lights.get(componentKey));
    }

    public static Color getSelectedForeground(ComponentName componentKey)
    {
	return UIManager.getDefaults().getColor(selectedForegrounds.get(componentKey));
    }

    public static Color getSelectedBackground(ComponentName componentKey)
    {
	return UIManager.getDefaults().getColor(selectedBackgrounds.get(componentKey));
    }

    public static Color getShadow(ComponentName componentKey)
    {
	return UIManager.getDefaults().getColor(shadows.get(componentKey));
    }
}

