package com.example.pong_game;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class StoreActivity extends FragmentActivity {
	// Reference to the view that will hold the store's pages
	ViewPager myViewPager;
	StoreCollectionPagerAdapter myStoreCollectionAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Erase the title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Make it full Screen
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		// Set content view of the viewPager from the XML
		setContentView(R.layout.store_swipe_view);
		// User the helper inner class to get a adapter
		myStoreCollectionAdapter = new StoreCollectionPagerAdapter(
				getSupportFragmentManager());
		// find the ViewPager
		myViewPager = (ViewPager) findViewById(R.id.storeSwiper);
		// Attach the instantiated adapted on the ViewPager
		myViewPager.setAdapter(myStoreCollectionAdapter);
		// ViewPager and the adapters that handle it use support library
		// for fragments

	}

}

// This is a collection of non-predetermined objects
// so this means you can use the FragmentStatePagerAdapter
// in conjunction with the ViewPager in order to represent
// each fragment as a view for each page in the scrolling view
// Needs to be passed in a fragment manager
class StoreCollectionPagerAdapter extends FragmentStatePagerAdapter {
	public StoreCollectionPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	// Returns a fragment of the page to be in the view
	@Override
	public android.support.v4.app.Fragment getItem(int i) {
		//Use Switch to assign cases to each item.
		//Where to store the data containing all the information for a store page,
		//in application, but using what data structure
		storeItemFragment fragment;
		switch(i){
		
	//	case 1: frag
		
		
		
		
		default: fragment = new storeItemFragment();
		
		}
		
		
		
		return fragment;
	}

	@Override
	public int getCount() {
		return 3;
	}

}

// Inside the activity that will host the fragment
// create an inner fragment class that represents one
// instance (item/page)
@SuppressLint("ValidFragment")
class storeItemFragment extends android.support.v4.app.Fragment {
	// Cost of Pts... int
	int cost;
	// item Description ...string
	String description;
	// Bitmap image of the item... string
	Bitmap image;
	// Name of the item... string
	String name;

	// Get the views from the layout
	TextView costView;
	TextView descriptionView;
	ImageView storeImage;

	/*
	 * The primary constructor used for populating the store with the
	 * appropriate images and items.
	 */
	public storeItemFragment(int cost, String description, Bitmap image,
			String name) {
		// initialize with parameters
		this.cost = cost;
		this.description = description;
		this.image = image;
		this.name = name;
	}

	public storeItemFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Set the title of the fragment, to be filled in with name parameter
		getActivity().getActionBar().setTitle("NOTHING FOR NOW");
		// rootView layout should not have XML set images or text, but it should
		// have
		// fields to set them
		View rootView = inflater.inflate(R.layout.fragment_store_view,
				container, false);
		// ***Set images. description, cost, heres
		/*
		 * 
		 * 
		 * 
		 * costView= (TextView)rootView.findViewById(R.id.costText);
		 * descriptionView= (TextView)rootView.findViewById(R.id.storeDescrip);
		 * storeImage= (ImageView)rootView.findViewById(R.id.storeImage);
		 * 
		 * costView.append(String.valueOf(cost));
		 * descriptionView.append(description);
		 * storeImage.setImageBitmap(image);
		 */
		return rootView;
	}

}
