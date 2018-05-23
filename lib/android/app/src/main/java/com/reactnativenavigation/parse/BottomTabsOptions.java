package com.reactnativenavigation.parse;

import com.reactnativenavigation.parse.params.Bool;
import com.reactnativenavigation.parse.params.Color;
import com.reactnativenavigation.parse.params.NullBool;
import com.reactnativenavigation.parse.params.NullColor;
import com.reactnativenavigation.parse.params.NullNumber;
import com.reactnativenavigation.parse.params.NullText;
import com.reactnativenavigation.parse.params.Number;
import com.reactnativenavigation.parse.params.Text;
import com.reactnativenavigation.parse.parsers.BoolParser;
import com.reactnativenavigation.parse.parsers.ColorParser;
import com.reactnativenavigation.parse.parsers.NumberParser;
import com.reactnativenavigation.parse.parsers.TextParser;

import org.json.JSONObject;

public class BottomTabsOptions {

	public static BottomTabsOptions parse(JSONObject json) {
		BottomTabsOptions options = new BottomTabsOptions();
		if (json == null) return options;

        options.backgroundColor = ColorParser.parse(json, "backgroundColor");
        options.tabColor = ColorParser.parse(json, "tabColor");
        options.selectedTabColor = ColorParser.parse(json, "selectedTabColor");
        options.currentTabId = TextParser.parse(json, "currentTabId");
		options.currentTabIndex = NumberParser.parse(json,"currentTabIndex");
		options.visible = BoolParser.parse(json,"visible");
        options.drawBehind = BoolParser.parse(json, "drawBehind");
		options.animate = BoolParser.parse(json,"animate");
        options.testId = TextParser.parse(json, "testID");

		return options;
	}

    public Color backgroundColor = new NullColor();
    public Color tabColor = new NullColor();
    public Color selectedTabColor = new NullColor();
	public Bool visible = new NullBool();
    public Bool drawBehind = new NullBool();
	public Bool animate = new NullBool();
	public Number currentTabIndex = new NullNumber();
	public Text currentTabId = new NullText();
    public Text testId = new NullText();

	void mergeWith(final BottomTabsOptions other) {
		if (other.currentTabId.hasValue()) {
			currentTabId = other.currentTabId;
		}
		if (other.currentTabIndex.hasValue()) {
            currentTabIndex = other.currentTabIndex;
		}
		if (other.visible.hasValue()) {
			visible = other.visible;
		}
        if (other.drawBehind.hasValue()) {
            drawBehind = other.drawBehind;
        }
		if (other.animate.hasValue()) {
			animate = other.animate;
		}
        if (other.tabColor.hasValue()) {
            tabColor = other.tabColor;
        }
        if (other.selectedTabColor.hasValue()) {
            selectedTabColor = other.selectedTabColor;
        }
        if (other.backgroundColor.hasValue()) {
		    backgroundColor = other.backgroundColor;
        }
        if (other.testId.hasValue()) {
            testId = other.testId;
        }
    }

    void mergeWithDefault(final BottomTabsOptions defaultOptions) {
        if (!currentTabId.hasValue()) {
            currentTabId = defaultOptions.currentTabId;
        }
        if (!currentTabIndex.hasValue()) {
            currentTabIndex = defaultOptions.currentTabIndex;
        }
        if (!visible.hasValue()) {
            visible = defaultOptions.visible;
        }
        if (!drawBehind.hasValue()) {
            drawBehind = defaultOptions.drawBehind;
        }
        if (!animate.hasValue()) {
            animate = defaultOptions.animate;
        }
        if (!tabColor.hasValue()) {
            tabColor = defaultOptions.tabColor;
        }
        if (!selectedTabColor.hasValue()) {
            selectedTabColor = defaultOptions.selectedTabColor;
        }
        if (!backgroundColor.hasValue()) {
            backgroundColor = defaultOptions.backgroundColor;
        }
    }
}
