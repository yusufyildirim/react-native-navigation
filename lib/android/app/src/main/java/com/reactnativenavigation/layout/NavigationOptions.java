package com.reactnativenavigation.layout;

import android.support.annotation.NonNull;

import org.json.JSONObject;

public class NavigationOptions {

	@NonNull
	public static NavigationOptions parse(JSONObject json) {
		if (json == null) {
			return new NavigationOptions();
		}

		return new NavigationOptions(json.optString("title"));
	}

	@NonNull
	static NavigationOptions merge(NavigationOptions main, NavigationOptions update) {
		return new NavigationOptions(
				update.title == null ? main.title : update.title
		);
	}

	public final String title;

	public NavigationOptions() {
		this("");
	}

	public NavigationOptions(String title) {
		this.title = title;
	}

}
