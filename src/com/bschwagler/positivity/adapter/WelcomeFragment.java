package com.bschwagler.positivity.adapter;

import com.bschwagler.positivity.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class WelcomeFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.welcome, container, false);
		
		//Setup nicer text with HTML formatting for our welcome message
		TextView welcomeMsg = (TextView)rootView.findViewById(R.id.welcome_message);
		welcomeMsg.setText(Html.fromHtml(
						  "<br>"
						+ "Welcome! This app is designed to increase your happiness and boost positive mental states.<br><br>"
						+ "Messages are displayed throughout your day to aid you in focusing on positive qualities for 20 seconds. Because of the continuity of this practice, over time you will begin to notice your mind <i>automatically seeking out more happiness</i>.<br><br>"
						//+ "This technique is based on buddhist theory and meditation. Insight practice such as vipassana allows you to remove sticky difficult habits, objectify constricted mental states and ultimately a freedom from a root belief in any permanent lasting &apos;self&apos;. The apposing practice is the active building up of a self, with positive mental states. In this practice you develop true appreciation for life, whatever the circumstances, as love and gratitude deepen. And it can be done any time - <i>without</i> thousands of hours on the cushion.<br><br>"
						+ "In this practice you develop a true appreciation for life, whatever the circumstances, as love and gratitude deepen. And it can be done any time - <i>without</i> thousands of hours meditating on the cushion.<br><br>"
						+ "<a href='https://www.google.com/search?q=negativity+bias+studies&hl=en&authuser=0&gws_rd=ssl'>Recent studies in neuroscience</a> show that negative memories make an impression that are <i>five times stronger</i> than positive ones. To make a <i>lasting</i> positive neural connection, it takes about <b>twenty seconds</b> of continuous attention. What you'll discover is that this is the amount of time needed to change your mood.<br><br>"
						+ "For some sessions of this practice you'll feel an immediate impact, but like any practice - the key is long term <i>consistency</i>. By setting up notifications when you <b>want</b> them to appear and by making a commitment to <b>actually</b> take a break to focus on these good qualities - you <i>will</i> become a more positive person!<br>"
				));
		welcomeMsg.setMovementMethod(LinkMovementMethod.getInstance());
		return rootView;
	}
}
