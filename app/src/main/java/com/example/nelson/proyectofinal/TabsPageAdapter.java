package com.example.nelson.proyectofinal;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

class TabsPageAdapter extends FragmentPagerAdapter{

    public TabsPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                RequestsFragment requestsFragment =  new RequestsFragment();
                return requestsFragment;

            case 1:
                ChatsFragment chatsFragment = new ChatsFragment();
                return chatsFragment;


            case 2:
                FriendsFragment friendsFragment =  new FriendsFragment();
                return friendsFragment;

            case 3:
                UsersFragment usersFragment =new UsersFragment();
                return usersFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }


    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "Requests";
            case 1:
                return "Chats";

            case 2:
                return "Friends";

            case 3:
                return "Users";

            default:
                return null;
        }
    }
}
