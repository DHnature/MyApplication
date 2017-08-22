package com.example.administrator.rainmusic.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;


public class MyPagerAdapter extends FragmentPagerAdapter {
	  
	private ArrayList<Fragment> fragmentlist;
    
	public MyPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragmentList2) {
		super(fm);
		fragmentlist=fragmentList2;
		
		
	}

	

	@Override
	public int getCount() {
		return fragmentlist.size();
	}





	public Fragment getItem(int arg0) {
		return fragmentlist.get(arg0);
		
	}
   
}

